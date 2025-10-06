package com.rental.saas.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rental.api.pricing.PriceClient;
import com.rental.api.pricing.dto.QuoteDetailResponse;
import com.rental.saas.common.enums.OrderStatus;
import com.rental.saas.common.enums.PickupType;
import com.rental.saas.common.exception.BusinessException;
import com.rental.saas.order.dto.CreateOrderRequest;
import com.rental.saas.order.entity.Order;
import com.rental.saas.order.entity.OrderStatusLog;
import com.rental.saas.order.mapper.OrderMapper;
import com.rental.saas.order.mapper.OrderStatusLogMapper;
import com.rental.saas.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单服务实现类
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderStatusLogMapper orderStatusLogMapper;
    private final PriceClient pricingClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(CreateOrderRequest request) {
        log.info("创建订单: quoteId={}", request.getQuoteId());
        
        try {
            // 从定价服务获取报价详情
            QuoteDetailResponse quoteDetail = getQuoteDetailFromPricingService(request.getQuoteId());
            
            // 创建订单实体
            Order order = new Order();
            
            // 生成订单号
            String orderNo = generateOrderNo();
            order.setOrderNo(orderNo);
            
            // 设置订单基本信息
            order.setDriverName(request.getDriverName());
            order.setDriverIdCard(request.getDriverIdCard());
            order.setDriverPhone(request.getDriverPhone());
            order.setStartTime(request.getStartTime());
            order.setEndTime(request.getEndTime());
            order.setOrderLocation(request.getOrderLocation());
            order.setUserId(request.getUserId());
            int pickupType = PickupType.getByDesc(quoteDetail.getQuote().getDeliveryType()).getCode();
            order.setPickupType(pickupType);
            order.setReturnType(pickupType);
            
            // 设置租户信息
            if (quoteDetail.getQuote() != null) {
                order.setTenantId(quoteDetail.getQuote().getTenantId()); // 使用门店ID作为租户ID
            }
            
            // 设置商品和车型信息
            if (quoteDetail.getQuote() != null) {
                order.setCarModelId(quoteDetail.getQuote().getModelId());
                order.setProductId(quoteDetail.getQuote().getProductId());
                order.setPickupStoreId(quoteDetail.getQuote().getStoreId());
                order.setReturnStoreId(quoteDetail.getQuote().getStoreId());
            }
            
            // 设置金额信息（从元转换为分）
            order.setBasicRentalFee(request.getTotalAmount() * 100);
            order.setOrderAmount(request.getTotalAmount() * 100);
            order.setDamageDeposit(request.getDamageDeposit() * 100);
            order.setViolationDeposit(request.getViolationDeposit() * 100);
            
            // 设置初始状态
            order.setOrderStatus(OrderStatus.PENDING_PAYMENT.getCode());
            order.setCreateTime(LocalDateTime.now());
            
            // 设置押金信息
            int actualDeposit = Math.max(
                request.getDamageDeposit() * 100,
                request.getViolationDeposit() * 100
            );
            order.setActualDeposit(actualDeposit);
            
            // 保存快照信息
            saveSnapshots(order, quoteDetail);
            
            // 保存订单
            orderMapper.insert(order);
            
            // 记录状态变更日志
            recordStatusChange(order.getId(), null, OrderStatus.PENDING_PAYMENT, "创建订单", 0L, "系统");
            
            log.info("订单创建成功: orderId={}, orderNo={}", order.getId(), orderNo);
            return order;
        } catch (Exception e) {
            log.error("创建订单失败: quoteId={}, error={}", request.getQuoteId(), e.getMessage(), e);
            throw new BusinessException("创建订单失败: " + e.getMessage());
        }
    }

    /**
     * 从定价服务获取报价详情
     */
    private QuoteDetailResponse getQuoteDetailFromPricingService(String quoteId) {
        try {
            QuoteDetailResponse response =
                pricingClient.getQuoteDetail(quoteId);
            
            if (response != null) {
                return response;
            } else {
                throw new BusinessException("无法获取报价详情");
            }
        } catch (Exception e) {
            log.error("调用定价服务获取报价详情失败: quoteId={}, error={}", quoteId, e.getMessage(), e);
            throw new BusinessException("获取报价详情失败: " + e.getMessage());
        }
    }

    /**
     * 保存快照信息
     */
    private void saveSnapshots(Order order, QuoteDetailResponse quoteDetail) {
        try {
            // 保存增值服务快照
            if (quoteDetail.getVasTemplates() != null) {
                order.setVasSnapshot(objectMapper.writeValueAsString(quoteDetail.getVasTemplates()));
            }
            
            // 保存取消规则快照
            if (quoteDetail.getCancellationPolicy() != null) {
                order.setCancellationRuleSnapshot(objectMapper.writeValueAsString(quoteDetail.getCancellationPolicy()));
            }
            
            // 保存服务政策快照
            if (quoteDetail.getServicePolicy() != null) {
                order.setServicePolicySnapshot(objectMapper.writeValueAsString(quoteDetail.getServicePolicy()));
            }
        } catch (Exception e) {
            log.warn("保存快照信息失败: {}", e.getMessage(), e);
            // 快照保存失败不影响订单创建
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long id, Long operatorId, String operatorName) {
        log.info("取消订单: id={}", id);
        
        try {
            // 查询订单
            Order order = orderMapper.selectById(id);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            // 检查订单状态是否可以取消
            OrderStatus currentStatus = OrderStatus.getByCode(order.getOrderStatus());
            if (!currentStatus.canCancel()) {
                throw new BusinessException("当前订单状态不支持取消");
            }
            
            // 更新订单状态
            OrderStatus newStatus = OrderStatus.CANCELLED;
            order.setOrderStatus(newStatus.getCode());
            order.setCancelTime(LocalDateTime.now());
            orderMapper.updateById(order);
            
            // 记录状态变更日志
            recordStatusChange(id, currentStatus, newStatus, "用户取消订单", operatorId, operatorName);
            
            log.info("订单取消成功: id={}", id);
            return true;
        } catch (Exception e) {
            log.error("取消订单失败: id={}, error={}", id, e.getMessage(), e);
            throw new BusinessException("取消订单失败: " + e.getMessage());
        }
    }

    /**
     * 生成订单号
     * 格式: R + yyyyMMdd + 6位随机数
     */
    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%06d", (int) (Math.random() * 1000000));
        return "R" + dateStr + randomStr;
    }

    @Override
    public IPage<Order> getOrdersByTenantId(Long tenantId, Integer current, Integer size, String orderNo, Integer status) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getTenantId, tenantId);
        queryWrapper.eq(Order::getDeleted, 0);
        
        if (orderNo != null && !orderNo.isEmpty()) {
            queryWrapper.like(Order::getOrderNo, orderNo);
        }
        
        if (status != null) {
            queryWrapper.eq(Order::getOrderStatus, status);
        }
        
        queryWrapper.orderByDesc(Order::getCreateTime);
        
        Page<Order> page = new Page<>(current, size);
        return orderMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<Order> getOrdersByUserId(Long userId, Integer current, Integer size, String orderNo, Integer status) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId);
        queryWrapper.eq(Order::getDeleted, 0);
        
        if (orderNo != null && !orderNo.isEmpty()) {
            queryWrapper.like(Order::getOrderNo, orderNo);
        }
        
        if (status != null) {
            queryWrapper.eq(Order::getOrderStatus, status);
        }
        
        queryWrapper.orderByDesc(Order::getCreateTime);
        
        Page<Order> page = new Page<>(current, size);
        return orderMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Order getOrderDetail(Long id) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getId, id);
        queryWrapper.eq(Order::getDeleted, 0);
        
        return orderMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPickupDriver(Long id, Long tenantId, String driverName, Long operatorId, String operatorName) {
        // 查询订单
        Order order = getOrderDetail(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查订单状态是否可以分配取车司机
        OrderStatus currentStatus = OrderStatus.getByCode(order.getOrderStatus());
        if (!currentStatus.canPickup()) {
            throw new BusinessException("当前订单状态不支持分配取车司机");
        }
        
        // 更新取车司机
        order.setPickupDriver(driverName);
        orderMapper.updateById(order);
        
        // 记录日志
        recordStatusChange(id, currentStatus, currentStatus, "分配取车司机: " + driverName, operatorId, operatorName);
        
        log.info("成功为订单{}分配取车司机: {}", id, driverName);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignReturnDriver(Long id, Long tenantId, String driverName, Long operatorId, String operatorName) {
        // 查询订单
        Order order = getOrderDetail(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查订单状态是否可以分配还车司机
        OrderStatus currentStatus = OrderStatus.getByCode(order.getOrderStatus());
        if (!currentStatus.canReturn() && currentStatus != OrderStatus.PICKED_UP) {
            throw new BusinessException("当前订单状态不支持分配还车司机");
        }
        
        // 更新还车司机
        order.setReturnDriver(driverName);
        orderMapper.updateById(order);
        
        // 记录日志
        recordStatusChange(id, currentStatus, currentStatus, "分配还车司机: " + driverName, operatorId, operatorName);
        
        log.info("成功为订单{}分配还车司机: {}", id, driverName);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmPickup(Long id, Long tenantId, Long operatorId, String operatorName) {
        // 查询订单
        Order order = getOrderDetail(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查订单状态是否可以确认取车
        OrderStatus currentStatus = OrderStatus.getByCode(order.getOrderStatus());
        if (!currentStatus.canPickup()) {
            throw new BusinessException("当前订单状态不支持确认取车");
        }
        
        // 更新订单状态
        OrderStatus newStatus = OrderStatus.PICKED_UP;
        order.setOrderStatus(newStatus.getCode());
        order.setActualPickupTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // 记录状态变更日志
        recordStatusChange(id, currentStatus, newStatus, "确认取车", operatorId, operatorName);
        
        log.info("成功确认订单{}取车", id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmReturn(Long id, Long tenantId, Long operatorId, String operatorName) {
        // 查询订单
        Order order = getOrderDetail(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查订单状态是否可以确认还车
        OrderStatus currentStatus = OrderStatus.getByCode(order.getOrderStatus());
        if (!currentStatus.canReturn()) {
            throw new BusinessException("当前订单状态不支持确认还车");
        }
        
        // 更新订单状态
        OrderStatus newStatus = OrderStatus.COMPLETED;
        order.setOrderStatus(newStatus.getCode());
        order.setActualReturnTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // 记录状态变更日志
        recordStatusChange(id, currentStatus, newStatus, "确认还车", operatorId, operatorName);
        
        log.info("成功确认订单{}还车", id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePaymentSuccess(Long orderId, Integer paymentType) {
        log.info("处理支付成功回调: orderId={}, paymentType={}", orderId, paymentType);
        
        try {
            // 查询订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                log.warn("订单不存在: orderId={}", orderId);
                return false;
            }
            
            // 获取当前订单状态
            OrderStatus currentStatus = OrderStatus.getByCode(order.getOrderStatus());
            
            // 根据支付类型处理不同的逻辑
            if (paymentType == 1) {
                // 租车费支付成功，更新订单状态为待取车
                if (currentStatus == OrderStatus.PENDING_PAYMENT) {
                    order.setOrderStatus(OrderStatus.PENDING_PICKUP.getCode());
                    orderMapper.updateById(order);
                    
                    // 记录状态变更日志
                    recordStatusChange(orderId, currentStatus, OrderStatus.PENDING_PICKUP, 
                                     "租车费支付成功", 0L, "系统");
                    log.info("租车费支付成功，订单状态更新为待取车: orderId={}", orderId);
                }
            } else if (paymentType == 2) {
                order.setIsDepositPaid(1);
                orderMapper.updateById(order);
                // 押金支付成功，目前不需要更新订单状态，但可以记录日志
                log.info("押金支付成功: orderId={}", orderId);
            }
            
            return true;
        } catch (Exception e) {
            log.error("处理支付成功回调失败: orderId={}, paymentType={}, error={}", orderId, paymentType, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void recordStatusChange(Long orderId, OrderStatus oldStatus, OrderStatus newStatus, 
                                 String changeReason, Long operatorId, String operatorName) {
        OrderStatusLog logEntry = new OrderStatusLog();
        logEntry.setOrderId(orderId);
        logEntry.setOldStatus(oldStatus != null ? oldStatus.getCode() : null);
        logEntry.setNewStatus(newStatus.getCode());
        logEntry.setChangeReason(changeReason);
        logEntry.setOperatorId(operatorId);
        logEntry.setOperatorName(operatorName);
        logEntry.setChangeTime(LocalDateTime.now());
        
        orderStatusLogMapper.insert(logEntry);
    }
}
package com.rental.saas.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.saas.common.enums.OrderStatus;
import com.rental.saas.common.exception.BusinessException;
import com.rental.saas.order.entity.Order;
import com.rental.saas.order.entity.OrderStatusLog;
import com.rental.saas.order.mapper.OrderMapper;
import com.rental.saas.order.mapper.OrderStatusLogMapper;
import com.rental.saas.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Order getOrderDetail(Long id, Long tenantId) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getId, id);
        queryWrapper.eq(Order::getTenantId, tenantId);
        queryWrapper.eq(Order::getDeleted, 0);
        
        return orderMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPickupDriver(Long id, Long tenantId, String driverName, Long operatorId, String operatorName) {
        // 查询订单
        Order order = getOrderDetail(id, tenantId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查订单状态是否可以分配取车司机
        OrderStatus currentStatus = order.getOrderStatusEnum();
        if (!currentStatus.canPickup()) {
            throw new BusinessException("当前订单状态不支持分配取车司机");
        }
        
        // 更新取车司机
        order.setPickupDriver(driverName);
        order.setUpdatedBy(operatorId);
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
        Order order = getOrderDetail(id, tenantId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查订单状态是否可以分配还车司机
        OrderStatus currentStatus = order.getOrderStatusEnum();
        if (!currentStatus.canReturn() && currentStatus != OrderStatus.PICKED_UP) {
            throw new BusinessException("当前订单状态不支持分配还车司机");
        }
        
        // 更新还车司机
        order.setReturnDriver(driverName);
        order.setUpdatedBy(operatorId);
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
        Order order = getOrderDetail(id, tenantId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查订单状态是否可以确认取车
        OrderStatus currentStatus = order.getOrderStatusEnum();
        if (!currentStatus.canPickup()) {
            throw new BusinessException("当前订单状态不支持确认取车");
        }
        
        // 更新订单状态
        OrderStatus newStatus = OrderStatus.PICKED_UP;
        order.setOrderStatus(newStatus);
        order.setActualPickupTime(LocalDateTime.now());
        order.setUpdatedBy(operatorId);
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
        Order order = getOrderDetail(id, tenantId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查订单状态是否可以确认还车
        OrderStatus currentStatus = order.getOrderStatusEnum();
        if (!currentStatus.canReturn()) {
            throw new BusinessException("当前订单状态不支持确认还车");
        }
        
        // 更新订单状态
        OrderStatus newStatus = OrderStatus.COMPLETED;
        order.setOrderStatus(newStatus);
        order.setActualReturnTime(LocalDateTime.now());
        order.setUpdatedBy(operatorId);
        orderMapper.updateById(order);
        
        // 记录状态变更日志
        recordStatusChange(id, currentStatus, newStatus, "确认还车", operatorId, operatorName);
        
        log.info("成功确认订单{}还车", id);
        return true;
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
        logEntry.setCreatedBy(operatorId);
        
        orderStatusLogMapper.insert(logEntry);
    }
}
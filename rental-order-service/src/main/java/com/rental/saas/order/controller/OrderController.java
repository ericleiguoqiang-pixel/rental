package com.rental.saas.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import com.rental.saas.order.dto.CreateOrderRequest;
import com.rental.saas.order.entity.Order;
import com.rental.saas.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 * 提供商户平台订单查询和操作功能
 *
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@Tag(name = "商户订单接口", description = "商户平台订单管理相关接口")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    @Operation(summary = "创建订单", description = "创建新的租车订单")
    public ApiResponse<Order> createOrder(
            @Parameter(description = "创建订单请求参数") @RequestBody CreateOrderRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        log.info("创建订单: quoteId={}", request.getQuoteId());
        
        try {
            // 设置用户ID
            request.setUserId(userId);
            
            Order order = orderService.createOrder(request);
            log.info("订单创建成功: orderId={}", order.getId());
            return ApiResponse.success(order);
        } catch (Exception e) {
            log.error("创建订单失败: quoteId={}, error={}", request.getQuoteId(), e.getMessage(), e);
            return ApiResponse.error("创建订单失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取订单列表", description = "分页获取订单列表")
    public ApiResponse<PageResponse<Order>> getOrders(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "订单号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        
        log.info("获取订单列表: tenantId={}, current={}, size={}, orderNo={}, status={}", 
                tenantId, current, size, orderNo, status);
        
        try {
            IPage<Order> page = orderService.getOrdersByTenantId(tenantId, current, size, orderNo, status);
            PageResponse<Order> response = PageResponse.of(page);
            log.info("获取订单列表成功，共{}条记录", page.getTotal());
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("获取订单列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取订单列表失败");
        }
    }

    /**
     * 根据用户ID获取订单列表（分页）
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID获取订单列表", description = "根据用户ID分页获取订单列表")
    public ApiResponse<PageResponse<Order>> getOrdersByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "订单号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status) {
        
        log.info("根据用户ID获取订单列表: userId={}, current={}, size={}, orderNo={}, status={}", 
                userId, current, size, orderNo, status);
        
        try {
            IPage<Order> page = orderService.getOrdersByUserId(userId, current, size, orderNo, status);
            PageResponse<Order> response = PageResponse.of(page);
            log.info("根据用户ID获取订单列表成功，共{}条记录", page.getTotal());
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("根据用户ID获取订单列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("根据用户ID获取订单列表失败");
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据ID获取订单详情")
    public ApiResponse<Order> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        
        log.info("获取订单详情: id={}, tenantId={}", id);
        
        try {
            Order order = orderService.getOrderDetail(id);
            if (order == null) {
                log.warn("订单不存在: id={}", id);
                return ApiResponse.error("订单不存在");
            }
            log.info("获取订单详情成功: id={}", id);
            return ApiResponse.success(order);
        } catch (Exception e) {
            log.error("获取订单详情失败: id={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("获取订单详情失败");
        }
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "取消订单")
    public ApiResponse<Boolean> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestHeader(value = "X-Operator-Id", required = false) Long operatorId,
            @RequestHeader(value = "X-Operator-Name", required = false) String operatorName) {
        
        log.info("取消订单: id={}", id);
        
        try {
            if (operatorId == null) operatorId = 0L;
            if (operatorName == null) operatorName = "未知用户";
            
            boolean result = orderService.cancelOrder(id, operatorId, operatorName);
            log.info("取消订单成功: id={}", id);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("取消订单失败: id={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("取消订单失败: " + e.getMessage());
        }
    }

    /**
     * 分配取车司机
     */
    @PutMapping("/{id}/pickup-driver")
    @Operation(summary = "分配取车司机", description = "为订单分配取车司机")
    public ApiResponse<Boolean> assignPickupDriver(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "司机姓名") @RequestParam String driverName,
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @RequestHeader(value = "X-Operator-Id", required = false) Long operatorId,
            @RequestHeader(value = "X-Operator-Name", required = false) String operatorName) {
        
        log.info("分配取车司机: id={}, tenantId={}, driverName={}", id, tenantId, driverName);
        
        try {
            if (operatorId == null) operatorId = 0L;
            if (operatorName == null) operatorName = "未知操作员";
            
            boolean result = orderService.assignPickupDriver(id, tenantId, driverName, operatorId, operatorName);
            log.info("分配取车司机成功: id={}", id);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("分配取车司机失败: id={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("分配取车司机失败: " + e.getMessage());
        }
    }

    /**
     * 分配还车司机
     */
    @PutMapping("/{id}/return-driver")
    @Operation(summary = "分配还车司机", description = "为订单分配还车司机")
    public ApiResponse<Boolean> assignReturnDriver(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "司机姓名") @RequestParam String driverName,
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @RequestHeader(value = "X-Operator-Id", required = false) Long operatorId,
            @RequestHeader(value = "X-Operator-Name", required = false) String operatorName) {
        
        log.info("分配还车司机: id={}, tenantId={}, driverName={}", id, tenantId, driverName);
        
        try {
            if (operatorId == null) operatorId = 0L;
            if (operatorName == null) operatorName = "未知操作员";
            
            boolean result = orderService.assignReturnDriver(id, tenantId, driverName, operatorId, operatorName);
            log.info("分配还车司机成功: id={}", id);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("分配还车司机失败: id={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("分配还车司机失败: " + e.getMessage());
        }
    }

    /**
     * 确认取车
     */
    @PutMapping("/{id}/confirm-pickup")
    @Operation(summary = "确认取车", description = "确认订单取车")
    public ApiResponse<Boolean> confirmPickup(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @RequestHeader(value = "X-Operator-Id", required = false) Long operatorId,
            @RequestHeader(value = "X-Operator-Name", required = false) String operatorName) {
        
        log.info("确认取车: id={}, tenantId={}", id, tenantId);
        
        try {
            if (operatorId == null) operatorId = 0L;
            if (operatorName == null) operatorName = "未知操作员";
            
            boolean result = orderService.confirmPickup(id, tenantId, operatorId, operatorName);
            log.info("确认取车成功: id={}", id);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("确认取车失败: id={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("确认取车失败: " + e.getMessage());
        }
    }

    /**
     * 确认还车
     */
    @PutMapping("/{id}/confirm-return")
    @Operation(summary = "确认还车", description = "确认订单还车")
    public ApiResponse<Boolean> confirmReturn(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @RequestHeader(value = "X-Operator-Id", required = false) Long operatorId,
            @RequestHeader(value = "X-Operator-Name", required = false) String operatorName) {
        
        log.info("确认还车: id={}, tenantId={}", id, tenantId);
        
        try {
            if (operatorId == null) operatorId = 0L;
            if (operatorName == null) operatorName = "未知操作员";
            
            boolean result = orderService.confirmReturn(id, tenantId, operatorId, operatorName);
            log.info("确认还车成功: id={}", id);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("确认还车失败: id={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("确认还车失败: " + e.getMessage());
        }
    }

    /**
     * 处理支付成功回调
     */
    @PutMapping("/{id}/payment-success")
    @Operation(summary = "处理支付成功回调", description = "处理支付成功回调并更新订单状态")
    public ApiResponse<Boolean> handlePaymentSuccess(
            @Parameter(description = "订单ID") @PathVariable("id") Long id,
            @Parameter(description = "支付类型:1-租车费,2-押金") @RequestParam("paymentType") Integer paymentType) {
        
        log.info("处理支付成功回调: orderId={}, paymentType={}", id, paymentType);
        
        try {
            boolean result = orderService.handlePaymentSuccess(id, paymentType);
            log.info("处理支付成功回调完成: orderId={}, paymentType={}, result={}", id, paymentType, result);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("处理支付成功回调失败: orderId={}, paymentType={}, error={}", id, paymentType, e.getMessage(), e);
            return ApiResponse.error("处理支付成功回调失败: " + e.getMessage());
        }
    }
    
    /**
     * 统计租户今日订单数
     */
    @GetMapping("/count/today")
    @Operation(summary = "统计租户今日订单数", description = "统计指定租户今日订单数")
    public ApiResponse<Integer> countTodayOrdersByTenantId(
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        
        log.info("统计租户今日订单数: tenantId={}", tenantId);
        
        try {
            int count = orderService.countTodayOrdersByTenantId(tenantId);
            log.info("统计租户今日订单数成功: tenantId={}, count={}", tenantId, count);
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.error("统计租户今日订单数失败: tenantId={}, error={}", tenantId, e.getMessage(), e);
            return ApiResponse.error("统计租户今日订单数失败: " + e.getMessage());
        }
    }
    
    /**
     * 统计租户今日收入
     */
    @GetMapping("/revenue/today")
    @Operation(summary = "统计租户今日收入", description = "统计指定租户今日收入")
    public ApiResponse<Double> sumTodayRevenueByTenantId(
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        
        log.info("统计租户今日收入: tenantId={}", tenantId);
        
        try {
            double revenue = orderService.sumTodayRevenueByTenantId(tenantId);
            log.info("统计租户今日收入成功: tenantId={}, revenue={}", tenantId, revenue);
            return ApiResponse.success(revenue);
        } catch (Exception e) {
            log.error("统计租户今日收入失败: tenantId={}, error={}", tenantId, e.getMessage(), e);
            return ApiResponse.error("统计租户今日收入失败: " + e.getMessage());
        }
    }
}
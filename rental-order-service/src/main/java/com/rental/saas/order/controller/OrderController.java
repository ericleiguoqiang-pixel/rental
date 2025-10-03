package com.rental.saas.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
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
     * 获取订单详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据ID获取订单详情")
    public ApiResponse<Order> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        
        log.info("获取订单详情: id={}, tenantId={}", id, tenantId);
        
        try {
            Order order = orderService.getOrderDetail(id, tenantId);
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
}
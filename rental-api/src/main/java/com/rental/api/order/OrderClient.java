package com.rental.api.order;

import com.rental.saas.common.response.ApiResponse;
import com.rental.api.order.request.CreateOrderRequest;
import com.rental.api.order.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 订单服务Feign客户端
 * 
 * @author Rental SaaS Team
 */
@FeignClient(name = "rental-order-service", path = "/api")
public interface OrderClient {

    /**
     * 创建订单
     * 
     * @param request 创建订单请求参数
     * @return 订单信息
     */
    @PostMapping("/orders")
    ApiResponse<OrderResponse> createOrder(@RequestBody CreateOrderRequest request);

    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @return 是否成功
     */
    @PutMapping("/orders/{orderId}/cancel")
    ApiResponse<Boolean> cancelOrder(@PathVariable("orderId") Long orderId);

    /**
     * 获取订单详情
     * 
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/orders/{orderId}")
    ApiResponse<OrderResponse> getOrderDetail(@PathVariable("orderId") Long orderId);

    /**
     * 根据用户ID获取订单列表
     * 
     * @param userId 用户ID
     * @param current 当前页码
     * @param size 每页条数
     * @param orderNo 订单号（可选）
     * @param status 订单状态（可选）
     * @return 订单列表
     */
    @GetMapping("/orders/user/{userId}")
    ApiResponse<com.rental.saas.common.response.PageResponse<OrderResponse>> getOrdersByUserId(
        @PathVariable("userId") Long userId,
        @RequestParam(value = "current", defaultValue = "1") Integer current,
        @RequestParam(value = "size", defaultValue = "10") Integer size,
        @RequestParam(value = "orderNo", required = false) String orderNo,
        @RequestParam(value = "status", required = false) Integer status);

    /**
     * 处理支付成功回调
     * 
     * @param orderId 订单ID
     * @param paymentType 支付类型:1-租车费,2-押金
     * @return 是否成功
     */
    @PutMapping("/orders/{orderId}/payment-success")
    ApiResponse<Boolean> handlePaymentSuccess(
        @PathVariable("orderId") Long orderId,
        @RequestParam("paymentType") Integer paymentType);
}
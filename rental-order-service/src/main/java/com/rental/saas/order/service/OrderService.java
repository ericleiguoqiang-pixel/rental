package com.rental.saas.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.saas.common.enums.OrderStatus;
import com.rental.saas.order.entity.Order;
import com.rental.saas.order.entity.OrderStatusLog;

/**
 * 订单服务接口
 * 
 * @author Rental SaaS Team
 */
public interface OrderService extends IService<Order> {

    /**
     * 分页查询订单列表
     * 
     * @param tenantId 租户ID
     * @param current 当前页码
     * @param size 每页条数
     * @param orderNo 订单号（可选）
     * @param status 订单状态（可选）
     * @return 订单分页数据
     */
    IPage<Order> getOrdersByTenantId(Long tenantId, Integer current, Integer size, String orderNo, Integer status);

    /**
     * 根据ID获取订单详情
     * 
     * @param id 订单ID
     * @param tenantId 租户ID
     * @return 订单详情
     */
    Order getOrderDetail(Long id, Long tenantId);

    /**
     * 分配取车司机
     * 
     * @param id 订单ID
     * @param tenantId 租户ID
     * @param driverName 司机姓名
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 是否成功
     */
    boolean assignPickupDriver(Long id, Long tenantId, String driverName, Long operatorId, String operatorName);

    /**
     * 分配还车司机
     * 
     * @param id 订单ID
     * @param tenantId 租户ID
     * @param driverName 司机姓名
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 是否成功
     */
    boolean assignReturnDriver(Long id, Long tenantId, String driverName, Long operatorId, String operatorName);

    /**
     * 确认取车
     * 
     * @param id 订单ID
     * @param tenantId 租户ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 是否成功
     */
    boolean confirmPickup(Long id, Long tenantId, Long operatorId, String operatorName);

    /**
     * 确认还车
     * 
     * @param id 订单ID
     * @param tenantId 租户ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 是否成功
     */
    boolean confirmReturn(Long id, Long tenantId, Long operatorId, String operatorName);

    /**
     * 记录订单状态变更日志
     * 
     * @param orderId 订单ID
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @param changeReason 变更原因
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     */
    void recordStatusChange(Long orderId, OrderStatus oldStatus, OrderStatus newStatus, String changeReason, 
                           Long operatorId, String operatorName);
}
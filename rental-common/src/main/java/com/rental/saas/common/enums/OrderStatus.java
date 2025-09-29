package com.rental.saas.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举
 * 
 * @author Rental SaaS Team
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING_PAYMENT(1, "待支付"),
    PENDING_PICKUP(2, "待取车"),
    PICKED_UP(3, "已取车"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消");

    private final Integer code;
    private final String description;

    public static OrderStatus getByCode(Integer code) {
        for (OrderStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否可以取消
     */
    public boolean canCancel() {
        return this == PENDING_PAYMENT || this == PENDING_PICKUP;
    }

    /**
     * 判断是否可以确认取车
     */
    public boolean canPickup() {
        return this == PENDING_PICKUP;
    }

    /**
     * 判断是否可以确认还车
     */
    public boolean canReturn() {
        return this == PICKED_UP;
    }
}
package com.rental.saas.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付状态枚举
 * 
 * @author Rental SaaS Team
 */
@Getter
@AllArgsConstructor
public enum PaymentStatus {

    PENDING(1, "待支付"),
    SUCCESS(2, "支付成功"),
    FAILED(3, "支付失败"),
    REFUNDED(4, "已退款");

    private final Integer code;
    private final String description;

    public static PaymentStatus getByCode(Integer code) {
        for (PaymentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否可以退款
     */
    public boolean canRefund() {
        return this == SUCCESS;
    }
}
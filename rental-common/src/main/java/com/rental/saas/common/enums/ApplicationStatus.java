package com.rental.saas.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商户申请状态枚举
 * 
 * @author Rental SaaS Team
 */
@Getter
@AllArgsConstructor
public enum ApplicationStatus {

    PENDING(0, "待审核"),
    APPROVED(1, "审核通过"),
    REJECTED(2, "审核拒绝");

    private final Integer code;
    private final String description;

    public static ApplicationStatus getByCode(Integer code) {
        for (ApplicationStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
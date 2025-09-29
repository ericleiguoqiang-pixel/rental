package com.rental.saas.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 车辆状态枚举
 * 
 * @author Rental SaaS Team
 */
@Getter
@AllArgsConstructor
public enum VehicleStatus {

    AVAILABLE(1, "空闲"),
    RENTED(2, "租出"),
    MAINTENANCE(3, "维修"),
    SCRAPPED(4, "报废");

    private final Integer code;
    private final String description;

    public static VehicleStatus getByCode(Integer code) {
        for (VehicleStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否可租
     */
    public boolean isAvailable() {
        return this == AVAILABLE;
    }
}
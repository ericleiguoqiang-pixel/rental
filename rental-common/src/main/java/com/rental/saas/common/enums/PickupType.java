package com.rental.saas.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PickupType {
    PICKUP_TYPE_SEND(2, "上门取送车"),
    PICKUP_TYPE_STORE(1, "到店取还");

    private final Integer code;
    private final String description;

    public static PickupType getByCode(Integer code) {
        for (PickupType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    public static PickupType getByDesc(String desc) {
        for (PickupType type : values()) {
            if (type.getDescription().equals(desc)) {
                return type;
            }
        }
        return null;
    }

}

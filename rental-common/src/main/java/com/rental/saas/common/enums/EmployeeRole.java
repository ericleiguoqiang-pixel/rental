package com.rental.saas.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 员工角色类型枚举
 * 
 * @author Rental SaaS Team
 */
@Getter
@AllArgsConstructor
public enum EmployeeRole {

    SUPER_ADMIN(1, "超级管理员"),
    STORE_ADMIN(2, "门店管理员"),
    VEHICLE_ADMIN(3, "车辆管理员"),
    ORDER_ADMIN(4, "订单管理员"),
    EMPLOYEE(5, "普通员工");

    private final Integer code;
    private final String description;

    public static EmployeeRole getByCode(Integer code) {
        for (EmployeeRole role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 是否为管理员角色
     */
    public boolean isAdmin() {
        return this == SUPER_ADMIN || this == STORE_ADMIN;
    }
}
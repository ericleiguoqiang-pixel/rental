package com.rental.saas.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 员工响应DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "员工响应DTO")
public class EmployeeResponse {

    /**
     * 员工ID
     */
    @Schema(description = "员工ID", example = "1")
    private Long id;

    /**
     * 所属门店ID
     */
    @Schema(description = "所属门店ID", example = "1")
    private Long storeId;

    /**
     * 门店名称
     */
    @Schema(description = "门店名称", example = "中关村门店")
    private String storeName;

    /**
     * 员工姓名
     */
    @Schema(description = "员工姓名", example = "张三")
    private String employeeName;

    /**
     * 手机号
     */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 状态：0-禁用，1-启用
     */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /**
     * 状态描述
     */
    @Schema(description = "状态描述", example = "启用")
    private String statusDesc;

    /**
     * 角色类型：1-超级管理员，2-门店管理员，3-车辆管理员，4-订单管理员，5-普通员工
     */
    @Schema(description = "角色类型", example = "2")
    private Integer roleType;

    /**
     * 角色类型描述
     */
    @Schema(description = "角色类型描述", example = "门店管理员")
    private String roleTypeDesc;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后登录时间", example = "2024-01-01 12:00:00")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    @Schema(description = "最后登录IP", example = "192.168.1.100")
    private String lastLoginIp;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createdTime;
}
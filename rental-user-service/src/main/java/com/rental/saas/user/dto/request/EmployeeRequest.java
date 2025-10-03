package com.rental.saas.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 员工请求DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "员工请求DTO")
public class EmployeeRequest {

    /**
     * 所属门店ID
     */
    @Schema(description = "所属门店ID", example = "1")
    private Long storeId;

    /**
     * 员工姓名
     */
    @NotBlank(message = "员工姓名不能为空")
    @Size(max = 50, message = "员工姓名长度不能超过50个字符")
    @Schema(description = "员工姓名", example = "张三")
    private String employeeName;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 50, message = "用户名长度必须在4-50个字符之间")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Schema(description = "密码", example = "123456")
    private String password;

    /**
     * 状态：0-禁用，1-启用
     */
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态", example = "1")
    private Integer status;

    /**
     * 角色类型：1-超级管理员，2-门店管理员，3-车辆管理员，4-订单管理员，5-普通员工
     */
    @NotNull(message = "角色类型不能为空")
    @Schema(description = "角色类型", example = "2")
    private Integer roleType;
}
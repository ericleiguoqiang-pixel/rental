package com.rental.saas.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "登录响应")
public class LoginResponse {

    /**
     * 访问令牌
     */
    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    /**
     * 刷新令牌
     */
    @Schema(description = "刷新令牌", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    /**
     * 令牌类型
     */
    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";

    /**
     * 过期时间(秒)
     */
    @Schema(description = "过期时间", example = "7200")
    private Long expiresIn;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Data
    @Schema(description = "用户信息")
    public static class UserInfo {
        /**
         * 用户ID
         */
        @Schema(description = "用户ID", example = "1")
        private Long userId;

        /**
         * 用户名
         */
        @Schema(description = "用户名", example = "zhangsan")
        private String username;

        /**
         * 员工姓名
         */
        @Schema(description = "员工姓名", example = "张三")
        private String employeeName;

        /**
         * 租户ID
         */
        @Schema(description = "租户ID", example = "1")
        private Long tenantId;

        /**
         * 企业名称
         */
        @Schema(description = "企业名称", example = "某某汽车租赁有限公司")
        private String companyName;

        /**
         * 角色类型
         */
        @Schema(description = "角色类型", example = "2")
        private Integer roleType;

        /**
         * 角色类型描述
         */
        @Schema(description = "角色类型描述", example = "门店管理员")
        private String roleTypeDesc;

        /**
         * 所属门店ID
         */
        @Schema(description = "所属门店ID", example = "1")
        private Long storeId;
    }
}
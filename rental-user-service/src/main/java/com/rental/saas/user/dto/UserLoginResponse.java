package com.rental.saas.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户登录响应DTO
 */
@Data
@Schema(description = "用户登录响应")
public class UserLoginResponse {
    
    @Schema(description = "访问令牌")
    private String token;
    
    @Schema(description = "用户信息")
    private UserInfo userInfo;
    
    /**
     * 用户信息内部类
     */
    @Data
    public static class UserInfo {
        @Schema(description = "用户ID")
        private Long id;
        
        @Schema(description = "手机号")
        private String phone;
        
        @Schema(description = "昵称")
        private String nickname;
        
        @Schema(description = "头像URL")
        private String avatar;
    }
}
package com.rental.saas.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 用户登录请求DTO
 */
@Data
@Schema(description = "用户登录请求")
public class UserLoginRequest {
    
    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号")
    private String phone;
    
    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码")
    private String code;
}
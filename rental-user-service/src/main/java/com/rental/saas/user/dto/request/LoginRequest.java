package com.rental.saas.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户登录请求DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "用户登录请求")
public class LoginRequest {

    /**
     * 用户名/手机号
     */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    @Schema(description = "用户名/手机号", example = "zhangsan", required = true)
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50个字符之间")
    @Schema(description = "密码", example = "123456", required = true)
    private String password;

    /**
     * 图形验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 4, max = 6, message = "验证码长度必须在4-6个字符之间")
    @Schema(description = "图形验证码", example = "ABCD", required = true)
    private String captcha;

    /**
     * 验证码key
     */
    @NotBlank(message = "验证码key不能为空")
    @Schema(description = "验证码key", example = "captcha_key_123", required = true)
    private String captchaKey;
}
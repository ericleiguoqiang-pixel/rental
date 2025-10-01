package com.rental.saas.user.controller;

import com.rental.saas.common.annotation.OperationLog;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.ResponseCode;
import com.rental.saas.user.dto.request.LoginRequest;
import com.rental.saas.user.dto.response.LoginResponse;
import com.rental.saas.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 认证控制器
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "认证管理", description = "用户登录认证相关接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "员工用户登录系统")
    @OperationLog(type = "用户认证", description = "用户登录", saveRequestParams = false)
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse result = authService.login(request);
        return ApiResponse.success("登录成功", result);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    @OperationLog(type = "用户认证", description = "刷新令牌")
    public ApiResponse<LoginResponse> refreshToken(
            @Parameter(description = "刷新令牌") @RequestParam String refreshToken) {
        LoginResponse result = authService.refreshToken(refreshToken);
        return ApiResponse.success("令牌刷新成功", result);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户退出登录")
    @OperationLog(type = "用户认证", description = "用户登出")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authorization) {
        // 提取访问令牌
        String accessToken = authorization.substring(7); // 去掉 "Bearer " 前缀
        authService.logout(accessToken);
        return ApiResponse.success();
    }

    @GetMapping("/captcha")
    @Operation(summary = "获取验证码", description = "获取图形验证码")
    public ApiResponse<String> getCaptcha() {
        String captcha = authService.getCaptcha();
        return ApiResponse.success("验证码获取成功", captcha);
    }

    @PostMapping("/verify")
    @Operation(summary = "验证令牌", description = "验证访问令牌是否有效")
    @OperationLog(type = "用户认证", description = "验证令牌")
    public ApiResponse<Void> verifyToken(@RequestHeader("Authorization") String authorization) {
        // 提取访问令牌
        String accessToken = authorization.substring(7); // 去掉 "Bearer " 前缀
        boolean isValid = authService.verifyToken(accessToken);
        
        if (isValid) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error(ResponseCode.TOKEN_INVALID);
        }
    }
}
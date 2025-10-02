package com.rental.saas.user.controller;

import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.ResponseCode;
import com.rental.saas.user.dto.request.LoginRequest;
import com.rental.saas.user.dto.response.LoginResponse;
import com.rental.saas.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 运营MIS认证控制器
 * 提供运营管理员的登录认证功能
 *
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/operation/auth")
@Tag(name = "运营认证接口", description = "运营MIS系统认证相关接口")
public class OperationAuthController {

    @Autowired
    private AuthService authService;

    /**
     * 运营管理员登录
     * 固定账号：admin/admin
     */
    @PostMapping("/login")
    @Operation(summary = "运营管理员登录", description = "运营管理员使用固定账号密码登录系统")
    public ApiResponse<LoginResponse> operationLogin(@RequestBody LoginRequest loginRequest) {
        log.info("运营管理员登录请求: username={}", loginRequest.getUsername());
        
        // 验证运营管理员账号密码
        if (!"admin".equals(loginRequest.getUsername()) || !"admin".equals(loginRequest.getPassword())) {
            return ApiResponse.error(ResponseCode.UNAUTHORIZED, "用户名或密码错误");
        }
        
        try {
            // 生成运营管理员令牌
            LoginResponse loginResponse = authService.generateOperationToken();
            log.info("运营管理员登录成功: username={}", loginRequest.getUsername());
            return ApiResponse.success(loginResponse);
        } catch (Exception e) {
            log.error("运营管理员登录失败: username={}, error={}", loginRequest.getUsername(), e.getMessage(), e);
            return ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "登录失败");
        }
    }
}
package com.rental.saas.user.controller;

import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.user.dto.UserLoginRequest;
import com.rental.saas.user.dto.UserLoginResponse;
import com.rental.saas.user.entity.User;
import com.rental.saas.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户登录、注册等相关接口")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户使用手机号和验证码登录")
    public ApiResponse<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        log.info("用户登录请求: phone={}", request.getPhone());
        UserLoginResponse response = userService.login(request);
        return ApiResponse.success("登录成功", response);
    }
    
    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的信息")
    public ApiResponse<User> getUserInfo(HttpServletRequest request) {
        // 从请求头中获取用户信息（由网关设置）
        String userId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");
        String tenantId = request.getHeader("X-Tenant-Id");
        
        log.info("获取用户信息请求: userId={}, username={}, tenantId={}", userId, username, tenantId);
        
        // 这里应该根据用户ID查询用户详细信息
        // 为了简化，我们构造一个用户对象返回
        User user = new User();
        user.setId(userId != null ? Long.valueOf(userId) : 0L);
        user.setPhone(username); // 这里简化处理，实际应该查询数据库
        
        return ApiResponse.success(user);
    }
}
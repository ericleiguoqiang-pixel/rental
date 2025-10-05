package com.rental.saas.user.controller;

import com.rental.saas.user.dto.UserLoginRequest;
import com.rental.saas.user.dto.UserLoginResponse;
import com.rental.saas.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
    public UserLoginResponse login(@Valid @RequestBody UserLoginRequest request) {
        log.info("用户登录请求: phone={}", request.getPhone());
        return userService.login(request);
    }
}
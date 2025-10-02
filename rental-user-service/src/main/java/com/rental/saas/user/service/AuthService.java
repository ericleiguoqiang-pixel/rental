package com.rental.saas.user.service;

import com.rental.saas.user.dto.request.LoginRequest;
import com.rental.saas.user.dto.response.LoginResponse;

/**
 * 认证服务接口
 * 
 * @author Rental SaaS Team
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 刷新令牌
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * 用户登出
     */
    void logout(String accessToken);

    /**
     * 获取验证码
     */
    String getCaptcha();

    /**
     * 验证访问令牌
     */
    boolean verifyToken(String accessToken);
    
    /**
     * 生成运营管理员令牌
     */
    LoginResponse generateOperationToken();
}
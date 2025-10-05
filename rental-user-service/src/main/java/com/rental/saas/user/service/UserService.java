package com.rental.saas.user.service;

import com.rental.saas.user.dto.UserLoginRequest;
import com.rental.saas.user.dto.UserLoginResponse;
import com.rental.saas.user.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     * @param request 登录请求参数
     * @return 登录响应
     */
    UserLoginResponse login(UserLoginRequest request);
    
    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户信息
     */
    User findByPhone(String phone);
    
    /**
     * 创建新用户
     * @param phone 手机号
     * @return 用户信息
     */
    User createUser(String phone);
}
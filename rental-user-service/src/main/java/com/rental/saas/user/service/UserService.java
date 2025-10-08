package com.rental.saas.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.saas.user.dto.UserLoginRequest;
import com.rental.saas.user.dto.UserLoginResponse;
import com.rental.saas.user.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
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
    
    /**
     * 分页获取用户列表
     * @param current 当前页码
     * @param size 每页条数
     * @param phone 手机号
     * @param nickname 昵称
     * @param status 状态
     * @return 用户分页数据
     */
    IPage<User> getUserList(Integer current, Integer size, String phone, String nickname, Integer status);
}
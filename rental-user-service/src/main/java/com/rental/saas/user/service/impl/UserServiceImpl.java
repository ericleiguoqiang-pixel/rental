package com.rental.saas.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.saas.common.utils.JwtUtil;
import com.rental.saas.user.dto.UserLoginRequest;
import com.rental.saas.user.dto.UserLoginResponse;
import com.rental.saas.user.entity.User;
import com.rental.saas.user.mapper.UserMapper;
import com.rental.saas.user.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Value("${app.jwt.secret:rental-secret-key}")
    private String jwtSecret;
    
    @Value("${app.jwt.access-token-expiration:86400000}")
    private Long jwtExpiration;

    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户登录
     * @param request 登录请求参数
     * @return 登录响应
     */
    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        // 简单验证验证码（实际项目中应该对接短信服务）
        if (!"1111".equals(request.getCode())) {
            throw new RuntimeException("验证码错误");
        }
        
        // 查找用户，如果不存在则创建
        User user = findByPhone(request.getPhone());
        if (user == null) {
            user = createUser(request.getPhone());
        }

        // 构建令牌声明
        Map<String, Object> claims = new HashMap<>();
        claims.put("employeeName", user.getPhone());

        // 生成访问令牌和刷新令牌
        String accessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getPhone(),
                0L,
                claims
        );
        //String refreshToken = jwtUtil.generateRefreshToken(employee.getId());
        
        // 生成JWT令牌
        //String token = generateToken(user);
        
        // 构造响应
        UserLoginResponse response = new UserLoginResponse();
        response.setToken(accessToken);
        
        UserLoginResponse.UserInfo userInfo = new UserLoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setPhone(user.getPhone());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        
        response.setUserInfo(userInfo);
        
        return response;
    }
    
    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户信息
     */
    @Override
    public User findByPhone(String phone) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        return getOne(queryWrapper);
    }
    
    /**
     * 创建新用户
     * @param phone 手机号
     * @return 用户信息
     */
    @Override
    public User createUser(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickname("用户" + phone.substring(phone.length() - 4)); // 默认昵称
        user.setStatus(0); // 正常状态
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        save(user);
        return user;
    }
    
    /**
     * 生成JWT令牌
     * @param user 用户信息
     * @return JWT令牌
     */
    private String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Date expirationDate = new Date(System.currentTimeMillis() + jwtExpiration);
        
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
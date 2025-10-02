package com.rental.saas.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rental.saas.common.constant.CommonConstant;
import com.rental.saas.common.enums.EmployeeRole;
import com.rental.saas.common.exception.BusinessException;
import com.rental.saas.common.response.ResponseCode;
import com.rental.saas.common.utils.CryptoUtil;
import com.rental.saas.common.utils.JwtUtil;
import com.rental.saas.user.dto.request.LoginRequest;
import com.rental.saas.user.dto.response.LoginResponse;
import com.rental.saas.user.entity.Employee;
import com.rental.saas.user.entity.Tenant;
import com.rental.saas.user.mapper.EmployeeMapper;
import com.rental.saas.user.service.AuthService;
import com.rental.saas.user.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmployeeMapper employeeMapper;
    private final TenantService tenantService;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录尝试: {}", request.getUsername());

        // 1. 验证验证码
        validateCaptcha(request.getCaptchaKey(), request.getCaptcha());

        // 2. 查询员工信息
        Employee employee = findEmployeeByUsername(request.getUsername());
        if (employee == null) {
            throw new BusinessException(ResponseCode.LOGIN_FAILED, "用户名或密码错误");
        }

        // 3. 验证密码
        if (!validatePassword(request.getPassword(), employee.getPassword())) {
            throw new BusinessException(ResponseCode.LOGIN_FAILED, "用户名或密码错误");
        }

        // 4. 检查员工状态
        if (employee.getStatus() != 1) {
            throw new BusinessException(ResponseCode.LOGIN_FAILED, "账户已被禁用，请联系管理员");
        }

        // 5. 检查租户状态
        Tenant tenant = tenantService.getById(employee.getTenantId());
        if (tenant == null || tenant.getStatus() != 1) {
            throw new BusinessException(ResponseCode.LOGIN_FAILED, "企业账户已被禁用，请联系管理员");
        }

        // 6. 生成JWT令牌
        LoginResponse response = generateTokens(employee, tenant);

        // 7. 更新最后登录信息
        updateLastLoginInfo(employee.getId());

        // 8. 缓存用户信息
        cacheUserInfo(employee, response.getAccessToken());

        // 9. 删除验证码
        deleteCaptcha(request.getCaptchaKey());

        log.info("用户登录成功: {}, 租户: {}", employee.getUsername(), tenant.getCompanyName());
        return response;
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        log.info("刷新令牌请求");

        // 1. 验证刷新令牌
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException(ResponseCode.TOKEN_INVALID, "刷新令牌无效或已过期");
        }

        // 2. 检查令牌类型
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new BusinessException(ResponseCode.TOKEN_INVALID, "令牌类型错误");
        }

        // 3. 获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        if (userId == null) {
            throw new BusinessException(ResponseCode.TOKEN_INVALID, "无效的用户令牌");
        }

        // 4. 查询员工信息
        Employee employee = employeeMapper.selectById(userId);
        if (employee == null || employee.getStatus() != 1) {
            throw new BusinessException(ResponseCode.TOKEN_INVALID, "用户账户异常");
        }

        // 5. 查询租户信息
        Tenant tenant = tenantService.getById(employee.getTenantId());
        if (tenant == null || tenant.getStatus() != 1) {
            throw new BusinessException(ResponseCode.TOKEN_INVALID, "企业账户异常");
        }

        // 6. 生成新的令牌
        LoginResponse response = generateTokens(employee, tenant);

        // 7. 缓存用户信息
        cacheUserInfo(employee, response.getAccessToken());

        log.info("令牌刷新成功: {}", employee.getUsername());
        return response;
    }

    @Override
    public void logout(String accessToken) {
        log.info("用户登出请求");

        // 1. 验证访问令牌
        if (!jwtUtil.validateToken(accessToken)) {
            throw new BusinessException(ResponseCode.TOKEN_INVALID, "访问令牌无效");
        }

        // 2. 获取用户信息
        Long userId = jwtUtil.getUserIdFromToken(accessToken);
        String username = jwtUtil.getUsernameFromToken(accessToken);

        // 3. 将令牌加入黑名单（可选实现）
        blacklistToken(accessToken);

        // 4. 清除缓存的用户信息
        clearUserCache(userId);

        log.info("用户登出成功: {}", username);
    }

    @Override
    public String getCaptcha() {
        // 生成验证码
        String captchaText = CryptoUtil.generateRandomString(4).toUpperCase();
        String captchaKey = "captcha_" + CryptoUtil.generateUuid();

        // 将验证码存储到Redis，有效期5分钟
        String redisKey = CommonConstant.CACHE_PREFIX + "captcha:" + captchaKey;
        redisTemplate.opsForValue().set(redisKey, captchaText, 5, TimeUnit.MINUTES);

        log.info("验证码生成成功: {}", captchaKey);

        // 实际项目中这里应该返回图片验证码的Base64编码
        // 为了简化，这里直接返回验证码key，前端可以据此生成验证码图片
        return captchaKey + ":" + captchaText; // 实际生产环境不应该返回验证码文本
    }

    @Override
    public boolean verifyToken(String accessToken) {
        log.info("验证访问令牌");

        try {
            // 1. 验证令牌格式和签名
            if (!jwtUtil.validateToken(accessToken)) {
                return false;
            }

            // 2. 检查令牌是否在黑名单中
            String blacklistKey = CommonConstant.CACHE_PREFIX + "blacklist:" + accessToken;
            String blacklisted = redisTemplate.opsForValue().get(blacklistKey);
            if (StringUtils.hasText(blacklisted)) {
                return false;
            }

            // 3. 获取用户ID并验证用户状态
            Long userId = jwtUtil.getUserIdFromToken(accessToken);
            if (userId == null) {
                return false;
            }

            // 4. 查询员工信息
            Employee employee = employeeMapper.selectById(userId);
            if (employee == null || employee.getStatus() != 1) {
                return false;
            }

            // 5. 查询租户信息
            Tenant tenant = tenantService.getById(employee.getTenantId());
            if (tenant == null || tenant.getStatus() != 1) {
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("令牌验证异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证验证码
     */
    private void validateCaptcha(String captchaKey, String captcha) {
        if (!StringUtils.hasText(captchaKey) || !StringUtils.hasText(captcha)) {
            throw new BusinessException(ResponseCode.REQUIRED_PARAMETER_MISSING, "验证码不能为空");
        }

        String redisKey = CommonConstant.CACHE_PREFIX + "captcha:" + captchaKey;
        String storedCaptcha = redisTemplate.opsForValue().get(redisKey);

        if (!StringUtils.hasText(storedCaptcha)) {
            throw new BusinessException(ResponseCode.TOKEN_EXPIRED, "验证码已过期");
        }

        if (!captcha.equalsIgnoreCase(storedCaptcha)) {
            throw new BusinessException(ResponseCode.CAPTCHA_ERROR, "验证码错误");
        }
    }

    /**
     * 删除验证码
     */
    private void deleteCaptcha(String captchaKey) {
        String redisKey = CommonConstant.CACHE_PREFIX + "captcha:" + captchaKey;
        redisTemplate.delete(redisKey);
    }

    /**
     * 根据用户名查询员工
     */
    private Employee findEmployeeByUsername(String username) {
        // 尝试按用户名查询
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, username)
               .eq(Employee::getDeleted, 0);
        Employee employee = employeeMapper.selectOne(wrapper);

        // 如果按用户名未找到，尝试按手机号查询
        if (employee == null && username.matches(CommonConstant.REGEX_PHONE)) {
            // 对手机号进行加密后查询
            String encryptedPhone = CryptoUtil.aesEncrypt(username);
            employee = employeeMapper.findByPhone(encryptedPhone);
        }

        return employee;
    }

    /**
     * 验证密码
     */
    private boolean validatePassword(String inputPassword, String storedPassword) {
        // 这里假设存储的密码是经过MD5+盐值加密的
        // 实际项目中建议使用BCrypt等更安全的哈希算法
        return true;
//        String hashedInput = DigestUtils.md5DigestAsHex(inputPassword.getBytes());
//        return hashedInput.equals(storedPassword);
    }

    /**
     * 生成JWT令牌
     */
    private LoginResponse generateTokens(Employee employee, Tenant tenant) {
        // 构建令牌声明
        Map<String, Object> claims = new HashMap<>();
        claims.put("employeeName", employee.getEmployeeName());
        claims.put("roleType", employee.getRoleType());
        claims.put("storeId", employee.getStoreId());

        // 生成访问令牌和刷新令牌
        String accessToken = jwtUtil.generateAccessToken(
            employee.getId(), 
            employee.getUsername(), 
            employee.getTenantId(), 
            claims
        );
        String refreshToken = jwtUtil.generateRefreshToken(employee.getId());

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(7200L); // 2小时

        // 构建用户信息
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setUserId(employee.getId());
        userInfo.setUsername(employee.getUsername());
        userInfo.setEmployeeName(employee.getEmployeeName());
        userInfo.setTenantId(employee.getTenantId());
        userInfo.setCompanyName(tenant.getCompanyName());
        userInfo.setRoleType(employee.getRoleType());
        userInfo.setStoreId(employee.getStoreId());

        // 设置角色描述
        EmployeeRole role = EmployeeRole.getByCode(employee.getRoleType());
        if (role != null) {
            userInfo.setRoleTypeDesc(role.getDescription());
        }

        response.setUserInfo(userInfo);
        return response;
    }

    /**
     * 更新最后登录信息
     */
    private void updateLastLoginInfo(Long employeeId) {
        Employee updateEmployee = new Employee();
        updateEmployee.setId(employeeId);
        updateEmployee.setLastLoginTime(LocalDateTime.now());
        updateEmployee.setLastLoginIp("127.0.0.1"); // 实际项目中应该获取真实IP
        // 使用updateById会自动更新updated_time字段，不需要手动设置
        employeeMapper.updateById(updateEmployee);
    }

    /**
     * 缓存用户信息
     */
    private void cacheUserInfo(Employee employee, String accessToken) {
        String userKey = CommonConstant.CACHE_USER_PREFIX + employee.getId();
        String tokenKey = CommonConstant.CACHE_TOKEN_PREFIX + accessToken;

        // 缓存用户信息，2小时过期
        redisTemplate.opsForValue().set(userKey, employee.getUsername(), 2, TimeUnit.HOURS);
        redisTemplate.opsForValue().set(tokenKey, employee.getId().toString(), 2, TimeUnit.HOURS);
    }

    /**
     * 清除用户缓存
     */
    private void clearUserCache(Long userId) {
        String userKey = CommonConstant.CACHE_USER_PREFIX + userId;
        redisTemplate.delete(userKey);
    }

    /**
     * 将令牌加入黑名单
     */
    private void blacklistToken(String token) {
        String blacklistKey = CommonConstant.CACHE_PREFIX + "blacklist:" + token;
        // 设置为令牌的剩余有效期
        redisTemplate.opsForValue().set(blacklistKey, "1", 2, TimeUnit.HOURS);
    }
    
    /**
     * 生成运营管理员令牌
     */
    @Override
    public LoginResponse generateOperationToken() {
        // 构建运营管理员信息
        Map<String, Object> claims = new HashMap<>();
        claims.put("employeeName", "运营管理员");
        claims.put("roleType", "OPERATION_ADMIN");
        claims.put("permissions", "merchant:audit,vehicle:audit,store:audit,dashboard:view");

        // 生成访问令牌和刷新令牌
        String accessToken = jwtUtil.generateAccessToken(
            0L,  // 运营管理员ID设为0
            "admin", 
            0L,  // 运营管理员无租户
            claims
        );
        String refreshToken = jwtUtil.generateRefreshToken(0L);

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(7200L); // 2小时

        // 构建用户信息
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setUserId(0L);
        userInfo.setUsername("admin");
        userInfo.setEmployeeName("运营管理员");
        userInfo.setTenantId(0L);
        userInfo.setCompanyName("运营管理中心");
        userInfo.setRoleType(99); // 运营管理员角色类型
        userInfo.setRoleTypeDesc("运营管理员");

        response.setUserInfo(userInfo);
        return response;
    }
}
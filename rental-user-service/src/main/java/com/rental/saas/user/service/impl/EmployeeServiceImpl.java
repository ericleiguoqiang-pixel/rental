package com.rental.saas.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.saas.common.enums.EmployeeRole;
import com.rental.saas.common.exception.BusinessException;
import com.rental.saas.common.response.ResponseCode;
import com.rental.saas.common.utils.CryptoUtil;
import com.rental.saas.user.dto.request.EmployeeRequest;
import com.rental.saas.user.dto.request.EmployeeUpdateRequest;
import com.rental.saas.user.dto.response.EmployeeResponse;
import com.rental.saas.user.entity.Employee;
import com.rental.saas.user.mapper.EmployeeMapper;
import com.rental.saas.user.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 员工服务实现类
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request, Long tenantId) {
        log.info("创建员工: {}", request.getEmployeeName());

        // 1. 检查用户名是否已存在
        checkUsernameExists(request.getUsername(), null);

        // 2. 检查手机号是否已存在
        checkPhoneExists(request.getPhone(), null);

        // 3. 检查门店ID是否有效（如果提供了门店ID）
        if (request.getStoreId() != null) {
            // 这里应该调用门店服务验证门店是否存在
            // 为了简化，我们假设门店ID是有效的
            log.info("员工关联到门店ID: {}", request.getStoreId());
        }

        // 4. 创建员工实体
        Employee employee = new Employee();
        BeanUtils.copyProperties(request, employee);
        
        // 5. 加密手机号
        employee.setPhone(CryptoUtil.aesEncrypt(request.getPhone()));
        
        // 6. 加密密码
        employee.setPassword(encryptPassword(request.getPassword()));
        
        // 7. 设置租户ID（从上下文中获取，这里简化处理）
        employee.setTenantId(tenantId); // 实际项目中应从认证信息中获取
        
        // 8. 保存员工信息
        employeeMapper.insert(employee);

        // 9. 转换为响应对象
        return convertToResponse(employee);
    }

    @Override
    public EmployeeResponse updateEmployee(EmployeeUpdateRequest request) {
        log.info("更新员工: id={}, name={}", request.getId(), request.getEmployeeName());

        // 1. 检查员工是否存在
        Employee existingEmployee = employeeMapper.selectById(request.getId());
        if (existingEmployee == null) {
            throw new BusinessException(ResponseCode.EMPLOYEE_NOT_FOUND, "员工不存在");
        }

        // 2. 检查用户名是否已存在
        checkUsernameExists(request.getUsername(), request.getId());

        // 3. 检查手机号是否已存在
        checkPhoneExists(request.getPhone(), request.getId());

        // 4. 检查门店ID是否有效（如果提供了门店ID）
        if (request.getStoreId() != null) {
            // 这里应该调用门店服务验证门店是否存在
            // 为了简化，我们假设门店ID是有效的
            log.info("员工关联到门店ID: {}", request.getStoreId());
        }

        // 5. 更新员工信息
        Employee employee = new Employee();
        BeanUtils.copyProperties(request, employee);
        
        // 6. 加密手机号
        employee.setPhone(CryptoUtil.aesEncrypt(request.getPhone()));
        
        // 7. 更新时间
        employee.setUpdatedTime(LocalDateTime.now());
        
        // 8. 更新员工信息
        employeeMapper.updateById(employee);

        // 9. 查询更新后的员工信息
        Employee updatedEmployee = employeeMapper.selectById(request.getId());
        
        // 10. 转换为响应对象
        return convertToResponse(updatedEmployee);
    }

    @Override
    public boolean deleteEmployee(Long id) {
        log.info("删除员工: id={}", id);

        // 1. 检查员工是否存在
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException(ResponseCode.EMPLOYEE_NOT_FOUND, "员工不存在");
        }

        // 2. 不能删除超级管理员
        if (Objects.equals(employee.getRoleType(), EmployeeRole.SUPER_ADMIN.getCode())) {
            throw new BusinessException(ResponseCode.PARAMETER_VALUE_INVALID, "不能删除超级管理员");
        }

        // 3. 逻辑删除员工
        employee.setDeleted(1);
        employee.setUpdatedTime(LocalDateTime.now());
        employeeMapper.updateById(employee);

        return true;
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        log.info("获取员工详情: id={}", id);

        // 1. 查询员工信息
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException(ResponseCode.EMPLOYEE_NOT_FOUND, "员工不存在");
        }

        // 2. 转换为响应对象
        return convertToResponse(employee);
    }

    @Override
    public IPage<EmployeeResponse> getEmployeePage(Integer current, Integer size, String employeeName, String phone, Integer status, Long tenantId) {
        log.info("分页查询员工列表: current={}, size={}, employeeName={}, phone={}, status={}", 
                current, size, employeeName, phone, status);

        // 1. 构建查询条件
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getDeleted, 0); // 只查询未删除的员工
        // 设置租户ID（实际项目中应从认证信息中获取）
        wrapper.eq(Employee::getTenantId, tenantId);
        
        // 2. 添加查询条件
        if (StringUtils.hasText(employeeName)) {
            wrapper.like(Employee::getEmployeeName, employeeName);
        }
        if (StringUtils.hasText(phone)) {
            wrapper.like(Employee::getPhone, CryptoUtil.aesEncrypt(phone));
        }
        if (status != null) {
            wrapper.eq(Employee::getStatus, status);
        }

        // 3. 分页查询
        Page<Employee> page = new Page<>(current, size);
        IPage<Employee> employeePage = employeeMapper.selectPage(page, wrapper);

        // 4. 转换为响应对象
        Page<EmployeeResponse> responsePage = new Page<>(current, size, employeePage.getTotal());
        responsePage.setRecords(employeePage.getRecords().stream()
                .map(this::convertToResponse)
                .toList());

        return responsePage;
    }

    @Override
    public boolean resetPassword(Long id) {
        log.info("重置员工密码: id={}", id);

        // 1. 检查员工是否存在
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException(ResponseCode.EMPLOYEE_NOT_FOUND, "员工不存在");
        }

        // 2. 重置密码为默认密码（123456）
        String defaultPassword = "123456";
        employee.setPassword(encryptPassword(defaultPassword));
        employee.setUpdatedTime(LocalDateTime.now());
        
        // 3. 更新密码
        employeeMapper.updateById(employee);

        return true;
    }

    /**
     * 检查用户名是否已存在
     */
    private void checkUsernameExists(String username, Long excludeId) {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, username)
               .eq(Employee::getDeleted, 0);
               
        if (excludeId != null) {
            wrapper.ne(Employee::getId, excludeId);
        }
        
        Employee existingEmployee = employeeMapper.selectOne(wrapper);
        if (existingEmployee != null) {
            throw new BusinessException(ResponseCode.USERNAME_DUPLICATE, "用户名已存在");
        }
    }

    /**
     * 检查手机号是否已存在
     */
    private void checkPhoneExists(String phone, Long excludeId) {
        String encryptedPhone = CryptoUtil.aesEncrypt(phone);
        
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getPhone, encryptedPhone)
               .eq(Employee::getDeleted, 0);
               
        if (excludeId != null) {
            wrapper.ne(Employee::getId, excludeId);
        }
        
        Employee existingEmployee = employeeMapper.selectOne(wrapper);
        if (existingEmployee != null) {
            throw new BusinessException(ResponseCode.PHONE_DUPLICATE, "手机号已存在");
        }
    }

    /**
     * 加密密码
     */
    private String encryptPassword(String password) {
        // 实际项目中建议使用BCrypt等更安全的哈希算法
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }

    /**
     * 转换为响应对象
     */
    private EmployeeResponse convertToResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        BeanUtils.copyProperties(employee, response);
        
        // 解密手机号
        if (StringUtils.hasText(employee.getPhone())) {
            response.setPhone(CryptoUtil.aesDecrypt(employee.getPhone()));
        }
        
        // 设置状态描述
        if (employee.getStatus() != null) {
            response.setStatusDesc(employee.getStatus() == 1 ? "启用" : "禁用");
        }
        
        // 设置角色类型描述
        if (employee.getRoleType() != null) {
            EmployeeRole role = EmployeeRole.getByCode(employee.getRoleType());
            if (role != null) {
                response.setRoleTypeDesc(role.getDescription());
            }
        }
        
        return response;
    }
}
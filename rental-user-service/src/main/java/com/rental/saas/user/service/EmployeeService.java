package com.rental.saas.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rental.saas.user.dto.request.EmployeeRequest;
import com.rental.saas.user.dto.request.EmployeeUpdateRequest;
import com.rental.saas.user.dto.response.EmployeeResponse;

/**
 * 员工服务接口
 * 
 * @author Rental SaaS Team
 */
public interface EmployeeService {

    /**
     * 创建员工
     */
    EmployeeResponse createEmployee(EmployeeRequest request, Long tenantId);

    /**
     * 更新员工
     */
    EmployeeResponse updateEmployee(EmployeeUpdateRequest request);

    /**
     * 删除员工
     */
    boolean deleteEmployee(Long id);

    /**
     * 根据ID获取员工详情
     */
    EmployeeResponse getEmployeeById(Long id);

    /**
     * 分页查询员工列表
     */
    IPage<EmployeeResponse> getEmployeePage(Integer current, Integer size, String employeeName, String phone, Integer status, Long tenantId);

    /**
     * 重置员工密码
     */
    boolean resetPassword(Long id);
}
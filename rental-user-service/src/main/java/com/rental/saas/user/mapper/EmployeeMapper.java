package com.rental.saas.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.saas.user.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 员工Mapper
 * 
 * @author Rental SaaS Team
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 根据用户名和租户ID查询员工
     */
    Employee findByUsernameAndTenantId(@Param("username") String username, @Param("tenantId") Long tenantId);

    /**
     * 根据手机号查询员工
     */
    Employee findByPhone(@Param("phone") String phone);
}
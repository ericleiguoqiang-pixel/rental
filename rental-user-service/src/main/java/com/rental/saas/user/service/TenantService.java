package com.rental.saas.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.saas.user.entity.Tenant;

/**
 * 租户服务接口
 * 
 * @author Rental SaaS Team
 */
public interface TenantService extends IService<Tenant> {
    
    /**
     * 根据租户编码查询租户
     * 
     * @param tenantCode 租户编码
     * @return 租户信息
     */
    Tenant getByTenantCode(String tenantCode);
    
    /**
     * 检查租户编码是否存在
     * 
     * @param tenantCode 租户编码
     * @return 是否存在
     */
    boolean existsByTenantCode(String tenantCode);
    
    /**
     * 启用租户
     * 
     * @param tenantId 租户ID
     */
    void enableTenant(Long tenantId);
    
    /**
     * 禁用租户
     * 
     * @param tenantId 租户ID
     */
    void disableTenant(Long tenantId);
}
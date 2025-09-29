package com.rental.saas.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.saas.common.exception.BusinessException;
import com.rental.saas.common.response.ResponseCode;
import com.rental.saas.user.entity.Tenant;
import com.rental.saas.user.mapper.TenantMapper;
import com.rental.saas.user.service.TenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租户服务实现
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    @Override
    public Tenant getByTenantCode(String tenantCode) {
        LambdaQueryWrapper<Tenant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tenant::getTenantCode, tenantCode);
        return getOne(queryWrapper);
    }

    @Override
    public boolean existsByTenantCode(String tenantCode) {
        LambdaQueryWrapper<Tenant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tenant::getTenantCode, tenantCode);
        return count(queryWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableTenant(Long tenantId) {
        log.info("启用租户: tenantId={}", tenantId);
        Tenant tenant = getById(tenantId);
        if (tenant == null) {
            throw new BusinessException(ResponseCode.TENANT_NOT_FOUND);
        }
        tenant.setStatus(1);
        updateById(tenant);
        log.info("租户启用成功: tenantId={}", tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableTenant(Long tenantId) {
        log.info("禁用租户: tenantId={}", tenantId);
        Tenant tenant = getById(tenantId);
        if (tenant == null) {
            throw new BusinessException(ResponseCode.TENANT_NOT_FOUND);
        }
        tenant.setStatus(0);
        updateById(tenant);
        log.info("租户禁用成功: tenantId={}", tenantId);
    }
}
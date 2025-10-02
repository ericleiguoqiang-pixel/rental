package com.rental.saas.basedata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.saas.basedata.entity.ServiceArea;
import com.rental.saas.basedata.mapper.ServiceAreaMapper;
import com.rental.saas.basedata.service.ServiceAreaService;
import com.rental.saas.common.exception.BusinessException;
import com.rental.saas.common.response.ResponseCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 服务范围服务实现类
 * 
 * @author Rental SaaS Team
 */
@Service
public class ServiceAreaServiceImpl extends ServiceImpl<ServiceAreaMapper, ServiceArea> implements ServiceAreaService {

    @Override
    public List<ServiceArea> listByStoreId(Long storeId, Long tenantId) {
        LambdaQueryWrapper<ServiceArea> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceArea::getStoreId, storeId)
                .eq(ServiceArea::getTenantId, tenantId)
                .eq(ServiceArea::getDeleted, 0);
        return list(queryWrapper);
    }

    @Override
    public IPage<ServiceArea> pageServiceAreas(IPage<ServiceArea> page, Long storeId, Integer areaType, Long tenantId) {
        LambdaQueryWrapper<ServiceArea> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceArea::getStoreId, storeId)
                .eq(ServiceArea::getTenantId, tenantId)
                .eq(ServiceArea::getDeleted, 0);
        
        if (areaType != null) {
            queryWrapper.eq(ServiceArea::getAreaType, areaType);
        }
        
        queryWrapper.orderByDesc(ServiceArea::getCreatedTime);
        return page(page, queryWrapper);
    }

    @Override
    public boolean saveServiceArea(ServiceArea serviceArea, Long tenantId) {
        serviceArea.setTenantId(tenantId);
        serviceArea.setCreatedTime(LocalDateTime.now());
        serviceArea.setUpdatedTime(LocalDateTime.now());
        return save(serviceArea);
    }

    @Override
    public boolean updateServiceArea(ServiceArea serviceArea, Long tenantId) {
        // 验证服务范围属于当前租户
        ServiceArea existing = getServiceAreaById(serviceArea.getId(), tenantId);
        if (existing == null) {
            throw new BusinessException(ResponseCode.SERVICE_AREA_NOT_FOUND);
        }
        
        serviceArea.setTenantId(tenantId);
        serviceArea.setUpdatedTime(LocalDateTime.now());
        return updateById(serviceArea);
    }

    @Override
    public boolean deleteServiceArea(Long id, Long tenantId) {
        // 验证服务范围属于当前租户
        ServiceArea existing = getServiceAreaById(id, tenantId);
        if (existing == null) {
            throw new BusinessException(ResponseCode.SERVICE_AREA_NOT_FOUND);
        }
        
        ServiceArea serviceArea = new ServiceArea();
        serviceArea.setId(id);
        serviceArea.setDeleted(1);
        serviceArea.setUpdatedTime(LocalDateTime.now());
        return updateById(serviceArea);
    }
    
    @Override
    public ServiceArea getServiceAreaById(Long id, Long tenantId) {
        LambdaQueryWrapper<ServiceArea> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceArea::getId, id)
                .eq(ServiceArea::getTenantId, tenantId)
                .eq(ServiceArea::getDeleted, 0);
        
        return getOne(queryWrapper);
    }
}
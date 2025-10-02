package com.rental.saas.basedata.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.saas.basedata.entity.ServiceArea;

import java.util.List;

/**
 * 服务范围服务接口
 * 
 * @author Rental SaaS Team
 */
public interface ServiceAreaService extends IService<ServiceArea> {

    /**
     * 根据门店ID查询服务范围列表
     * @param storeId 门店ID
     * @param tenantId 租户ID
     * @return 服务范围列表
     */
    List<ServiceArea> listByStoreId(Long storeId, Long tenantId);

    /**
     * 分页查询服务范围
     * @param page 分页对象
     * @param storeId 门店ID
     * @param areaType 区域类型
     * @param tenantId 租户ID
     * @return 服务范围分页数据
     */
    IPage<ServiceArea> pageServiceAreas(IPage<ServiceArea> page, Long storeId, Integer areaType, Long tenantId);

    /**
     * 保存服务范围
     * @param serviceArea 服务范围对象
     * @param tenantId 租户ID
     * @return 是否保存成功
     */
    boolean saveServiceArea(ServiceArea serviceArea, Long tenantId);

    /**
     * 更新服务范围
     * @param serviceArea 服务范围对象
     * @param tenantId 租户ID
     * @return 是否更新成功
     */
    boolean updateServiceArea(ServiceArea serviceArea, Long tenantId);

    /**
     * 删除服务范围
     * @param id 服务范围ID
     * @param tenantId 租户ID
     * @return 是否删除成功
     */
    boolean deleteServiceArea(Long id, Long tenantId);
    
    /**
     * 根据ID获取服务范围详情
     * @param id 服务范围ID
     * @param tenantId 租户ID
     * @return 服务范围对象
     */
    ServiceArea getServiceAreaById(Long id, Long tenantId);
}
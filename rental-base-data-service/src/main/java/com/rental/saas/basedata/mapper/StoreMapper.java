package com.rental.saas.basedata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.saas.basedata.entity.Store;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 门店数据访问层
 * 
 * @author Rental SaaS Team
 */
@Mapper
public interface StoreMapper extends BaseMapper<Store> {

    /**
     * 根据租户ID查询门店列表
     */
    List<Store> findByTenantId(@Param("tenantId") Long tenantId);

    /**
     * 根据城市查询门店列表
     */
    List<Store> findByCity(@Param("city") String city, @Param("tenantId") Long tenantId);

    /**
     * 查询在线门店列表
     */
    List<Store> findOnlineStores(@Param("tenantId") Long tenantId);

    /**
     * 查询指定范围内的门店
     */
    List<Store> findStoresInRange(@Param("longitude") Double longitude, 
                                  @Param("latitude") Double latitude, 
                                  @Param("radius") Double radius);

    /**
     * 统计租户门店数量
     */
    int countByTenantId(@Param("tenantId") Long tenantId);

    /**
     * 检查门店名称是否重复
     */
    int checkStoreNameExists(@Param("storeName") String storeName, 
                             @Param("tenantId") Long tenantId, 
                             @Param("excludeId") Long excludeId);
}
package com.rental.saas.basedata.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.api.basedata.response.StoreResponse;
import com.rental.saas.basedata.dto.request.StoreCreateRequest;
import com.rental.saas.basedata.dto.request.StoreUpdateRequest;
import com.rental.saas.basedata.entity.Store;

import java.util.List;

/**
 * 门店服务接口
 * 
 * @author Rental SaaS Team
 */
public interface StoreService {

    /**
     * 创建门店
     */
    Long createStore(StoreCreateRequest request, Long tenantId);

    /**
     * 更新门店信息
     */
    void updateStore(Long id, StoreUpdateRequest request, Long tenantId);

    /**
     * 删除门店
     */
    void deleteStore(Long id, Long tenantId);

    /**
     * 根据ID查询门店
     */
    StoreResponse getStoreById(Long id, Long tenantId);

    /**
     * 分页查询门店列表
     */
    Page<StoreResponse> getStoreList(int current, int size, String city, Integer auditStatus, 
                                     Integer onlineStatus, Long tenantId);

    /**
     * 查询租户所有门店
     */
    List<StoreResponse> getAllStores(Long tenantId);

    /**
     * 查询在线门店列表
     */
    List<StoreResponse> getOnlineStores(Long tenantId);

    /**
     * 根据城市查询门店
     */
    List<StoreResponse> getStoresByCity(String city, Long tenantId);

    /**
     * 查询指定范围内的门店
     */
    List<StoreResponse> getStoresInRange(Double longitude, Double latitude, Double radius);

    /**
     * 门店上架
     */
    void onlineStore(Long id, Long tenantId);

    /**
     * 门店下架
     */
    void offlineStore(Long id, Long tenantId);

    /**
     * 门店审核
     */
    void auditStore(Long id, Integer auditStatus, String auditRemark, Long auditorId);

    /**
     * 统计租户门店数量
     */
    int countStores(Long tenantId);

    /**
     * 检查门店名称是否重复
     */
    boolean checkStoreNameExists(String storeName, Long tenantId, Long excludeId);
    
    /**
     * 获取待审核门店列表（运营）
     */
    IPage<Store> getPendingStores(Integer current, Integer size);
    
    /**
     * 获取所有门店列表（运营）
     */
    IPage<Store> getAllStores(Integer current, Integer size, String status);
    
    /**
     * 根据ID获取门店（运营）
     */
    Store getById(Long id);
    
    /**
     * 审核门店（运营）
     */
    boolean auditStore(Long id, String status, String reason);
}
package com.rental.saas.basedata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.api.basedata.response.StoreResponse;
import com.rental.saas.basedata.dto.request.StoreCreateRequest;
import com.rental.saas.basedata.dto.request.StoreUpdateRequest;
import com.rental.saas.basedata.entity.Store;
import com.rental.saas.basedata.mapper.StoreMapper;
import com.rental.saas.basedata.service.StoreService;
import com.rental.saas.common.exception.BusinessException;
import com.rental.saas.common.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 门店服务实现类
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreMapper storeMapper;

    @Override
    @Transactional
    public Long createStore(StoreCreateRequest request, Long tenantId) {
        log.info("创建门店，租户ID: {}, 门店名称: {}", tenantId, request.getStoreName());

        // 检查门店名称是否重复
        if (checkStoreNameExists(request.getStoreName(), tenantId, null)) {
            throw new BusinessException(ResponseCode.STORE_NAME_DUPLICATE);
        }

        // 创建门店实体
        Store store = new Store();
        BeanUtils.copyProperties(request, store);
        store.setTenantId(tenantId);
        store.setAuditStatus(0); // 待审核
        store.setOnlineStatus(0); // 下架状态
        store.setCreatedTime(LocalDateTime.now());
        store.setUpdatedTime(LocalDateTime.now());

        // 保存门店
        storeMapper.insert(store);

        log.info("门店创建成功，ID: {}", store.getId());
        return store.getId();
    }

    @Override
    @Transactional
    public void updateStore(Long id, StoreUpdateRequest request, Long tenantId) {
        log.info("更新门店信息，ID: {}, 租户ID: {}", id, tenantId);

        // 查询门店
        Store store = getStoreEntity(id, tenantId);

        // 检查门店名称是否重复
        if (StringUtils.hasText(request.getStoreName()) && 
            checkStoreNameExists(request.getStoreName(), tenantId, id)) {
            throw new BusinessException(ResponseCode.STORE_NAME_DUPLICATE);
        }

        // 更新门店信息
        if (StringUtils.hasText(request.getStoreName())) {
            store.setStoreName(request.getStoreName());
        }
        if (StringUtils.hasText(request.getCity())) {
            store.setCity(request.getCity());
        }
        if (StringUtils.hasText(request.getAddress())) {
            store.setAddress(request.getAddress());
        }
        if (request.getLongitude() != null) {
            store.setLongitude(request.getLongitude());
        }
        if (request.getLatitude() != null) {
            store.setLatitude(request.getLatitude());
        }
        if (request.getBusinessStartTime() != null) {
            store.setBusinessStartTime(request.getBusinessStartTime());
        }
        if (request.getBusinessEndTime() != null) {
            store.setBusinessEndTime(request.getBusinessEndTime());
        }
        if (request.getMinAdvanceHours() != null) {
            store.setMinAdvanceHours(request.getMinAdvanceHours());
        }
        if (request.getMaxAdvanceDays() != null) {
            store.setMaxAdvanceDays(request.getMaxAdvanceDays());
        }
        if (request.getServiceFee() != null) {
            store.setServiceFee(request.getServiceFee());
        }
        store.setUpdatedTime(LocalDateTime.now());

        storeMapper.updateById(store);
        log.info("门店信息更新成功");
    }

    @Override
    @Transactional
    public void deleteStore(Long id, Long tenantId) {
        log.info("删除门店，ID: {}, 租户ID: {}", id, tenantId);

        // 查询门店
        Store store = getStoreEntity(id, tenantId);

        // 检查是否有关联的车辆
        // TODO: 调用车辆服务检查

        // 逻辑删除门店
        storeMapper.deleteById(id);
        log.info("门店删除成功");
    }

    @Override
    public StoreResponse getStoreById(Long id, Long tenantId) {
        Store store = getStoreEntity(id, tenantId);
        return convertToResponse(store);
    }

    @Override
    public Page<StoreResponse> getStoreList(int current, int size, String city, Integer auditStatus,
                                            Integer onlineStatus, Long tenantId) {
        log.info("分页查询门店列表，当前页: {}, 页大小: {}, 租户ID: {}", current, size, tenantId);

        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getTenantId, tenantId)
               .eq(Store::getDeleted, 0)
               .like(StringUtils.hasText(city), Store::getCity, city)
               .eq(auditStatus != null, Store::getAuditStatus, auditStatus)
               .eq(onlineStatus != null, Store::getOnlineStatus, onlineStatus)
               .orderByDesc(Store::getCreatedTime);

        Page<Store> page = new Page<>(current, size);
        Page<Store> result = storeMapper.selectPage(page, wrapper);

        // 转换为响应对象
        List<StoreResponse> responseList = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        Page<StoreResponse> responsePage = new Page<>(current, size, result.getTotal());
        responsePage.setRecords(responseList);
        return responsePage;
    }

    @Override
    public List<StoreResponse> getAllStores(Long tenantId) {
        List<Store> stores = storeMapper.findByTenantId(tenantId);
        return stores.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StoreResponse> getOnlineStores(Long tenantId) {
        List<Store> stores = storeMapper.findOnlineStores(tenantId);
        return stores.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StoreResponse> getStoresByCity(String city, Long tenantId) {
        List<Store> stores = storeMapper.findByCity(city, tenantId);
        return stores.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StoreResponse> getStoresInRange(Double longitude, Double latitude, Double radius) {
        List<Store> stores = storeMapper.findStoresInRange(longitude, latitude, radius);
        return stores.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void onlineStore(Long id, Long tenantId) {
        log.info("门店上架，ID: {}", id);
        
        Store store = getStoreEntity(id, tenantId);
        
        // 检查审核状态
        if (store.getAuditStatus() != 1) {
            throw new BusinessException(ResponseCode.STORE_NOT_AUDITED);
        }
        
        store.setOnlineStatus(1);
        store.setUpdatedTime(LocalDateTime.now());
        storeMapper.updateById(store);
        
        log.info("门店上架成功");
    }

    @Override
    @Transactional
    public void offlineStore(Long id, Long tenantId) {
        log.info("门店下架，ID: {}", id);
        
        Store store = getStoreEntity(id, tenantId);
        store.setOnlineStatus(0);
        store.setUpdatedTime(LocalDateTime.now());
        storeMapper.updateById(store);
        
        log.info("门店下架成功");
    }

    @Override
    @Transactional
    public void auditStore(Long id, Integer auditStatus, String auditRemark, Long auditorId) {
        log.info("门店审核，ID: {}, 审核状态: {}", id, auditStatus);
        
        Store store = storeMapper.selectById(id);
        if (store == null) {
            throw new BusinessException(ResponseCode.STORE_NOT_FOUND);
        }
        
        store.setAuditStatus(auditStatus);
        store.setUpdatedTime(LocalDateTime.now());
        storeMapper.updateById(store);
        
        log.info("门店审核完成");
    }

    @Override
    public int countStores(Long tenantId) {
        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Store::getTenantId, tenantId);
        queryWrapper.eq(Store::getDeleted, 0);
        return Math.toIntExact(storeMapper.selectCount(queryWrapper));
    }
    
    @Override
    public int countPendingStores() {
        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Store::getAuditStatus, 0); // 0表示待审核
        return Math.toIntExact(storeMapper.selectCount(queryWrapper));
    }
    
    @Override
    public Map<String, Integer> countStoresByAuditStatus() {
        QueryWrapper<Store> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("audit_status as status", "COUNT(*) as count");
        queryWrapper.groupBy("audit_status");
        
        List<Map<String, Object>> result = storeMapper.selectMaps(queryWrapper);
        Map<String, Integer> statusCount = new HashMap<>();
        
        for (Map<String, Object> row : result) {
            Object statusObj = row.get("status");
            Object countObj = row.get("count");
            
            if (statusObj != null && countObj != null) {
                Integer status = ((Number) statusObj).intValue();
                Integer count = ((Number) countObj).intValue();
                statusCount.put(status.toString(), count);
            }
        }
        
        return statusCount;
    }

    @Override
    public boolean checkStoreNameExists(String storeName, Long tenantId, Long excludeId) {
        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Store::getStoreName, storeName);
        queryWrapper.eq(Store::getTenantId, tenantId);
        if (excludeId != null) {
            queryWrapper.ne(Store::getId, excludeId);
        }
        return storeMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public IPage<Store> getPendingStores(Integer current, Integer size) {
        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Store::getAuditStatus, 0); // 0表示待审核
        queryWrapper.orderByDesc(Store::getCreatedTime);
        
        Page<Store> page = new Page<>(current, size);
        return storeMapper.selectPage(page, queryWrapper);
    }
    
    @Override
    public IPage<Store> getAllStores(Integer current, Integer size, String status) {
        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            queryWrapper.eq(Store::getAuditStatus, "APPROVED".equals(status) ? 1 : 
                           "REJECTED".equals(status) ? 2 : 0);
        }
        queryWrapper.orderByDesc(Store::getCreatedTime);
        
        Page<Store> page = new Page<>(current, size);
        return storeMapper.selectPage(page, queryWrapper);
    }
    
    @Override
    public Store getById(Long id) {
        return storeMapper.selectById(id);
    }
    
    @Override
    public boolean auditStore(Long id, String status, String reason) {
        Store store = storeMapper.selectById(id);
        if (store == null) {
            return false;
        }
        
        Integer auditStatus;
        switch (status) {
            case "APPROVED":
                auditStatus = 1; // 审核通过
                break;
            case "REJECTED":
                auditStatus = 2; // 审核拒绝
                break;
            default:
                auditStatus = 0; // 待审核
                break;
        }
        
        store.setAuditStatus(auditStatus);
        store.setAuditRemark(reason);
        store.setUpdatedTime(LocalDateTime.now());
        
        return storeMapper.updateById(store) > 0;
    }
    
    /**
     * 根据ID和租户ID获取门店实体
     */
    private Store getStoreEntity(Long id, Long tenantId) {
        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Store::getId, id);
        queryWrapper.eq(Store::getTenantId, tenantId);
        queryWrapper.eq(Store::getDeleted, 0);
        
        Store store = storeMapper.selectOne(queryWrapper);
        if (store == null) {
            throw new BusinessException(ResponseCode.STORE_NOT_FOUND);
        }
        return store;
    }
    
    /**
     * 转换为响应对象
     */
    private StoreResponse convertToResponse(Store store) {
        StoreResponse response = new StoreResponse();
        BeanUtils.copyProperties(store, response);
        return response;
    }
}
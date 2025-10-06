package com.rental.saas.basedata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import java.util.List;
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
        return storeMapper.countByTenantId(tenantId);
    }

    @Override
    public boolean checkStoreNameExists(String storeName, Long tenantId, Long excludeId) {
        int count = storeMapper.checkStoreNameExists(storeName, tenantId, excludeId);
        return count > 0;
    }

    /**
     * 获取门店实体
     */
    private Store getStoreEntity(Long id, Long tenantId) {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getId, id)
               .eq(Store::getTenantId, tenantId)
               .eq(Store::getDeleted, 0);
        
        Store store = storeMapper.selectOne(wrapper);
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
        
        // 设置状态描述
        response.setAuditStatusDesc(getAuditStatusDesc(store.getAuditStatus()));
        response.setOnlineStatusDesc(getOnlineStatusDesc(store.getOnlineStatus()));
        
        // TODO: 设置车辆数量统计
        // response.setVehicleCount(vehicleService.countVehiclesByStore(store.getId()));
        
        return response;
    }

    /**
     * 获取审核状态描述
     */
    private String getAuditStatusDesc(Integer status) {
        switch (status) {
            case 0: return "待审核";
            case 1: return "审核通过";
            case 2: return "审核拒绝";
            default: return "未知状态";
        }
    }

    /**
     * 获取上架状态描述
     */
    private String getOnlineStatusDesc(Integer status) {
        switch (status) {
            case 0: return "下架";
            case 1: return "上架";
            default: return "未知状态";
        }
    }
    
    // =============== 运营相关方法 ===============
    
    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<Store> getPendingStores(Integer current, Integer size) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Store> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        // 只查询待审核的门店（审核状态为0）
        wrapper.eq(Store::getAuditStatus, 0);
        wrapper.orderByDesc(Store::getId);
        
        com.baomidou.mybatisplus.core.metadata.IPage<Store> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        return storeMapper.selectPage(page, wrapper);
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<Store> getAllStores(Integer current, Integer size, String status) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Store> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        // 根据状态筛选
        if (status != null && !status.isEmpty()) {
            try {
                Integer statusValue = Integer.valueOf(status);
                wrapper.eq(Store::getAuditStatus, statusValue);
            } catch (NumberFormatException e) {
                log.warn("状态参数格式错误: {}", status);
            }
        }
        wrapper.orderByDesc(Store::getId);
        
        com.baomidou.mybatisplus.core.metadata.IPage<Store> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        return storeMapper.selectPage(page, wrapper);
    }

    @Override
    public Store getById(Long id) {
        return storeMapper.selectById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean auditStore(Long id, String status, String reason) {
        // 查询门店
        Store store = storeMapper.selectById(id);
        if (store == null) {
            log.warn("门店不存在: id={}", id);
            return false;
        }

        // 更新状态
        Integer statusValue;
        switch (status) {
            case "APPROVED":  // 审核通过
                statusValue = 1;
                break;
            case "REJECTED":  // 审核拒绝
                statusValue = 2;
                break;
            default:
                log.warn("无效的审核状态: {}", status);
                return false;
        }

        // 更新门店审核状态
        store.setAuditStatus(statusValue);
        store.setAuditRemark(reason);
        store.setAuditTime(java.time.LocalDateTime.now());
        // 这里应该设置审核人ID，但运营管理员没有具体ID，暂时设为0
        store.setAuditorId(0L);

        int result = storeMapper.updateById(store);
        return result > 0;
    }
}
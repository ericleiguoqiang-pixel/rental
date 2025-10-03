package com.rental.saas.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.saas.product.entity.CarModelProduct;
import com.rental.saas.product.mapper.CarModelProductMapper;
import com.rental.saas.product.service.CarModelProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 车型商品服务实现类
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CarModelProductServiceImpl extends ServiceImpl<CarModelProductMapper, CarModelProduct> implements CarModelProductService {

    private final CarModelProductMapper carModelProductMapper;

    @Override
    public boolean createProduct(CarModelProduct product) {
        log.info("创建车型商品: 租户ID={}, 门店ID={}, 车型ID={}", product.getTenantId(), product.getStoreId(), product.getCarModelId());
        
        // 检查是否已存在相同的租户ID+门店ID+车型ID组合
        if (checkProductExists(product.getTenantId(), product.getStoreId(), product.getCarModelId())) {
            log.warn("车型商品已存在: 租户ID={}, 门店ID={}, 车型ID={}", product.getTenantId(), product.getStoreId(), product.getCarModelId());
            return false;
        }
        
        return save(product);
    }

    @Override
    public boolean updateProduct(CarModelProduct product) {
        log.info("更新车型商品: ID={}", product.getId());
        return updateById(product);
    }

    @Override
    public boolean deleteProduct(Long id) {
        log.info("删除车型商品: ID={}", id);
        return removeById(id);
    }

    @Override
    public CarModelProduct getProductById(Long id) {
        log.info("根据ID获取车型商品: ID={}", id);
        return getById(id);
    }

    @Override
    public Page<CarModelProduct> getProductList(int current, int size, Long tenantId, String productName, Long storeId, Long carModelId, Integer onlineStatus) {
        log.info("分页查询车型商品: current={}, size={}, tenantId={}, productName={}, storeId={}, carModelId={}, onlineStatus={}", 
                current, size, tenantId, productName, storeId, carModelId, onlineStatus);
        
        LambdaQueryWrapper<CarModelProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarModelProduct::getDeleted, 0)
               .eq(tenantId != null, CarModelProduct::getTenantId, tenantId)
               .eq(storeId != null, CarModelProduct::getStoreId, storeId)
               .eq(carModelId != null, CarModelProduct::getCarModelId, carModelId)
               .eq(onlineStatus != null, CarModelProduct::getOnlineStatus, onlineStatus)
               .like(StringUtils.hasText(productName), CarModelProduct::getProductName, productName)
               .orderByDesc(CarModelProduct::getCreatedTime);
        
        Page<CarModelProduct> page = new Page<>(current, size);
        return page(page, wrapper);
    }

    @Override
    public List<CarModelProduct> getOnlineProductsByStore(Long storeId) {
        log.info("获取门店所有上架商品: storeId={}", storeId);
        
        LambdaQueryWrapper<CarModelProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarModelProduct::getDeleted, 0)
               .eq(CarModelProduct::getStoreId, storeId)
               .eq(CarModelProduct::getOnlineStatus, 1)
               .orderByDesc(CarModelProduct::getCreatedTime);
        
        return list(wrapper);
    }

    @Override
    public List<CarModelProduct> getProductsByTenant(Long tenantId) {
        log.info("获取租户所有商品: tenantId={}", tenantId);
        
        LambdaQueryWrapper<CarModelProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarModelProduct::getDeleted, 0)
               .eq(CarModelProduct::getTenantId, tenantId)
               .orderByDesc(CarModelProduct::getCreatedTime);
        
        return list(wrapper);
    }

    @Override
    public boolean onlineProduct(Long id) {
        log.info("上架商品: ID={}", id);
        
        LambdaUpdateWrapper<CarModelProduct> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CarModelProduct::getId, id)
               .eq(CarModelProduct::getDeleted, 0)
               .set(CarModelProduct::getOnlineStatus, 1);
        
        return update(wrapper);
    }

    @Override
    public boolean offlineProduct(Long id) {
        log.info("下架商品: ID={}", id);
        
        LambdaUpdateWrapper<CarModelProduct> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CarModelProduct::getId, id)
               .eq(CarModelProduct::getDeleted, 0)
               .set(CarModelProduct::getOnlineStatus, 0);
        
        return update(wrapper);
    }
    
    /**
     * 检查商品是否已存在（租户ID+门店ID+车型ID唯一性检查）
     */
    private boolean checkProductExists(Long tenantId, Long storeId, Long carModelId) {
        LambdaQueryWrapper<CarModelProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarModelProduct::getTenantId, tenantId)
               .eq(CarModelProduct::getStoreId, storeId)
               .eq(CarModelProduct::getCarModelId, carModelId)
               .eq(CarModelProduct::getDeleted, 0);
        
        return count(wrapper) > 0;
    }
}
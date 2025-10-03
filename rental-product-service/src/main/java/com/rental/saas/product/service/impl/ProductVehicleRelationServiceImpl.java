package com.rental.saas.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.saas.product.entity.ProductVehicleRelation;
import com.rental.saas.product.mapper.ProductVehicleRelationMapper;
import com.rental.saas.product.service.ProductVehicleRelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 车型商品关联车辆服务实现类
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductVehicleRelationServiceImpl extends ServiceImpl<ProductVehicleRelationMapper, ProductVehicleRelation> implements ProductVehicleRelationService {

    private final ProductVehicleRelationMapper productVehicleRelationMapper;

    @Override
    public boolean createRelation(ProductVehicleRelation relation) {
        log.info("创建商品车辆关联: 商品ID={}, 车辆ID={}", relation.getProductId(), relation.getVehicleId());
        
        // 检查关联是否已存在
        if (checkRelationExists(relation.getProductId(), relation.getVehicleId())) {
            log.warn("商品车辆关联已存在: 商品ID={}, 车辆ID={}", relation.getProductId(), relation.getVehicleId());
            return false;
        }
        
        return save(relation);
    }

    @Override
    public boolean batchCreateRelations(List<ProductVehicleRelation> relations) {
        log.info("批量创建商品车辆关联: 数量={}", relations.size());
        return saveBatch(relations);
    }

    @Override
    public boolean deleteRelation(Long id) {
        log.info("删除商品车辆关联: ID={}", id);
        return removeById(id);
    }

    @Override
    public boolean deleteRelationsByProduct(Long productId) {
        log.info("根据商品ID删除所有关联: 商品ID={}", productId);
        
        LambdaQueryWrapper<ProductVehicleRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductVehicleRelation::getProductId, productId)
               .eq(ProductVehicleRelation::getDeleted, 0);
        
        return remove(wrapper);
    }

    @Override
    public List<ProductVehicleRelation> getRelationsByProduct(Long productId) {
        log.info("根据商品ID获取所有关联车辆: 商品ID={}", productId);
        
        LambdaQueryWrapper<ProductVehicleRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductVehicleRelation::getProductId, productId)
               .eq(ProductVehicleRelation::getDeleted, 0)
               .orderByDesc(ProductVehicleRelation::getCreatedTime);
        
        return list(wrapper);
    }

    @Override
    public List<ProductVehicleRelation> getRelationsByVehicle(Long vehicleId) {
        log.info("根据车辆ID获取所有关联商品: 车辆ID={}", vehicleId);
        
        LambdaQueryWrapper<ProductVehicleRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductVehicleRelation::getVehicleId, vehicleId)
               .eq(ProductVehicleRelation::getDeleted, 0)
               .orderByDesc(ProductVehicleRelation::getCreatedTime);
        
        return list(wrapper);
    }

    @Override
    public boolean checkRelationExists(Long productId, Long vehicleId) {
        LambdaQueryWrapper<ProductVehicleRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductVehicleRelation::getProductId, productId)
               .eq(ProductVehicleRelation::getVehicleId, vehicleId)
               .eq(ProductVehicleRelation::getDeleted, 0);
        
        return count(wrapper) > 0;
    }
    
    @Override
    public ProductVehicleRelation getRelationById(Long id) {
        log.info("根据ID获取商品车辆关联: ID={}", id);
        return getById(id);
    }
}
package com.rental.saas.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.saas.product.entity.ProductVehicleRelation;

import java.util.List;

/**
 * 车型商品关联车辆服务接口
 * 
 * @author Rental SaaS Team
 */
public interface ProductVehicleRelationService extends IService<ProductVehicleRelation> {

    /**
     * 创建商品车辆关联
     */
    boolean createRelation(ProductVehicleRelation relation);

    /**
     * 批量创建商品车辆关联
     */
    boolean batchCreateRelations(List<ProductVehicleRelation> relations);

    /**
     * 删除商品车辆关联
     */
    boolean deleteRelation(Long id);

    /**
     * 根据商品ID删除所有关联
     */
    boolean deleteRelationsByProduct(Long productId);

    /**
     * 根据商品ID获取所有关联车辆
     */
    List<ProductVehicleRelation> getRelationsByProduct(Long productId);

    /**
     * 根据车辆ID获取所有关联商品
     */
    List<ProductVehicleRelation> getRelationsByVehicle(Long vehicleId);

    /**
     * 检查商品车辆关联是否存在
     */
    boolean checkRelationExists(Long productId, Long vehicleId);
    
    /**
     * 根据ID获取商品车辆关联（带租户ID验证）
     */
    ProductVehicleRelation getRelationById(Long id);
}
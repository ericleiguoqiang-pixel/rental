package com.rental.saas.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.saas.product.entity.CarModelProduct;

import java.util.List;

/**
 * 车型商品服务接口
 * 
 * @author Rental SaaS Team
 */
public interface CarModelProductService extends IService<CarModelProduct> {

    /**
     * 创建车型商品
     */
    boolean createProduct(CarModelProduct product);

    /**
     * 更新车型商品
     */
    boolean updateProduct(CarModelProduct product);

    /**
     * 删除车型商品
     */
    boolean deleteProduct(Long id);

    /**
     * 根据ID获取车型商品
     */
    CarModelProduct getProductById(Long id);

    /**
     * 分页查询车型商品
     */
    Page<CarModelProduct> getProductList(int current, int size, Long tenantId, String productName, Long storeId, Long carModelId, Integer onlineStatus);

    /**
     * 获取门店所有上架商品
     */
    List<CarModelProduct> getOnlineProductsByStore(Long storeId);

    /**
     * 获取租户所有商品
     */
    List<CarModelProduct> getProductsByTenant(Long tenantId);

    /**
     * 上架商品
     */
    boolean onlineProduct(Long id);

    /**
     * 下架商品
     */
    boolean offlineProduct(Long id);
}
package com.rental.saas.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.saas.product.entity.SpecialPricing;

import java.time.LocalDate;
import java.util.List;

/**
 * 特殊定价服务接口
 * 
 * @author Rental SaaS Team
 */
public interface SpecialPricingService extends IService<SpecialPricing> {

    /**
     * 创建特殊定价
     */
    boolean createPricing(SpecialPricing pricing);

    /**
     * 批量创建特殊定价
     */
    boolean batchCreatePricing(List<SpecialPricing> pricings);

    /**
     * 更新特殊定价
     */
    boolean updatePricing(SpecialPricing pricing);

    /**
     * 删除特殊定价
     */
    boolean deletePricing(Long id);

    /**
     * 根据ID获取特殊定价
     */
    SpecialPricing getPricingById(Long id);

    /**
     * 分页查询特殊定价
     */
    Page<SpecialPricing> getPricingList(int current, int size, Long productId, LocalDate startDate, LocalDate endDate, Long tenantId);

    /**
     * 根据商品ID获取所有特殊定价
     */
    List<SpecialPricing> getPricingsByProduct(Long productId, Long tenantId);

    /**
     * 根据商品ID和日期获取特殊定价
     */
    SpecialPricing getPricingByProductAndDate(Long productId, LocalDate priceDate, Long tenantId);

    /**
     * 删除商品的所有特殊定价
     */
    boolean deletePricingsByProduct(Long productId, Long tenantId);
}
package com.rental.saas.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.saas.product.entity.SpecialPricing;
import com.rental.saas.product.mapper.SpecialPricingMapper;
import com.rental.saas.product.service.SpecialPricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 特殊定价服务实现类
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpecialPricingServiceImpl extends ServiceImpl<SpecialPricingMapper, SpecialPricing> implements SpecialPricingService {

    private final SpecialPricingMapper specialPricingMapper;

    @Override
    public boolean createPricing(SpecialPricing pricing) {
        log.info("创建特殊定价: 商品ID={}, 定价日期={}", pricing.getProductId(), pricing.getPriceDate());
        
        // 检查是否已存在相同日期的定价
        if (getPricingByProductAndDate(pricing.getProductId(), pricing.getPriceDate(), pricing.getTenantId()) != null) {
            log.warn("特殊定价已存在: 商品ID={}, 定价日期={}", pricing.getProductId(), pricing.getPriceDate());
            return false;
        }
        
        return save(pricing);
    }

    @Override
    public boolean batchCreatePricing(List<SpecialPricing> pricings) {
        log.info("批量创建特殊定价: 数量={}", pricings.size());
        return saveBatch(pricings);
    }

    @Override
    public boolean updatePricing(SpecialPricing pricing) {
        log.info("更新特殊定价: ID={}", pricing.getId());
        return updateById(pricing);
    }

    @Override
    public boolean deletePricing(Long id) {
        log.info("删除特殊定价: ID={}", id);
        return removeById(id);
    }

    @Override
    public SpecialPricing getPricingById(Long id) {
        log.info("根据ID获取特殊定价: ID={}", id);
        return getById(id);
    }

    @Override
    public Page<SpecialPricing> getPricingList(int current, int size, Long productId, LocalDate startDate, LocalDate endDate, Long tenantId) {
        log.info("分页查询特殊定价: current={}, size={}, productId={}, startDate={}, endDate={}, tenantId={}", 
                current, size, productId, startDate, endDate, tenantId);
        
        LambdaQueryWrapper<SpecialPricing> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpecialPricing::getDeleted, 0)
               .eq(SpecialPricing::getTenantId, tenantId)
               .eq(productId != null, SpecialPricing::getProductId, productId)
               .ge(startDate != null, SpecialPricing::getPriceDate, startDate)
               .le(endDate != null, SpecialPricing::getPriceDate, endDate)
               .orderByDesc(SpecialPricing::getPriceDate);
        
        Page<SpecialPricing> page = new Page<>(current, size);
        return page(page, wrapper);
    }

    @Override
    public List<SpecialPricing> getPricingsByProduct(Long productId, Long tenantId) {
        log.info("根据商品ID获取所有特殊定价: 商品ID={}, tenantId={}", productId, tenantId);
        
        LambdaQueryWrapper<SpecialPricing> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpecialPricing::getDeleted, 0)
               .eq(SpecialPricing::getTenantId, tenantId)
               .eq(SpecialPricing::getProductId, productId)
               .orderByDesc(SpecialPricing::getPriceDate);
        
        return list(wrapper);
    }

    @Override
    public SpecialPricing getPricingByProductAndDate(Long productId, LocalDate priceDate, Long tenantId) {
        log.info("根据商品ID和日期获取特殊定价: 商品ID={}, 定价日期={}, tenantId={}", productId, priceDate, tenantId);
        
        LambdaQueryWrapper<SpecialPricing> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpecialPricing::getDeleted, 0)
               .eq(SpecialPricing::getTenantId, tenantId)
               .eq(SpecialPricing::getProductId, productId)
               .eq(SpecialPricing::getPriceDate, priceDate);
        
        return getOne(wrapper);
    }

    @Override
    public boolean deletePricingsByProduct(Long productId, Long tenantId) {
        log.info("删除商品的所有特殊定价: 商品ID={}, tenantId={}", productId, tenantId);
        
        LambdaQueryWrapper<SpecialPricing> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpecialPricing::getTenantId, tenantId)
               .eq(SpecialPricing::getProductId, productId)
               .eq(SpecialPricing::getDeleted, 0);
        
        return remove(wrapper);
    }
}
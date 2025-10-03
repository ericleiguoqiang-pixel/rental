package com.rental.saas.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.saas.product.entity.SpecialPricing;
import org.apache.ibatis.annotations.Mapper;

/**
 * 特殊定价数据访问层
 * 
 * @author Rental SaaS Team
 */
@Mapper
public interface SpecialPricingMapper extends BaseMapper<SpecialPricing> {
}
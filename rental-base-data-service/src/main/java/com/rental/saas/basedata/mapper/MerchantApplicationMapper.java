package com.rental.saas.basedata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.saas.basedata.entity.MerchantApplication;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户申请Mapper接口
 * 
 * @author Rental SaaS Team
 */
@Mapper
public interface MerchantApplicationMapper extends BaseMapper<MerchantApplication> {
}
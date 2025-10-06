package com.rental.saas.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.saas.payment.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付记录Mapper接口
 * 
 * @author Rental SaaS Team
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {
}
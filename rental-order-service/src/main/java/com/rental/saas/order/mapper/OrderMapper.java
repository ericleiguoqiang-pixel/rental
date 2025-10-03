package com.rental.saas.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.saas.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper接口
 * 
 * @author Rental SaaS Team
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
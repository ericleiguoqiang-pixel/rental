package com.rental.saas.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.saas.order.entity.OrderStatusLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单状态变更记录Mapper接口
 * 
 * @author Rental SaaS Team
 */
@Mapper
public interface OrderStatusLogMapper extends BaseMapper<OrderStatusLog> {
}
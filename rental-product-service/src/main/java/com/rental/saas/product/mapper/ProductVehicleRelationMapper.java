package com.rental.saas.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.saas.product.entity.ProductVehicleRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车型商品关联车辆数据访问层
 * 
 * @author Rental SaaS Team
 */
@Mapper
public interface ProductVehicleRelationMapper extends BaseMapper<ProductVehicleRelation> {
}
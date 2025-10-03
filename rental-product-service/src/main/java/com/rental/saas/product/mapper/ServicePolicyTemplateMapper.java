package com.rental.saas.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.saas.product.entity.ServicePolicyTemplate;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务政策模板数据访问层
 * 
 * @author Rental SaaS Team
 */
@Mapper
public interface ServicePolicyTemplateMapper extends BaseMapper<ServicePolicyTemplate> {
}
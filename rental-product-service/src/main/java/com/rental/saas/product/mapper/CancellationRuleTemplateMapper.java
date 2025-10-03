package com.rental.saas.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.saas.product.entity.CancellationRuleTemplate;
import org.apache.ibatis.annotations.Mapper;

/**
 * 取消规则模板数据访问层
 * 
 * @author Rental SaaS Team
 */
@Mapper
public interface CancellationRuleTemplateMapper extends BaseMapper<CancellationRuleTemplate> {
}
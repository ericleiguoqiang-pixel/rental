package com.rental.saas.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.saas.user.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户Mapper
 * 
 * @author Rental SaaS Team
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {

}
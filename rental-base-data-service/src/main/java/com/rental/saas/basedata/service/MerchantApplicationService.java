package com.rental.saas.basedata.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rental.saas.basedata.entity.MerchantApplication;

/**
 * 商户申请服务接口（用于运营审核）
 * 
 * @author Rental SaaS Team
 */
public interface MerchantApplicationService {

    /**
     * 获取待审核商户申请列表（分页）
     */
    IPage<MerchantApplication> getPendingApplications(Integer current, Integer size);

    /**
     * 获取所有商户申请列表（分页）
     */
    IPage<MerchantApplication> getAllApplications(Integer current, Integer size, String status);

    /**
     * 根据ID获取商户申请
     */
    MerchantApplication getById(Long id);

    /**
     * 审核商户申请
     */
    boolean auditApplication(Long id, String status, String reason);
}
package com.rental.saas.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.saas.common.response.PageResponse;
import com.rental.saas.user.dto.request.MerchantApplicationRequest;
import com.rental.saas.user.dto.response.MerchantApplicationResponse;
import com.rental.saas.user.entity.MerchantApplication;

/**
 * 商户入驻申请服务接口
 * 
 * @author Rental SaaS Team
 */
public interface MerchantApplicationService extends IService<MerchantApplication> {

    /**
     * 提交商户入驻申请
     */
    String submitApplication(MerchantApplicationRequest request);

    /**
     * 审核商户入驻申请
     */
    void auditApplication(Long id, Integer status, String remark, Long auditorId);

    /**
     * 分页查询商户入驻申请
     */
    PageResponse<MerchantApplicationResponse> pageApplications(Integer pageNum, Integer pageSize, 
                                                               Integer status, String keyword);

    /**
     * 根据ID查询商户入驻申请详情
     */
    MerchantApplicationResponse getApplicationById(Long id);

    /**
     * 根据申请编号查询商户入驻申请
     */
    MerchantApplicationResponse getApplicationByNo(String applicationNo);
}
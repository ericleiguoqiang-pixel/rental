package com.rental.saas.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.saas.common.response.PageResponse;
import com.rental.saas.user.dto.request.MerchantApplicationRequest;
import com.rental.saas.user.dto.response.MerchantApplicationResponse;
import com.rental.saas.user.entity.MerchantApplication;

import java.util.Map;

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
    boolean auditApplication(Long id, String status, String remark, Long auditorId);

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
    
    /**
     * 获取待审核商户申请列表（分页）
     */
    IPage<MerchantApplication> getPendingApplications(Integer current, Integer size);
    
    /**
     * 获取所有商户申请列表（分页）
     */
    IPage<MerchantApplication> getAllApplications(Integer current, Integer size, String status);
    
    /**
     * 统计待审核商户申请数量
     */
    int countPendingApplications();
    
    /**
     * 统计各状态商户申请数量
     */
    Map<String, Integer> countApplicationsByStatus();
}
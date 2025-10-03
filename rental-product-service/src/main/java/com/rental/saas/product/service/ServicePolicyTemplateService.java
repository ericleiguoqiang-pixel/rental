package com.rental.saas.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.saas.product.entity.ServicePolicyTemplate;

import java.util.List;

/**
 * 服务政策模板服务接口
 * 
 * @author Rental SaaS Team
 */
public interface ServicePolicyTemplateService extends IService<ServicePolicyTemplate> {

    /**
     * 创建服务政策模板
     */
    boolean createTemplate(ServicePolicyTemplate template);

    /**
     * 更新服务政策模板
     */
    boolean updateTemplate(ServicePolicyTemplate template);

    /**
     * 删除服务政策模板
     */
    boolean deleteTemplate(Long id);

    /**
     * 根据ID获取服务政策模板
     */
    ServicePolicyTemplate getTemplateById(Long id);

    /**
     * 分页查询服务政策模板
     */
    Page<ServicePolicyTemplate> getTemplateList(int current, int size, String templateName, Long tenantId);

    /**
     * 获取所有服务政策模板
     */
    List<ServicePolicyTemplate> getAllTemplates();
}
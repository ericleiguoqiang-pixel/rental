package com.rental.saas.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.saas.product.entity.ValueAddedServiceTemplate;

import java.util.List;

/**
 * 增值服务模板服务接口
 * 
 * @author Rental SaaS Team
 */
public interface ValueAddedServiceTemplateService extends IService<ValueAddedServiceTemplate> {

    /**
     * 创建增值服务模板
     */
    boolean createTemplate(ValueAddedServiceTemplate template);

    /**
     * 更新增值服务模板
     */
    boolean updateTemplate(ValueAddedServiceTemplate template);

    /**
     * 删除增值服务模板
     */
    boolean deleteTemplate(Long id);

    /**
     * 根据ID获取增值服务模板
     */
    ValueAddedServiceTemplate getTemplateById(Long id);

    /**
     * 分页查询增值服务模板
     */
    Page<ValueAddedServiceTemplate> getTemplateList(int current, int size, Integer serviceType, Long tenantId);

    /**
     * 获取所有增值服务模板
     */
    List<ValueAddedServiceTemplate> getAllTemplates();

    /**
     * 根据服务类型查询模板
     */
    List<ValueAddedServiceTemplate> getTemplatesByServiceType(Integer serviceType);
}
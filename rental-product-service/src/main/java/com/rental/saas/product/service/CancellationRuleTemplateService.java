package com.rental.saas.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.saas.product.entity.CancellationRuleTemplate;

import java.util.List;

/**
 * 取消规则模板服务接口
 * 
 * @author Rental SaaS Team
 */
public interface CancellationRuleTemplateService extends IService<CancellationRuleTemplate> {

    /**
     * 创建取消规则模板
     */
    boolean createTemplate(CancellationRuleTemplate template);

    /**
     * 更新取消规则模板
     */
    boolean updateTemplate(CancellationRuleTemplate template);

    /**
     * 删除取消规则模板
     */
    boolean deleteTemplate(Long id);

    /**
     * 根据ID获取取消规则模板
     */
    CancellationRuleTemplate getTemplateById(Long id);

    /**
     * 分页查询取消规则模板
     */
    Page<CancellationRuleTemplate> getTemplateList(int current, int size, String templateName, Long tenantId);

    /**
     * 获取所有取消规则模板
     */
    List<CancellationRuleTemplate> getAllTemplates();
}
package com.rental.saas.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.saas.product.entity.CancellationRuleTemplate;
import com.rental.saas.product.mapper.CancellationRuleTemplateMapper;
import com.rental.saas.product.service.CancellationRuleTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 取消规则模板服务实现类
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CancellationRuleTemplateServiceImpl extends ServiceImpl<CancellationRuleTemplateMapper, CancellationRuleTemplate> implements CancellationRuleTemplateService {

    private final CancellationRuleTemplateMapper cancellationRuleTemplateMapper;

    @Override
    public boolean createTemplate(CancellationRuleTemplate template) {
        log.info("创建取消规则模板: {}", template.getTemplateName());
        return save(template);
    }

    @Override
    public boolean updateTemplate(CancellationRuleTemplate template) {
        log.info("更新取消规则模板: ID={}, 名称={}", template.getId(), template.getTemplateName());
        return updateById(template);
    }

    @Override
    public boolean deleteTemplate(Long id) {
        log.info("删除取消规则模板: ID={}", id);
        return removeById(id);
    }

    @Override
    public CancellationRuleTemplate getTemplateById(Long id) {
        log.info("根据ID获取取消规则模板: ID={}", id);
        return getById(id);
    }

    @Override
    public Page<CancellationRuleTemplate> getTemplateList(int current, int size, String templateName, Long tenantId) {
        log.info("分页查询取消规则模板: current={}, size={}, templateName={}", current, size, templateName);
        
        LambdaQueryWrapper<CancellationRuleTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CancellationRuleTemplate::getDeleted, 0)
                .eq(CancellationRuleTemplate::getTenantId, tenantId)
               .like(StringUtils.hasText(templateName), CancellationRuleTemplate::getTemplateName, templateName)
               .orderByDesc(CancellationRuleTemplate::getCreatedTime);
        
        Page<CancellationRuleTemplate> page = new Page<>(current, size);
        return page(page, wrapper);
    }

    @Override
    public List<CancellationRuleTemplate> getAllTemplates() {
        log.info("获取所有取消规则模板");
        
        LambdaQueryWrapper<CancellationRuleTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CancellationRuleTemplate::getDeleted, 0)
               .orderByDesc(CancellationRuleTemplate::getCreatedTime);
        
        return list(wrapper);
    }
}
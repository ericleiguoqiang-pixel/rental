package com.rental.saas.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.saas.product.entity.ServicePolicyTemplate;
import com.rental.saas.product.mapper.ServicePolicyTemplateMapper;
import com.rental.saas.product.service.ServicePolicyTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 服务政策模板服务实现类
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePolicyTemplateServiceImpl extends ServiceImpl<ServicePolicyTemplateMapper, ServicePolicyTemplate> implements ServicePolicyTemplateService {

    private final ServicePolicyTemplateMapper servicePolicyTemplateMapper;

    @Override
    public boolean createTemplate(ServicePolicyTemplate template) {
        log.info("创建服务政策模板: {}", template.getTemplateName());
        return save(template);
    }

    @Override
    public boolean updateTemplate(ServicePolicyTemplate template) {
        log.info("更新服务政策模板: ID={}, 名称={}", template.getId(), template.getTemplateName());
        return updateById(template);
    }

    @Override
    public boolean deleteTemplate(Long id) {
        log.info("删除服务政策模板: ID={}", id);
        return removeById(id);
    }

    @Override
    public ServicePolicyTemplate getTemplateById(Long id) {
        log.info("根据ID获取服务政策模板: ID={}", id);
        return getById(id);
    }

    @Override
    public Page<ServicePolicyTemplate> getTemplateList(int current, int size, String templateName, Long tenantId) {
        log.info("分页查询服务政策模板: current={}, size={}, templateName={}", current, size, templateName);
        
        LambdaQueryWrapper<ServicePolicyTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServicePolicyTemplate::getDeleted, 0)
                .eq(ServicePolicyTemplate::getTenantId, tenantId)
               .like(StringUtils.hasText(templateName), ServicePolicyTemplate::getTemplateName, templateName)
               .orderByDesc(ServicePolicyTemplate::getCreatedTime);
        
        Page<ServicePolicyTemplate> page = new Page<>(current, size);
        return page(page, wrapper);
    }

    @Override
    public List<ServicePolicyTemplate> getAllTemplates() {
        log.info("获取所有服务政策模板");
        
        LambdaQueryWrapper<ServicePolicyTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServicePolicyTemplate::getDeleted, 0)
               .orderByDesc(ServicePolicyTemplate::getCreatedTime);
        
        return list(wrapper);
    }
}
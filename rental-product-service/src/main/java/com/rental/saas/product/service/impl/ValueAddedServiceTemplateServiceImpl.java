package com.rental.saas.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.saas.product.entity.ValueAddedServiceTemplate;
import com.rental.saas.product.mapper.ValueAddedServiceTemplateMapper;
import com.rental.saas.product.service.ValueAddedServiceTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 增值服务模板服务实现类
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ValueAddedServiceTemplateServiceImpl extends ServiceImpl<ValueAddedServiceTemplateMapper, ValueAddedServiceTemplate> implements ValueAddedServiceTemplateService {

    private final ValueAddedServiceTemplateMapper valueAddedServiceTemplateMapper;

    @Override
    public boolean createTemplate(ValueAddedServiceTemplate template) {
        log.info("创建增值服务模板: {}", template.getTemplateName());
        return save(template);
    }

    @Override
    public boolean updateTemplate(ValueAddedServiceTemplate template) {
        log.info("更新增值服务模板: ID={}, 名称={}", template.getId(), template.getTemplateName());
        return updateById(template);
    }

    @Override
    public boolean deleteTemplate(Long id) {
        log.info("删除增值服务模板: ID={}", id);
        return removeById(id);
    }

    @Override
    public ValueAddedServiceTemplate getTemplateById(Long id) {
        log.info("根据ID获取增值服务模板: ID={}", id);
        return getById(id);
    }

    @Override
    public Page<ValueAddedServiceTemplate> getTemplateList(int current, int size, Integer serviceType, Long tenantId) {
        log.info("分页查询增值服务模板: current={}, size={}, serviceType={}", current, size, serviceType);
        
        LambdaQueryWrapper<ValueAddedServiceTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ValueAddedServiceTemplate::getDeleted, 0)
               .eq(serviceType != null, ValueAddedServiceTemplate::getServiceType, serviceType)
                .eq(tenantId != null, ValueAddedServiceTemplate::getTenantId, tenantId)
               .orderByDesc(ValueAddedServiceTemplate::getCreatedTime);
        
        Page<ValueAddedServiceTemplate> page = new Page<>(current, size);
        return page(page, wrapper);
    }

    @Override
    public List<ValueAddedServiceTemplate> getAllTemplates() {
        log.info("获取所有增值服务模板");
        
        LambdaQueryWrapper<ValueAddedServiceTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ValueAddedServiceTemplate::getDeleted, 0)
               .orderByAsc(ValueAddedServiceTemplate::getServiceType)
               .orderByDesc(ValueAddedServiceTemplate::getCreatedTime);
        
        return list(wrapper);
    }

    @Override
    public List<ValueAddedServiceTemplate> getTemplatesByServiceType(Integer serviceType) {
        log.info("根据服务类型查询模板: serviceType={}", serviceType);
        
        LambdaQueryWrapper<ValueAddedServiceTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ValueAddedServiceTemplate::getDeleted, 0)
               .eq(ValueAddedServiceTemplate::getServiceType, serviceType)
               .orderByDesc(ValueAddedServiceTemplate::getCreatedTime);
        
        return list(wrapper);
    }
}
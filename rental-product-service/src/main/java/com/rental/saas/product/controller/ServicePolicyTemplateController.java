package com.rental.saas.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import com.rental.saas.product.dto.request.ServicePolicyTemplateRequest;
import com.rental.saas.product.dto.response.ServicePolicyTemplateResponse;
import com.rental.saas.product.entity.ServicePolicyTemplate;
import com.rental.saas.product.service.ServicePolicyTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务政策模板控制器
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/service-policy-templates")
@RequiredArgsConstructor
@Validated
@Tag(name = "服务政策模板", description = "服务政策模板管理相关接口")
public class ServicePolicyTemplateController {

    private final ServicePolicyTemplateService servicePolicyTemplateService;

    @PostMapping
    @Operation(summary = "创建服务政策模板", description = "创建新的服务政策模板")
    public ApiResponse<Long> createTemplate(
            @Valid @RequestBody ServicePolicyTemplateRequest request,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        log.info("创建服务政策模板: {}", request.getTemplateName());
        
        ServicePolicyTemplate template = new ServicePolicyTemplate();
        BeanUtils.copyProperties(request, template);
        template.setTenantId(tenantId);
        
        boolean result = servicePolicyTemplateService.createTemplate(template);
        if (result) {
            return ApiResponse.success("创建成功", template.getId());
        } else {
            return ApiResponse.error("创建失败");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新服务政策模板", description = "更新服务政策模板信息")
    public ApiResponse<Void> updateTemplate(@PathVariable @NotNull @Min(1) Long id,
                                           @Valid @RequestBody ServicePolicyTemplateRequest request) {
        log.info("更新服务政策模板: ID={}", id);
        
        ServicePolicyTemplate template = new ServicePolicyTemplate();
        BeanUtils.copyProperties(request, template);
        template.setId(id);
        
        boolean result = servicePolicyTemplateService.updateTemplate(template);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除服务政策模板", description = "删除指定的服务政策模板")
    public ApiResponse<Void> deleteTemplate(@PathVariable @NotNull @Min(1) Long id) {
        log.info("删除服务政策模板: ID={}", id);
        
        boolean result = servicePolicyTemplateService.deleteTemplate(id);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("删除失败");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取服务政策模板详情", description = "根据ID获取服务政策模板详细信息")
    public ApiResponse<ServicePolicyTemplateResponse> getTemplate(@PathVariable @NotNull @Min(1) Long id) {
        log.info("获取服务政策模板详情: ID={}", id);
        
        ServicePolicyTemplate template = servicePolicyTemplateService.getTemplateById(id);
        if (template != null) {
            ServicePolicyTemplateResponse response = new ServicePolicyTemplateResponse();
            BeanUtils.copyProperties(template, response);
            return ApiResponse.success("查询成功", response);
        } else {
            return ApiResponse.error("模板不存在");
        }
    }

    @GetMapping
    @Operation(summary = "分页查询服务政策模板", description = "分页查询服务政策模板列表")
    public ApiResponse<PageResponse<ServicePolicyTemplateResponse>> getTemplateList(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") @Min(1) Integer current,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "模板名称") @RequestParam(required = false) String templateName,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        
        log.info("分页查询服务政策模板: current={}, size={}, templateName={}", current, size, templateName);
        
        Page<ServicePolicyTemplate> page = servicePolicyTemplateService.getTemplateList(current, size, templateName, tenantId);
        
        List<ServicePolicyTemplateResponse> responseList = page.getRecords().stream()
                .map(template -> {
                    ServicePolicyTemplateResponse response = new ServicePolicyTemplateResponse();
                    BeanUtils.copyProperties(template, response);
                    return response;
                })
                .collect(Collectors.toList());
        
        PageResponse<ServicePolicyTemplateResponse> pageResponse = PageResponse.of(
                current, size, page.getTotal(), responseList);
        
        return ApiResponse.success("查询成功", pageResponse);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有服务政策模板", description = "获取所有服务政策模板列表")
    public ApiResponse<List<ServicePolicyTemplateResponse>> getAllTemplates() {
        log.info("获取所有服务政策模板");
        
        List<ServicePolicyTemplate> templates = servicePolicyTemplateService.getAllTemplates();
        
        List<ServicePolicyTemplateResponse> responseList = templates.stream()
                .map(template -> {
                    ServicePolicyTemplateResponse response = new ServicePolicyTemplateResponse();
                    BeanUtils.copyProperties(template, response);
                    return response;
                })
                .collect(Collectors.toList());
        
        return ApiResponse.success("查询成功", responseList);
    }
}
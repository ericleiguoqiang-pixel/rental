package com.rental.saas.product.controller;

import com.rental.saas.product.dto.request.ValueAddedServiceTemplateRequest;
import com.rental.saas.product.dto.response.ValueAddedServiceTemplateResponse;
import com.rental.saas.product.entity.ValueAddedServiceTemplate;
import com.rental.saas.product.service.ValueAddedServiceTemplateService;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 增值服务模板控制器
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/value-added-service-templates")
@RequiredArgsConstructor
@Validated
@Tag(name = "增值服务模板", description = "增值服务模板管理相关接口")
public class ValueAddedServiceTemplateController {

    private final ValueAddedServiceTemplateService valueAddedServiceTemplateService;

    @PostMapping
    @Operation(summary = "创建增值服务模板", description = "创建新的增值服务模板")
    public ApiResponse<Long> createTemplate(
            @Valid @RequestBody ValueAddedServiceTemplateRequest request,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        log.info("创建增值服务模板: {}", request.getTemplateName());
        
        ValueAddedServiceTemplate template = new ValueAddedServiceTemplate();
        BeanUtils.copyProperties(request, template);
        template.setTenantId(tenantId);
        
        boolean result = valueAddedServiceTemplateService.createTemplate(template);
        if (result) {
            return ApiResponse.success("创建成功", template.getId());
        } else {
            return ApiResponse.error("创建失败");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新增值服务模板", description = "更新增值服务模板信息")
    public ApiResponse<Void> updateTemplate(@PathVariable @NotNull @Min(1) Long id,
                                           @Valid @RequestBody ValueAddedServiceTemplateRequest request) {
        log.info("更新增值服务模板: ID={}", id);
        
        ValueAddedServiceTemplate template = new ValueAddedServiceTemplate();
        BeanUtils.copyProperties(request, template);
        template.setId(id);
        
        boolean result = valueAddedServiceTemplateService.updateTemplate(template);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除增值服务模板", description = "删除指定的增值服务模板")
    public ApiResponse<Void> deleteTemplate(@PathVariable @NotNull @Min(1) Long id) {
        log.info("删除增值服务模板: ID={}", id);
        
        boolean result = valueAddedServiceTemplateService.deleteTemplate(id);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("删除失败");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取增值服务模板详情", description = "根据ID获取增值服务模板详细信息")
    public ApiResponse<ValueAddedServiceTemplateResponse> getTemplate(@PathVariable @NotNull @Min(1) Long id) {
        log.info("获取增值服务模板详情: ID={}", id);
        
        ValueAddedServiceTemplate template = valueAddedServiceTemplateService.getTemplateById(id);
        if (template != null) {
            ValueAddedServiceTemplateResponse response = new ValueAddedServiceTemplateResponse();
            BeanUtils.copyProperties(template, response);
            return ApiResponse.success("查询成功", response);
        } else {
            return ApiResponse.error("模板不存在");
        }
    }

    @GetMapping
    @Operation(summary = "分页查询增值服务模板", description = "分页查询增值服务模板列表")
    public ApiResponse<PageResponse<ValueAddedServiceTemplateResponse>> getTemplateList(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") @Min(1) Integer current,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "服务类型") @RequestParam(required = false) Integer serviceType,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        
        log.info("分页查询增值服务模板: current={}, size={}, serviceType={}", current, size, serviceType);
        
        Page<ValueAddedServiceTemplate> page = valueAddedServiceTemplateService.getTemplateList(current, size, serviceType, tenantId);
        
        List<ValueAddedServiceTemplateResponse> responseList = page.getRecords().stream()
                .map(template -> {
                    ValueAddedServiceTemplateResponse response = new ValueAddedServiceTemplateResponse();
                    BeanUtils.copyProperties(template, response);
                    return response;
                })
                .collect(Collectors.toList());
        
        PageResponse<ValueAddedServiceTemplateResponse> pageResponse = PageResponse.of(
                current, size, page.getTotal(), responseList);
        
        return ApiResponse.success("查询成功", pageResponse);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有增值服务模板", description = "获取所有增值服务模板列表")
    public ApiResponse<List<ValueAddedServiceTemplateResponse>> getAllTemplates() {
        log.info("获取所有增值服务模板");
        
        List<ValueAddedServiceTemplate> templates = valueAddedServiceTemplateService.getAllTemplates();
        
        List<ValueAddedServiceTemplateResponse> responseList = templates.stream()
                .map(template -> {
                    ValueAddedServiceTemplateResponse response = new ValueAddedServiceTemplateResponse();
                    BeanUtils.copyProperties(template, response);
                    return response;
                })
                .collect(Collectors.toList());
        
        return ApiResponse.success("查询成功", responseList);
    }

    @GetMapping("/service-type/{serviceType}")
    @Operation(summary = "根据服务类型查询模板", description = "根据服务类型查询增值服务模板列表")
    public ApiResponse<List<ValueAddedServiceTemplateResponse>> getTemplatesByServiceType(
            @PathVariable @Min(1) Integer serviceType) {
        log.info("根据服务类型查询模板: serviceType={}", serviceType);
        
        List<ValueAddedServiceTemplate> templates = valueAddedServiceTemplateService.getTemplatesByServiceType(serviceType);
        
        List<ValueAddedServiceTemplateResponse> responseList = templates.stream()
                .map(template -> {
                    ValueAddedServiceTemplateResponse response = new ValueAddedServiceTemplateResponse();
                    BeanUtils.copyProperties(template, response);
                    return response;
                })
                .collect(Collectors.toList());
        
        return ApiResponse.success("查询成功", responseList);
    }
}
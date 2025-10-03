package com.rental.saas.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import com.rental.saas.product.dto.request.CancellationRuleTemplateRequest;
import com.rental.saas.product.dto.response.CancellationRuleTemplateResponse;
import com.rental.saas.product.entity.CancellationRuleTemplate;
import com.rental.saas.product.service.CancellationRuleTemplateService;
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
 * 取消规则模板控制器
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/cancellation-rule-templates")
@RequiredArgsConstructor
@Validated
@Tag(name = "取消规则模板", description = "取消规则模板管理相关接口")
public class CancellationRuleTemplateController {

    private final CancellationRuleTemplateService cancellationRuleTemplateService;

    @PostMapping
    @Operation(summary = "创建取消规则模板", description = "创建新的取消规则模板")
    public ApiResponse<Long> createTemplate(
            @Valid @RequestBody CancellationRuleTemplateRequest request,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        log.info("创建取消规则模板: {}", request.getTemplateName());
        
        CancellationRuleTemplate template = new CancellationRuleTemplate();
        BeanUtils.copyProperties(request, template);
        template.setTenantId(tenantId);
        
        boolean result = cancellationRuleTemplateService.createTemplate(template);
        if (result) {
            return ApiResponse.success("创建成功", template.getId());
        } else {
            return ApiResponse.error("创建失败");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新取消规则模板", description = "更新取消规则模板信息")
    public ApiResponse<Void> updateTemplate(@PathVariable @NotNull @Min(1) Long id,
                                           @Valid @RequestBody CancellationRuleTemplateRequest request) {
        log.info("更新取消规则模板: ID={}", id);
        
        CancellationRuleTemplate template = new CancellationRuleTemplate();
        BeanUtils.copyProperties(request, template);
        template.setId(id);
        
        boolean result = cancellationRuleTemplateService.updateTemplate(template);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除取消规则模板", description = "删除指定的取消规则模板")
    public ApiResponse<Void> deleteTemplate(@PathVariable @NotNull @Min(1) Long id) {
        log.info("删除取消规则模板: ID={}", id);
        
        boolean result = cancellationRuleTemplateService.deleteTemplate(id);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("删除失败");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取取消规则模板详情", description = "根据ID获取取消规则模板详细信息")
    public ApiResponse<CancellationRuleTemplateResponse> getTemplate(@PathVariable @NotNull @Min(1) Long id) {
        log.info("获取取消规则模板详情: ID={}", id);
        
        CancellationRuleTemplate template = cancellationRuleTemplateService.getTemplateById(id);
        if (template != null) {
            CancellationRuleTemplateResponse response = new CancellationRuleTemplateResponse();
            BeanUtils.copyProperties(template, response);
            return ApiResponse.success("查询成功", response);
        } else {
            return ApiResponse.error("模板不存在");
        }
    }

    @GetMapping
    @Operation(summary = "分页查询取消规则模板", description = "分页查询取消规则模板列表")
    public ApiResponse<PageResponse<CancellationRuleTemplateResponse>> getTemplateList(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") @Min(1) Integer current,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "模板名称") @RequestParam(required = false) String templateName,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        
        log.info("分页查询取消规则模板: current={}, size={}, templateName={}", current, size, templateName);
        
        Page<CancellationRuleTemplate> page = cancellationRuleTemplateService.getTemplateList(current, size, templateName, tenantId);
        
        List<CancellationRuleTemplateResponse> responseList = page.getRecords().stream()
                .map(template -> {
                    CancellationRuleTemplateResponse response = new CancellationRuleTemplateResponse();
                    BeanUtils.copyProperties(template, response);
                    return response;
                })
                .collect(Collectors.toList());
        
        PageResponse<CancellationRuleTemplateResponse> pageResponse = PageResponse.of(
                current, size, page.getTotal(), responseList);
        
        return ApiResponse.success("查询成功", pageResponse);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有取消规则模板", description = "获取所有取消规则模板列表")
    public ApiResponse<List<CancellationRuleTemplateResponse>> getAllTemplates() {
        log.info("获取所有取消规则模板");
        
        List<CancellationRuleTemplate> templates = cancellationRuleTemplateService.getAllTemplates();
        
        List<CancellationRuleTemplateResponse> responseList = templates.stream()
                .map(template -> {
                    CancellationRuleTemplateResponse response = new CancellationRuleTemplateResponse();
                    BeanUtils.copyProperties(template, response);
                    return response;
                })
                .collect(Collectors.toList());
        
        return ApiResponse.success("查询成功", responseList);
    }
}
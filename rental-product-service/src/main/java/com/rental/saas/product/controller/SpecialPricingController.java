package com.rental.saas.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import com.rental.saas.product.dto.request.BatchSpecialPricingRequest;
import com.rental.saas.product.dto.request.SpecialPricingRequest;
import com.rental.saas.product.dto.response.SpecialPricingResponse;
import com.rental.saas.product.entity.SpecialPricing;
import com.rental.saas.product.service.SpecialPricingService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 特殊定价控制器
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/special-pricings")
@RequiredArgsConstructor
@Validated
@Tag(name = "特殊定价", description = "特殊定价管理相关接口")
public class SpecialPricingController {

    private final SpecialPricingService specialPricingService;

    @PostMapping
    @Operation(summary = "创建特殊定价", description = "创建新的特殊定价")
    public ApiResponse<Long> createPricing(@RequestHeader("X-Tenant-Id") Long tenantId,
                                        @Valid @RequestBody SpecialPricingRequest request) {
        log.info("创建特殊定价: 商品ID={}, 定价日期={}", request.getProductId(), request.getPriceDate());
        
        SpecialPricing pricing = new SpecialPricing();
        BeanUtils.copyProperties(request, pricing);
        
        // 设置租户ID
        pricing.setTenantId(tenantId);
        
        boolean result = specialPricingService.createPricing(pricing);
        if (result) {
            return ApiResponse.success("创建成功", pricing.getId());
        } else {
            return ApiResponse.error("创建失败，定价可能已存在");
        }
    }

    @PostMapping("/batch")
    @Operation(summary = "批量创建特殊定价", description = "批量创建特殊定价")
    public ApiResponse<Void> batchCreatePricing(@RequestHeader("X-Tenant-Id") Long tenantId,
                                            @Valid @RequestBody List<SpecialPricingRequest> requests) {
        log.info("批量创建特殊定价: 数量={}", requests.size());
        
        List<SpecialPricing> pricings = requests.stream().map(request -> {
            SpecialPricing pricing = new SpecialPricing();
            BeanUtils.copyProperties(request, pricing);
            // 设置租户ID
            pricing.setTenantId(tenantId);
            return pricing;
        }).collect(Collectors.toList());
        
        boolean result = specialPricingService.batchCreatePricing(pricings);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("批量创建失败");
        }
    }
    
    @PostMapping("/batch-for-product")
    @Operation(summary = "为商品批量设置特殊定价", description = "为指定商品批量设置特殊定价，会先删除该商品的所有现有关联")
    public ApiResponse<Void> batchCreatePricingForProduct(@RequestHeader("X-Tenant-Id") Long tenantId,
                                                   @Valid @RequestBody BatchSpecialPricingRequest request) {
        log.info("为商品批量设置特殊定价: 商品ID={}, 定价数量={}", request.getProductId(), request.getPricings().size());
        
        // 先检查商品是否属于当前租户
        // 这里需要调用商品服务验证商品的租户ID，暂时跳过验证
        
        // 先删除该商品的所有现有关联
        specialPricingService.deletePricingsByProduct(request.getProductId(), tenantId);
        
        // 创建新的定价
        List<SpecialPricing> pricings = request.getPricings().stream()
                .map(item -> {
                    SpecialPricing pricing = new SpecialPricing();
                    pricing.setProductId(request.getProductId());
                    pricing.setPriceDate(item.getPriceDate());
                    pricing.setPrice(item.getPrice());
                    // 设置租户ID
                    pricing.setTenantId(tenantId);
                    return pricing;
                })
                .collect(Collectors.toList());
        
        boolean result = specialPricingService.batchCreatePricing(pricings);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("设置失败");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新特殊定价", description = "更新特殊定价信息")
    public ApiResponse<Void> updatePricing(@RequestHeader("X-Tenant-Id") Long tenantId,
                                          @PathVariable @NotNull @Min(1) Long id,
                                          @Valid @RequestBody SpecialPricingRequest request) {
        log.info("更新特殊定价: ID={}", id);
        
        // 先检查定价是否存在且属于当前租户
        SpecialPricing existingPricing = specialPricingService.getPricingById(id);
        if (existingPricing == null) {
            return ApiResponse.error("定价不存在");
        }
        
        if (!tenantId.equals(existingPricing.getTenantId())) {
            return ApiResponse.error("无权限操作该定价");
        }
        
        SpecialPricing pricing = new SpecialPricing();
        BeanUtils.copyProperties(request, pricing);
        pricing.setId(id);
        
        boolean result = specialPricingService.updatePricing(pricing);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除特殊定价", description = "删除指定的特殊定价")
    public ApiResponse<Void> deletePricing(@RequestHeader("X-Tenant-Id") Long tenantId,
                                        @PathVariable @NotNull @Min(1) Long id) {
        log.info("删除特殊定价: ID={}", id);
        
        // 先检查定价是否存在且属于当前租户
        SpecialPricing existingPricing = specialPricingService.getPricingById(id);
        if (existingPricing == null) {
            return ApiResponse.error("定价不存在");
        }
        
        if (!tenantId.equals(existingPricing.getTenantId())) {
            return ApiResponse.error("无权限操作该定价");
        }
        
        boolean result = specialPricingService.deletePricing(id);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("删除失败");
        }
    }

    @DeleteMapping("/product/{productId}")
    @Operation(summary = "删除商品的所有特殊定价", description = "删除指定商品的所有特殊定价")
    public ApiResponse<Void> deletePricingsByProduct(@RequestHeader("X-Tenant-Id") Long tenantId,
                                            @PathVariable @NotNull @Min(1) Long productId) {
        log.info("删除商品的所有特殊定价: 商品ID={}", productId);
        
        // 这里需要验证商品是否属于当前租户，暂时跳过验证
        
        boolean result = specialPricingService.deletePricingsByProduct(productId, tenantId);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("删除失败");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取特殊定价详情", description = "根据ID获取特殊定价详细信息")
    public ApiResponse<SpecialPricingResponse> getPricing(@RequestHeader("X-Tenant-Id") Long tenantId,
                                                      @PathVariable @NotNull @Min(1) Long id) {
        log.info("获取特殊定价详情: ID={}", id);
        
        SpecialPricing pricing = specialPricingService.getPricingById(id);
        if (pricing != null) {
            // 验证定价是否属于当前租户
            if (!tenantId.equals(pricing.getTenantId())) {
                return ApiResponse.error("无权限访问该定价");
            }
            
            SpecialPricingResponse response = new SpecialPricingResponse();
            BeanUtils.copyProperties(pricing, response);
            return ApiResponse.success("查询成功", response);
        } else {
            return ApiResponse.error("定价不存在");
        }
    }

    @GetMapping
    @Operation(summary = "分页查询特殊定价", description = "分页查询特殊定价列表")
    public ApiResponse<PageResponse<SpecialPricingResponse>> getPricingList(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") @Min(1) Integer current,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "商品ID") @RequestParam(required = false) Long productId,
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate) {
        
        log.info("分页查询特殊定价: current={}, size={}, productId={}, startDate={}, endDate={}", 
                current, size, productId, startDate, endDate);
        
        // 在服务层查询时添加租户ID过滤
        Page<SpecialPricing> page = specialPricingService.getPricingList(current, size, productId, startDate, endDate, tenantId);
        
        List<SpecialPricingResponse> responseList = page.getRecords().stream()
                .map(pricing -> {
                    SpecialPricingResponse response = new SpecialPricingResponse();
                    BeanUtils.copyProperties(pricing, response);
                    return response;
                })
                .collect(Collectors.toList());
        
        PageResponse<SpecialPricingResponse> pageResponse = PageResponse.of(
                current, size, page.getTotal(), responseList);
        
        return ApiResponse.success("查询成功", pageResponse);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "获取商品的所有特殊定价", description = "获取指定商品的所有特殊定价")
    public ApiResponse<List<SpecialPricingResponse>> getPricingsByProduct(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @PathVariable @NotNull @Min(1) Long productId) {
        log.info("获取商品的所有特殊定价: 商品ID={}", productId);
        
        List<SpecialPricing> pricings = specialPricingService.getPricingsByProduct(productId, tenantId);
        
        List<SpecialPricingResponse> responseList = pricings.stream()
                .map(pricing -> {
                    SpecialPricingResponse response = new SpecialPricingResponse();
                    BeanUtils.copyProperties(pricing, response);
                    return response;
                })
                .collect(Collectors.toList());
        
        return ApiResponse.success("查询成功", responseList);
    }
    
    @GetMapping("/product/{productId}/calendar")
    @Operation(summary = "获取商品特殊定价日历视图", description = "获取指定商品的特殊定价，按日期组织，便于日历展示")
    public ApiResponse<Map<LocalDate, SpecialPricingResponse>> getPricingsByProductForCalendar(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @PathVariable @NotNull @Min(1) Long productId,
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate) {
        log.info("获取商品特殊定价日历视图: 商品ID={}, startDate={}, endDate={}", productId, startDate, endDate);
        
        List<SpecialPricing> pricings = specialPricingService.getPricingsByProduct(productId, tenantId);
        
        // 如果指定了日期范围，进行过滤
        if (startDate != null || endDate != null) {
            pricings = pricings.stream()
                    .filter(pricing -> {
                        LocalDate date = pricing.getPriceDate();
                        boolean afterStart = startDate == null || !date.isBefore(startDate);
                        boolean beforeEnd = endDate == null || !date.isAfter(endDate);
                        return afterStart && beforeEnd;
                    })
                    .collect(Collectors.toList());
        }
        
        Map<LocalDate, SpecialPricingResponse> calendarMap = pricings.stream()
                .collect(Collectors.toMap(
                    SpecialPricing::getPriceDate,
                    pricing -> {
                        SpecialPricingResponse response = new SpecialPricingResponse();
                        BeanUtils.copyProperties(pricing, response);
                        return response;
                    }
                ));
        
        return ApiResponse.success("查询成功", calendarMap);
    }

    @GetMapping("/product/{productId}/date/{priceDate}")
    @Operation(summary = "根据商品ID和日期获取特殊定价", description = "根据商品ID和日期获取特殊定价")
    public ApiResponse<SpecialPricingResponse> getPricingByProductAndDate(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @PathVariable @NotNull @Min(1) Long productId,
            @PathVariable LocalDate priceDate) {
        log.info("根据商品ID和日期获取特殊定价: 商品ID={}, 定价日期={}", productId, priceDate);
        
        SpecialPricing pricing = specialPricingService.getPricingByProductAndDate(productId, priceDate, tenantId);
        if (pricing != null) {
            // 验证定价是否属于当前租户
            if (!tenantId.equals(pricing.getTenantId())) {
                return ApiResponse.error("无权限访问该定价");
            }
            
            SpecialPricingResponse response = new SpecialPricingResponse();
            BeanUtils.copyProperties(pricing, response);
            return ApiResponse.success("查询成功", response);
        } else {
            return ApiResponse.error("定价不存在");
        }
    }
}
package com.rental.saas.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import com.rental.saas.user.entity.MerchantApplication;
import com.rental.saas.user.service.MerchantApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 运营商户审核控制器
 * 提供运营人员对商户申请的审核管理功能
 *
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/operation/merchants")
@Tag(name = "运营商户审核接口", description = "运营MIS系统商户审核相关接口")
@RequiredArgsConstructor
@Validated
public class OperationMerchantController {

    private final MerchantApplicationService merchantApplicationService;

    /**
     * 获取待审核商户申请列表（分页）
     */
    @GetMapping("/pending")
    @Operation(summary = "获取待审核商户申请列表", description = "分页获取待审核的商户申请列表")
    public ApiResponse<PageResponse<MerchantApplication>> getPendingMerchants(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("获取待审核商户申请列表: current={}, size={}", current, size);
        
        try {
            IPage<MerchantApplication> page = merchantApplicationService.getPendingApplications(current, size);
            PageResponse<MerchantApplication> response = new PageResponse<>(
                current, 
                size, 
                page.getTotal(), 
                page.getRecords()
            );
            log.info("获取待审核商户申请列表成功，共{}条记录", page.getTotal());
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("获取待审核商户申请列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取待审核商户申请列表失败");
        }
    }

    /**
     * 获取所有商户申请列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取所有商户申请列表", description = "分页获取所有商户申请列表")
    public ApiResponse<PageResponse<MerchantApplication>> getAllMerchants(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "申请状态") @RequestParam(required = false) String status) {
        
        log.info("获取所有商户申请列表: current={}, size={}, status={}", current, size, status);
        
        try {
            IPage<MerchantApplication> page = merchantApplicationService.getAllApplications(current, size, status);
            PageResponse<MerchantApplication> response = new PageResponse<>(
                current, 
                size, 
                page.getTotal(), 
                page.getRecords()
            );
            log.info("获取所有商户申请列表成功，共{}条记录", page.getTotal());
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("获取所有商户申请列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取所有商户申请列表失败");
        }
    }

    /**
     * 获取商户申请详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取商户申请详情", description = "根据ID获取商户申请详情")
    public ApiResponse<MerchantApplication> getMerchantDetail(
            @Parameter(description = "商户申请ID") @PathVariable Long id) {
        
        log.info("获取商户申请详情: id={}", id);
        
        try {
            MerchantApplication merchant = merchantApplicationService.getById(id);
            if (merchant == null) {
                log.warn("商户申请不存在: id={}", id);
                return ApiResponse.error("商户申请不存在");
            }
            log.info("获取商户申请详情成功: id={}", id);
            return ApiResponse.success(merchant);
        } catch (Exception e) {
            log.error("获取商户申请详情失败: id={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("获取商户申请详情失败");
        }
    }

    /**
     * 审核商户申请
     */
    @PutMapping("/{id}/audit")
    @Operation(summary = "审核商户申请", description = "审核商户申请，通过或拒绝")
    public ApiResponse<Boolean> auditMerchant(
            @Parameter(description = "商户申请ID") @PathVariable Long id,
            @Parameter(description = "审核状态") @RequestParam String status,
            @Parameter(description = "审核意见") @RequestParam(required = false) String reason) {
        
        log.info("审核商户申请: id={}, status={}, reason={}", id, status, reason);
        
        try {
            boolean result = merchantApplicationService.auditApplication(id, status, reason, 1L);
            if (result) {
                log.info("审核商户申请成功: id={}", id);
                return ApiResponse.success(true);
            } else {
                log.warn("审核商户申请失败: id={}", id);
                return ApiResponse.error("审核商户申请失败");
            }
        } catch (Exception e) {
            log.error("审核商户申请异常: id={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("审核商户申请异常: " + e.getMessage());
        }
    }
}
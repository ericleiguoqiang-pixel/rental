package com.rental.saas.user.controller;

import com.rental.saas.common.annotation.OperationLog;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import com.rental.saas.user.dto.request.MerchantApplicationRequest;
import com.rental.saas.user.dto.response.MerchantApplicationResponse;
import com.rental.saas.user.service.MerchantApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 商户入驻申请控制器
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
@Validated
@Tag(name = "商户入驻管理", description = "商户入驻申请相关接口")
public class MerchantApplicationController {

    private final MerchantApplicationService merchantApplicationService;

    @PostMapping("/apply")
    @Operation(summary = "提交商户入驻申请", description = "企业提交入驻申请，等待平台审核")
    @OperationLog(type = "商户入驻", description = "提交商户入驻申请")
    public ApiResponse<String> submitApplication(@Valid @RequestBody MerchantApplicationRequest request) {
        String applicationNo = merchantApplicationService.submitApplication(request);
        return ApiResponse.success("申请提交成功", applicationNo);
    }

    @PutMapping("/{id}/audit")
    @Operation(summary = "审核商户入驻申请", description = "平台管理员审核商户入驻申请")
    @OperationLog(type = "商户入驻", description = "审核商户入驻申请")
    public ApiResponse<Void> auditApplication(
            @Parameter(description = "申请ID") @PathVariable @NotNull @Positive Long id,
            @Parameter(description = "审核状态：1-通过，2-拒绝") @RequestParam @NotNull Integer status,
            @Parameter(description = "审核备注") @RequestParam(required = false) String remark,
            @RequestHeader("X-User-Id") Long auditorId) {
        merchantApplicationService.auditApplication(id, status, remark, auditorId);
        return ApiResponse.success();
    }

    @GetMapping("/applications")
    @Operation(summary = "分页查询商户入驻申请", description = "分页查询商户入驻申请列表")
    public ApiResponse<PageResponse<MerchantApplicationResponse>> pageApplications(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "申请状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        PageResponse<MerchantApplicationResponse> result = 
                merchantApplicationService.pageApplications(pageNum, pageSize, status, keyword);
        return ApiResponse.success(result);
    }

    @GetMapping("/applications/{id}")
    @Operation(summary = "查询申请详情", description = "根据申请ID查询申请详情")
    public ApiResponse<MerchantApplicationResponse> getApplicationById(
            @Parameter(description = "申请ID") @PathVariable @NotNull @Positive Long id) {
        MerchantApplicationResponse result = merchantApplicationService.getApplicationById(id);
        return ApiResponse.success(result);
    }

    @GetMapping("/applications/by-no/{applicationNo}")
    @Operation(summary = "根据申请编号查询", description = "根据申请编号查询申请详情")
    public ApiResponse<MerchantApplicationResponse> getApplicationByNo(
            @Parameter(description = "申请编号") @PathVariable String applicationNo) {
        MerchantApplicationResponse result = merchantApplicationService.getApplicationByNo(applicationNo);
        return ApiResponse.success(result);
    }
}
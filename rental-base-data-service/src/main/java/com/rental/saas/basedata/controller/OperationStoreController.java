package com.rental.saas.basedata.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rental.saas.basedata.entity.Store;
import com.rental.saas.basedata.service.StoreService;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 运营门店审核控制器
 * 提供运营人员对门店信息的审核管理功能
 *
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/operation/stores")
@Tag(name = "运营门店审核接口", description = "运营MIS系统门店审核相关接口")
@RequiredArgsConstructor
public class OperationStoreController {

    private final StoreService storeService;

    /**
     * 获取待审核门店列表（分页）
     */
    @GetMapping("/pending")
    @Operation(summary = "获取待审核门店列表", description = "分页获取待审核的门店列表")
    public ApiResponse<PageResponse<Store>> getPendingStores(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("获取待审核门店列表: current={}, size={}", current, size);
        
        try {
            IPage<Store> page = storeService.getPendingStores(current, size);
            PageResponse<Store> response = new PageResponse<>();
            response.setPageNum(current);
            response.setPageSize(size);
            response.setTotal(page.getTotal());
            response.setRecords(page.getRecords());
            response.setPages((int) Math.ceil((double) page.getTotal() / size));
            response.setHasNext(current < response.getPages());
            response.setHasPrevious(current > 1);
            log.info("获取待审核门店列表成功，共{}条记录", page.getTotal());
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("获取待审核门店列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取待审核门店列表失败");
        }
    }

    /**
     * 获取所有门店列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取所有门店列表", description = "分页获取所有门店列表")
    public ApiResponse<PageResponse<Store>> getAllStores(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "门店状态") @RequestParam(required = false) String status) {
        
        log.info("获取所有门店列表: current={}, size={}, status={}", current, size, status);
        
        try {
            IPage<Store> page = storeService.getAllStores(current, size, status);
            PageResponse<Store> response = new PageResponse<>();
            response.setPageNum(current);
            response.setPageSize(size);
            response.setTotal(page.getTotal());
            response.setRecords(page.getRecords());
            response.setPages((int) Math.ceil((double) page.getTotal() / size));
            response.setHasNext(current < response.getPages());
            response.setHasPrevious(current > 1);
            log.info("获取所有门店列表成功，共{}条记录", page.getTotal());
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("获取所有门店列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取所有门店列表失败");
        }
    }

    /**
     * 获取门店详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取门店详情", description = "根据ID获取门店详情")
    public ApiResponse<Store> getStoreDetail(
            @Parameter(description = "门店ID") @PathVariable Long id) {
        
        log.info("获取门店详情: id={}", id);
        
        try {
            Store store = storeService.getById(id);
            if (store == null) {
                log.warn("门店不存在: id={}", id);
                return ApiResponse.error("门店不存在");
            }
            log.info("获取门店详情成功: id={}", id);
            return ApiResponse.success(store);
        } catch (Exception e) {
            log.error("获取门店详情失败: id={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("获取门店详情失败");
        }
    }

    /**
     * 审核门店
     */
    @PutMapping("/{id}/audit")
    @Operation(summary = "审核门店", description = "审核门店，通过或拒绝")
    public ApiResponse<Boolean> auditStore(
            @Parameter(description = "门店ID") @PathVariable Long id,
            @Parameter(description = "审核状态") @RequestParam String status,
            @Parameter(description = "审核意见") @RequestParam(required = false) String reason) {
        
        log.info("审核门店: id={}, status={}, reason={}", id, status, reason);
        
        try {
            boolean result = storeService.auditStore(id, status, reason);
            if (result) {
                log.info("审核门店成功: id={}", id);
                return ApiResponse.success(true);
            } else {
                log.warn("审核门店失败: id={}", id);
                return ApiResponse.error("审核门店失败");
            }
        } catch (Exception e) {
            log.error("审核门店异常: id={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("审核门店异常: " + e.getMessage());
        }
    }
}
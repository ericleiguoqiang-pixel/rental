package com.rental.saas.basedata.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.saas.basedata.dto.request.StoreCreateRequest;
import com.rental.saas.basedata.dto.request.StoreUpdateRequest;
import com.rental.saas.basedata.dto.response.StoreResponse;
import com.rental.saas.basedata.service.StoreService;
import com.rental.saas.common.annotation.OperationLog;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 门店管理控制器
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Validated
@Tag(name = "门店管理", description = "门店管理相关接口")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    @Operation(summary = "创建门店", description = "创建新的门店")
    @OperationLog(type = "门店管理", description = "创建门店")
    public ApiResponse<Long> createStore(@Valid @RequestBody StoreCreateRequest request,
                                         @RequestHeader("X-Tenant-Id") Long tenantId) {
        Long storeId = storeService.createStore(request, tenantId);
        return ApiResponse.success("门店创建成功", storeId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新门店", description = "更新门店信息")
    @OperationLog(type = "门店管理", description = "更新门店")
    public ApiResponse<Void> updateStore(@PathVariable @NotNull @Min(1) Long id,
                                         @Valid @RequestBody StoreUpdateRequest request,
                                         @RequestHeader("X-Tenant-Id") Long tenantId) {
        storeService.updateStore(id, request, tenantId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除门店", description = "删除指定门店")
    @OperationLog(type = "门店管理", description = "删除门店")
    public ApiResponse<Void> deleteStore(@PathVariable @NotNull @Min(1) Long id,
                                         @RequestHeader("X-Tenant-Id") Long tenantId) {
        storeService.deleteStore(id, tenantId);
        return ApiResponse.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询门店详情", description = "根据ID查询门店详细信息")
    public ApiResponse<StoreResponse> getStore(@PathVariable @NotNull @Min(1) Long id,
                                               @RequestHeader("X-Tenant-Id") Long tenantId) {
        StoreResponse store = storeService.getStoreById(id, tenantId);
        return ApiResponse.success("查询成功", store);
    }

    @GetMapping
    @Operation(summary = "分页查询门店列表", description = "分页查询门店列表")
    public ApiResponse<PageResponse<StoreResponse>> getStoreList(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") @Min(1) Integer current,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "城市") @RequestParam(required = false) String city,
            @Parameter(description = "审核状态") @RequestParam(required = false) Integer auditStatus,
            @Parameter(description = "上架状态") @RequestParam(required = false) Integer onlineStatus,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        
        Page<StoreResponse> page = storeService.getStoreList(current, size, city, auditStatus, onlineStatus, tenantId);
        PageResponse<StoreResponse> pageResponse = PageResponse.of(
                current, size, page.getTotal(), page.getRecords());
        return ApiResponse.success("查询成功", pageResponse);
    }

    @GetMapping("/all")
    @Operation(summary = "查询全部门店", description = "查询租户下所有门店")
    public ApiResponse<List<StoreResponse>> getAllStores(@RequestHeader("X-Tenant-Id") Long tenantId) {
        List<StoreResponse> stores = storeService.getAllStores(tenantId);
        return ApiResponse.success("查询成功", stores);
    }

    @GetMapping("/online")
    @Operation(summary = "查询在线门店", description = "查询租户下所有在线门店")
    public ApiResponse<List<StoreResponse>> getOnlineStores(@RequestHeader("X-Tenant-Id") Long tenantId) {
        List<StoreResponse> stores = storeService.getOnlineStores(tenantId);
        return ApiResponse.success("查询成功", stores);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "按城市查询门店", description = "根据城市查询门店列表")
    public ApiResponse<List<StoreResponse>> getStoresByCity(@PathVariable String city,
                                                            @RequestHeader("X-Tenant-Id") Long tenantId) {
        List<StoreResponse> stores = storeService.getStoresByCity(city, tenantId);
        return ApiResponse.success("查询成功", stores);
    }

//    @GetMapping("/nearby")
//    @Operation(summary = "查询附近门店", description = "查询指定范围内的门店")
//    public ApiResponse<List<StoreResponse>> getStoresInRange(
//            @Parameter(description = "经度") @RequestParam Double longitude,
//            @Parameter(description = "纬度") @RequestParam Double latitude,
//            @Parameter(description = "半径(公里)") @RequestParam(defaultValue = "10") Double radius) {
//
//        List<StoreResponse> stores = storeService.getStoresInRange(longitude, latitude, radius);
//        return ApiResponse.success("查询成功", stores);
//    }

    @PostMapping("/{id}/online")
    @Operation(summary = "门店上架", description = "设置门店为上架状态")
    @OperationLog(type = "门店管理", description = "门店上架")
    public ApiResponse<Void> onlineStore(@PathVariable @NotNull @Min(1) Long id,
                                         @RequestHeader("X-Tenant-Id") Long tenantId) {
        storeService.onlineStore(id, tenantId);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/offline")
    @Operation(summary = "门店下架", description = "设置门店为下架状态")
    @OperationLog(type = "门店管理", description = "门店下架")
    public ApiResponse<Void> offlineStore(@PathVariable @NotNull @Min(1) Long id,
                                          @RequestHeader("X-Tenant-Id") Long tenantId) {
        storeService.offlineStore(id, tenantId);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/audit")
    @Operation(summary = "门店审核", description = "审核门店申请")
    @OperationLog(type = "门店管理", description = "门店审核")
    public ApiResponse<Void> auditStore(@PathVariable @NotNull @Min(1) Long id,
                                        @Parameter(description = "审核状态：1-通过，2-拒绝") @RequestParam Integer auditStatus,
                                        @Parameter(description = "审核备注") @RequestParam(required = false) String auditRemark,
                                        @RequestHeader("X-User-Id") Long auditorId) {
        storeService.auditStore(id, auditStatus, auditRemark, auditorId);
        return ApiResponse.success();
    }

    @GetMapping("/count")
    @Operation(summary = "统计门店数量", description = "统计租户门店总数")
    public ApiResponse<Integer> countStores(@RequestHeader("X-Tenant-Id") Long tenantId) {
        int count = storeService.countStores(tenantId);
        return ApiResponse.success("查询成功", count);
    }

    @GetMapping("/check-name")
    @Operation(summary = "检查门店名称", description = "检查门店名称是否重复")
    public ApiResponse<Boolean> checkStoreName(@Parameter(description = "门店名称") @RequestParam String storeName,
                                               @Parameter(description = "排除的门店ID") @RequestParam(required = false) Long excludeId,
                                               @RequestHeader("X-Tenant-Id") Long tenantId) {
        boolean exists = storeService.checkStoreNameExists(storeName, tenantId, excludeId);
        return ApiResponse.success("查询成功", exists);
    }
}
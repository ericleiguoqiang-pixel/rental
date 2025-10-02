package com.rental.saas.basedata.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.saas.basedata.entity.ServiceArea;
import com.rental.saas.basedata.service.ServiceAreaService;
import com.rental.saas.common.annotation.OperationLog;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务范围控制器
 * 
 * @author Rental SaaS Team
 */
@RestController
@RequestMapping("/api/service-area")
@RequiredArgsConstructor
@Tag(name = "服务范围管理", description = "门店服务范围管理接口")
public class ServiceAreaController {

    private final ServiceAreaService serviceAreaService;

    @GetMapping("/list")
    @Operation(summary = "根据门店ID查询服务范围列表")
    public ApiResponse<List<ServiceArea>> listByStoreId(
            @Parameter(description = "门店ID", required = true) @RequestParam Long storeId,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        List<ServiceArea> serviceAreas = serviceAreaService.listByStoreId(storeId, tenantId);
        return ApiResponse.success(serviceAreas);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询服务范围")
    public ApiResponse<PageResponse<ServiceArea>> pageServiceAreas(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNo,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "门店ID") @RequestParam(required = false) Long storeId,
            @Parameter(description = "区域类型") @RequestParam(required = false) Integer areaType,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        
        IPage<ServiceArea> page = new Page<>(pageNo, pageSize);
        IPage<ServiceArea> result = serviceAreaService.pageServiceAreas(page, storeId, areaType, tenantId);
        
        PageResponse<ServiceArea> pageResponse = PageResponse.of(result);
        
        return ApiResponse.success(pageResponse);
    }

    @PostMapping("/save")
    @Operation(summary = "保存服务范围")
    @OperationLog(type = "服务范围管理", description = "保存服务范围")
    public ApiResponse<Boolean> saveServiceArea(@Valid @RequestBody ServiceArea serviceArea,
                                                @RequestHeader("X-Tenant-Id") Long tenantId) {
        boolean result = serviceAreaService.saveServiceArea(serviceArea, tenantId);
        return ApiResponse.success(result);
    }

    @PutMapping("/update")
    @Operation(summary = "更新服务范围")
    @OperationLog(type = "服务范围管理", description = "更新服务范围")
    public ApiResponse<Boolean> updateServiceArea(@Valid @RequestBody ServiceArea serviceArea,
                                                  @RequestHeader("X-Tenant-Id") Long tenantId) {
        boolean result = serviceAreaService.updateServiceArea(serviceArea, tenantId);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除服务范围")
    @OperationLog(type = "服务范围管理", description = "删除服务范围")
    public ApiResponse<Boolean> deleteServiceArea(@PathVariable Long id,
                                                  @RequestHeader("X-Tenant-Id") Long tenantId) {
        boolean result = serviceAreaService.deleteServiceArea(id, tenantId);
        return ApiResponse.success(result);
    }
}
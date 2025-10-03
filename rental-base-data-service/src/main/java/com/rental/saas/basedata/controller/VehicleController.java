package com.rental.saas.basedata.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.saas.basedata.dto.request.VehicleCreateRequest;
import com.rental.saas.basedata.dto.request.VehicleUpdateRequest;
import com.rental.api.basedata.response.VehicleResponse;
import com.rental.saas.basedata.service.VehicleService;
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
import java.util.Map;

/**
 * 车辆管理控制器
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Validated
@Tag(name = "车辆管理", description = "车辆管理相关接口")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @Operation(summary = "创建车辆", description = "创建新的车辆")
    @OperationLog(type = "车辆管理", description = "创建车辆")
    public ApiResponse<Long> createVehicle(@Valid @RequestBody VehicleCreateRequest request,
                                           @RequestHeader("X-Tenant-Id") Long tenantId) {
        Long vehicleId = vehicleService.createVehicle(request, tenantId);
        return ApiResponse.success("车辆创建成功", vehicleId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新车辆", description = "更新车辆信息")
    @OperationLog(type = "车辆管理", description = "更新车辆")
    public ApiResponse<Void> updateVehicle(@PathVariable @NotNull @Min(1) Long id,
                                           @Valid @RequestBody VehicleUpdateRequest request,
                                           @RequestHeader("X-Tenant-Id") Long tenantId) {
        vehicleService.updateVehicle(id, request, tenantId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除车辆", description = "删除指定车辆")
    @OperationLog(type = "车辆管理", description = "删除车辆")
    public ApiResponse<Void> deleteVehicle(@PathVariable @NotNull @Min(1) Long id,
                                           @RequestHeader("X-Tenant-Id") Long tenantId) {
        vehicleService.deleteVehicle(id, tenantId);
        return ApiResponse.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询车辆详情", description = "根据ID查询车辆详细信息")
    public ApiResponse<VehicleResponse> getVehicle(@PathVariable @NotNull @Min(1) Long id,
                                                   @RequestHeader("X-Tenant-Id") Long tenantId) {
        VehicleResponse vehicle = vehicleService.getVehicleById(id, tenantId);
        return ApiResponse.success("查询成功", vehicle);
    }

    @GetMapping
    @Operation(summary = "分页查询车辆列表", description = "分页查询车辆列表")
    public ApiResponse<PageResponse<VehicleResponse>> getVehicleList(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") @Min(1) Integer current,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "门店ID") @RequestParam(required = false) Long storeId,
            @Parameter(description = "车型ID") @RequestParam(required = false) Long carModelId,
            @Parameter(description = "车辆状态") @RequestParam(required = false) Integer vehicleStatus,
            @Parameter(description = "审核状态") @RequestParam(required = false) Integer auditStatus,
            @Parameter(description = "上架状态") @RequestParam(required = false) Integer onlineStatus,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        
        Page<VehicleResponse> page = vehicleService.getVehicleList(
                current, size, storeId, carModelId, vehicleStatus, auditStatus, onlineStatus, tenantId);
        PageResponse<VehicleResponse> pageResponse = PageResponse.of(
                current, size, page.getTotal(), page.getRecords());
        return ApiResponse.success("查询成功", pageResponse);
    }

    @GetMapping("/available")
    @Operation(summary = "查询可用车辆", description = "查询可用的车辆列表")
    public ApiResponse<List<VehicleResponse>> getAvailableVehicles(
            @Parameter(description = "门店ID") @RequestParam(required = false) Long storeId,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        List<VehicleResponse> vehicles = vehicleService.getAvailableVehicles(tenantId, storeId);
        return ApiResponse.success("查询成功", vehicles);
    }

    @GetMapping("/car-model/{carModelId}")
    @Operation(summary = "按车型查询车辆", description = "根据车型查询车辆列表")
    public ApiResponse<List<VehicleResponse>> getVehiclesByCarModel(@PathVariable @NotNull @Min(1) Long carModelId) {
        List<VehicleResponse> vehicles = vehicleService.getVehiclesByCarModel(carModelId);
        return ApiResponse.success("查询成功", vehicles);
    }

    @GetMapping("/license-plate/{licensePlate}")
    @Operation(summary = "按车牌查询车辆", description = "根据车牌号查询车辆")
    public ApiResponse<VehicleResponse> getVehicleByLicensePlate(@PathVariable String licensePlate,
                                                                 @RequestHeader("X-Tenant-Id") Long tenantId) {
        VehicleResponse vehicle = vehicleService.getVehicleByLicensePlate(licensePlate, tenantId);
        return ApiResponse.success("查询成功", vehicle);
    }

    @PostMapping("/{id}/online")
    @Operation(summary = "车辆上架", description = "设置车辆为上架状态")
    @OperationLog(type = "车辆管理", description = "车辆上架")
    public ApiResponse<Void> onlineVehicle(@PathVariable @NotNull @Min(1) Long id,
                                           @RequestHeader("X-Tenant-Id") Long tenantId) {
        vehicleService.onlineVehicle(id, tenantId);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/offline")
    @Operation(summary = "车辆下架", description = "设置车辆为下架状态")
    @OperationLog(type = "车辆管理", description = "车辆下架")
    public ApiResponse<Void> offlineVehicle(@PathVariable @NotNull @Min(1) Long id,
                                            @RequestHeader("X-Tenant-Id") Long tenantId) {
        vehicleService.offlineVehicle(id, tenantId);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/audit")
    @Operation(summary = "车辆审核", description = "审核车辆申请")
    @OperationLog(type = "车辆管理", description = "车辆审核")
    public ApiResponse<Void> auditVehicle(@PathVariable @NotNull @Min(1) Long id,
                                          @Parameter(description = "审核状态：1-通过，2-拒绝") @RequestParam Integer auditStatus,
                                          @Parameter(description = "审核备注") @RequestParam(required = false) String auditRemark,
                                          @RequestHeader("X-User-Id") Long auditorId) {
        vehicleService.auditVehicle(id, auditStatus, auditRemark, auditorId);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新车辆状态", description = "更新车辆运营状态")
    @OperationLog(type = "车辆管理", description = "更新车辆状态")
    public ApiResponse<Void> updateVehicleStatus(@PathVariable @NotNull @Min(1) Long id,
                                                  @Parameter(description = "车辆状态：1-空闲，2-租出，3-维修，4-报废") @RequestParam Integer vehicleStatus,
                                                  @RequestHeader("X-Tenant-Id") Long tenantId) {
        vehicleService.updateVehicleStatus(id, vehicleStatus, tenantId);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/mileage")
    @Operation(summary = "更新车辆里程", description = "更新车辆总里程数")
    @OperationLog(type = "车辆管理", description = "更新车辆里程")
    public ApiResponse<Void> updateMileage(@PathVariable @NotNull @Min(1) Long id,
                                           @Parameter(description = "里程数(公里)") @RequestParam @Min(0) Integer mileage,
                                           @RequestHeader("X-Tenant-Id") Long tenantId) {
        vehicleService.updateMileage(id, mileage, tenantId);
        return ApiResponse.success();
    }

    @GetMapping("/count")
    @Operation(summary = "统计车辆数量", description = "统计租户车辆总数")
    public ApiResponse<Integer> countVehicles(@RequestHeader("X-Tenant-Id") Long tenantId) {
        int count = vehicleService.countVehicles(tenantId);
        return ApiResponse.success("查询成功", count);
    }

    @GetMapping("/count/store/{storeId}")
    @Operation(summary = "统计门店车辆数量", description = "统计指定门店的车辆数量")
    public ApiResponse<Integer> countVehiclesByStore(@PathVariable @NotNull @Min(1) Long storeId) {
        int count = vehicleService.countVehiclesByStore(storeId);
        return ApiResponse.success("查询成功", count);
    }

    @GetMapping("/count/status")
    @Operation(summary = "统计各状态车辆数量", description = "统计租户各状态车辆数量")
    public ApiResponse<Map<String, Integer>> countVehiclesByStatus(@RequestHeader("X-Tenant-Id") Long tenantId) {
        Map<String, Integer> statusCount = vehicleService.countVehiclesByStatus(tenantId);
        return ApiResponse.success("查询成功", statusCount);
    }

    @GetMapping("/check-license-plate")
    @Operation(summary = "检查车牌号", description = "检查车牌号是否重复")
    public ApiResponse<Boolean> checkLicensePlate(@Parameter(description = "车牌号") @RequestParam String licensePlate,
                                                  @Parameter(description = "排除的车辆ID") @RequestParam(required = false) Long excludeId) {
        boolean exists = vehicleService.checkLicensePlateExists(licensePlate, excludeId);
        return ApiResponse.success("查询成功", exists);
    }

    @GetMapping("/check-vin")
    @Operation(summary = "检查车架号", description = "检查车架号是否重复")
    public ApiResponse<Boolean> checkVin(@Parameter(description = "车架号") @RequestParam String vin,
                                         @Parameter(description = "排除的车辆ID") @RequestParam(required = false) Long excludeId) {
        boolean exists = vehicleService.checkVinExists(vin, excludeId);
        return ApiResponse.success("查询成功", exists);
    }
}
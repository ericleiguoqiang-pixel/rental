package com.rental.saas.basedata.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rental.saas.basedata.entity.Vehicle;
import com.rental.saas.basedata.service.VehicleService;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 运营车辆审核控制器
 * 提供运营人员对车辆信息的审核管理功能
 *
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/operation/vehicles")
@Tag(name = "运营车辆审核接口", description = "运营MIS系统车辆审核相关接口")
@RequiredArgsConstructor
public class OperationVehicleController {

    private final VehicleService vehicleService;

    /**
     * 获取待审核车辆列表（分页）
     */
    @GetMapping("/pending")
    @Operation(summary = "获取待审核车辆列表", description = "分页获取待审核的车辆列表")
    public ApiResponse<PageResponse<Vehicle>> getPendingVehicles(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("获取待审核车辆列表: current={}, size={}", current, size);
        
        try {
            IPage<Vehicle> page = vehicleService.getPendingVehicles(current, size);
            PageResponse<Vehicle> response = new PageResponse<>();
            response.setPageNum(current);
            response.setPageSize(size);
            response.setTotal(page.getTotal());
            response.setRecords(page.getRecords());
            response.setPages((int) Math.ceil((double) page.getTotal() / size));
            response.setHasNext(current < response.getPages());
            response.setHasPrevious(current > 1);
            log.info("获取待审核车辆列表成功，共{}条记录", page.getTotal());
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("获取待审核车辆列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取待审核车辆列表失败");
        }
    }

    /**
     * 获取所有车辆列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取所有车辆列表", description = "分页获取所有车辆列表")
    public ApiResponse<PageResponse<Vehicle>> getAllVehicles(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "车辆状态") @RequestParam(required = false) String status) {
        
        log.info("获取所有车辆列表: current={}, size={}, status={}", current, size, status);
        
        try {
            IPage<Vehicle> page = vehicleService.getAllVehicles(current, size, status);
            PageResponse<Vehicle> response = new PageResponse<>();
            response.setPageNum(current);
            response.setPageSize(size);
            response.setTotal(page.getTotal());
            response.setRecords(page.getRecords());
            response.setPages((int) Math.ceil((double) page.getTotal() / size));
            response.setHasNext(current < response.getPages());
            response.setHasPrevious(current > 1);
            log.info("获取所有车辆列表成功，共{}条记录", page.getTotal());
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("获取所有车辆列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取所有车辆列表失败");
        }
    }

    /**
     * 获取车辆详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取车辆详情", description = "根据ID获取车辆详情")
    public ApiResponse<Vehicle> getVehicleDetail(
            @Parameter(description = "车辆ID") @PathVariable Long id) {
        
        log.info("获取车辆详情: id={}", id);
        
        try {
            Vehicle vehicle = vehicleService.getById(id);
            if (vehicle == null) {
                log.warn("车辆不存在: id={}", id);
                return ApiResponse.error("车辆不存在");
            }
            log.info("获取车辆详情成功: id={}", id);
            return ApiResponse.success(vehicle);
        } catch (Exception e) {
            log.error("获取车辆详情失败: id={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("获取车辆详情失败");
        }
    }

    /**
     * 审核车辆
     */
    @PutMapping("/{id}/audit")
    @Operation(summary = "审核车辆", description = "审核车辆，通过或拒绝")
    public ApiResponse<Boolean> auditVehicle(
            @Parameter(description = "车辆ID") @PathVariable Long id,
            @Parameter(description = "审核状态") @RequestParam String status,
            @Parameter(description = "审核意见") @RequestParam(required = false) String reason) {
        
        log.info("审核车辆: id={}, status={}, reason={}", id, status, reason);
        
        try {
            boolean result = vehicleService.auditVehicle(id, status, reason);
            if (result) {
                log.info("审核车辆成功: id={}", id);
                return ApiResponse.success(true);
            } else {
                log.warn("审核车辆失败: id={}", id);
                return ApiResponse.error("审核车辆失败");
            }
        } catch (Exception e) {
            log.error("审核车辆异常: id={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("审核车辆异常: " + e.getMessage());
        }
    }
}
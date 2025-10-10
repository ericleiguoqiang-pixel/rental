package com.rental.api.basedata;

import com.rental.api.basedata.response.CarModelResponse;
import com.rental.api.basedata.response.ServiceAreaResponse;
import com.rental.api.basedata.response.StoreResponse;
import com.rental.api.basedata.response.VehicleResponse;
import java.util.Map;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 基础数据服务Feign客户端
 * 
 * @author Rental SaaS Team
 */
@FeignClient(name = "rental-base-data-service")
public interface BaseDataClient {
    
    /**
     * 根据门店ID和租户ID获取车辆列表
     */
    @GetMapping("/api/feign/vehicles/store/{storeId}")
    ApiResponse<List<VehicleResponse>> getVehiclesByStore(
            @PathVariable("storeId") Long storeId);

    @GetMapping("/api/feign/car-models/batch/{ids}")
    ApiResponse<List<CarModelResponse>> getCarModelsByIds(
            @PathVariable("ids") String ids);
    
    /**
     * 获取附近门店列表
     */
    @GetMapping("/api/feign/stores/nearby")
    ApiResponse<List<StoreResponse>> getNearbyStores(
            @RequestParam("longitude") Double longitude,
            @RequestParam("latitude") Double latitude,
            @RequestParam("distance") Double distance);
    
    /**
     * 根据门店ID获取门店详情
     */
    @GetMapping("/api/feign/stores/{id}")
    ApiResponse<StoreResponse> getStoreById(@PathVariable("id") Long id);
    
    /**
     * 获取门店的服务范围列表
     */
    @GetMapping("/api/feign/service-areas/store/{storeId}")
    ApiResponse<List<ServiceAreaResponse>> getServiceAreasByStore(
            @PathVariable("storeId") Long storeId);
            
    /**
     * 统计各状态车辆数量
     */
    @GetMapping("/api/feign/vehicles/count/status")
    ApiResponse<Map<String, Integer>> countVehiclesByStatus(@RequestHeader("X-Tenant-Id") Long tenantId);
    
    /**
     * 统计待审核车辆数量
     */
    @GetMapping("/api/feign/vehicles/count/pending")
    ApiResponse<Integer> countPendingVehicles();
    
    /**
     * 统计待审核门店数量
     */
    @GetMapping("/api/feign/stores/count/pending")
    ApiResponse<Integer> countPendingStores();
    
    /**
     * 统计各状态车辆数量（运营）
     */
    @GetMapping("/api/feign/operation/vehicles/count/status")
    ApiResponse<Map<String, Integer>> countVehiclesByAuditStatus();
    
    /**
     * 统计各状态门店数量（运营）
     */
    @GetMapping("/api/feign/operation/stores/count/status")
    ApiResponse<Map<String, Integer>> countStoresByAuditStatus();
    
    // 门店管理相关接口
    
    /**
     * 创建门店
     */
    @PostMapping("/api/stores")
    ApiResponse<Long> createStore(@RequestHeader("X-Tenant-Id") Long tenantId,
                                  @RequestBody Map<String, Object> request);
    
    /**
     * 更新门店
     */
    @PutMapping("/api/stores/{id}")
    ApiResponse<Void> updateStore(@PathVariable("id") Long id,
                                  @RequestHeader("X-Tenant-Id") Long tenantId,
                                  @RequestBody Map<String, Object> request);
    
    /**
     * 删除门店
     */
    @DeleteMapping("/api/stores/{id}")
    ApiResponse<Void> deleteStore(@PathVariable("id") Long id,
                                  @RequestHeader("X-Tenant-Id") Long tenantId);
    
    /**
     * 获取门店详情
     */
    @GetMapping("/api/stores/{id}")
    ApiResponse<StoreResponse> getStoreDetail(@PathVariable("id") Long id,
                                              @RequestHeader("X-Tenant-Id") Long tenantId);
    
    /**
     * 获取所有门店
     */
    @GetMapping("/api/stores/all")
    ApiResponse<List<StoreResponse>> getAllStores(@RequestHeader("X-Tenant-Id") Long tenantId);
    
    /**
     * 门店上架
     */
    @PostMapping("/api/stores/{id}/online")
    ApiResponse<Void> onlineStore(@PathVariable("id") Long id,
                                  @RequestHeader("X-Tenant-Id") Long tenantId);
    
    /**
     * 门店下架
     */
    @PostMapping("/api/stores/{id}/offline")
    ApiResponse<Void> offlineStore(@PathVariable("id") Long id,
                                   @RequestHeader("X-Tenant-Id") Long tenantId);
    
    /**
     * 统计租户门店数
     */
    @GetMapping("/api/stores/count")
    ApiResponse<Integer> countTenantStores(@RequestHeader("X-Tenant-Id") Long tenantId);
    
    // 车辆管理相关接口
    
    /**
     * 创建车辆
     */
    @PostMapping("/api/vehicles")
    ApiResponse<Long> createVehicle(@RequestHeader("X-Tenant-Id") Long tenantId,
                                    @RequestBody Map<String, Object> request);
    
    /**
     * 更新车辆
     */
    @PutMapping("/api/vehicles/{id}")
    ApiResponse<Void> updateVehicle(@PathVariable("id") Long id,
                                    @RequestHeader("X-Tenant-Id") Long tenantId,
                                    @RequestBody Map<String, Object> request);
    
    /**
     * 删除车辆
     */
    @DeleteMapping("/api/vehicles/{id}")
    ApiResponse<Void> deleteVehicle(@PathVariable("id") Long id,
                                    @RequestHeader("X-Tenant-Id") Long tenantId);
    
    /**
     * 获取车辆详情
     */
    @GetMapping("/api/vehicles/{id}")
    ApiResponse<VehicleResponse> getVehicleDetail(@PathVariable("id") Long id,
                                                  @RequestHeader("X-Tenant-Id") Long tenantId);
    
    /**
     * 分页查询车辆列表
     */
    @GetMapping("/api/vehicles")
    ApiResponse<PageResponse<VehicleResponse>> getVehicleList(
            @RequestParam("current") Integer current,
            @RequestParam("size") Integer size,
            @RequestHeader("X-Tenant-Id") Long tenantId);
    
    /**
     * 根据车型查询车辆列表
     */
    @GetMapping("/api/vehicles/car-model/{carModelId}")
    ApiResponse<List<VehicleResponse>> getVehiclesByCarModel(@PathVariable("carModelId") Long carModelId);
    
    /**
     * 根据车牌号查询车辆
     */
    @GetMapping("/api/vehicles/license-plate/{licensePlate}")
    ApiResponse<VehicleResponse> getVehicleByLicensePlate(@PathVariable("licensePlate") String licensePlate,
                                                          @RequestHeader("X-Tenant-Id") Long tenantId);
    
    /**
     * 车辆上架
     */
    @PostMapping("/api/vehicles/{id}/online")
    ApiResponse<Void> onlineVehicle(@PathVariable("id") Long id,
                                    @RequestHeader("X-Tenant-Id") Long tenantId);
    
    /**
     * 车辆下架
     */
    @PostMapping("/api/vehicles/{id}/offline")
    ApiResponse<Void> offlineVehicle(@PathVariable("id") Long id,
                                     @RequestHeader("X-Tenant-Id") Long tenantId);
    
    /**
     * 更新车辆里程
     */
    @PutMapping("/api/vehicles/{id}/mileage")
    ApiResponse<Void> updateVehicleMileage(@PathVariable("id") Long id,
                                           @RequestParam("mileage") Integer mileage,
                                           @RequestHeader("X-Tenant-Id") Long tenantId);
    
    /**
     * 统计租户车辆总数
     */
    @GetMapping("/api/vehicles/count")
    ApiResponse<Integer> countTenantVehicles(@RequestHeader("X-Tenant-Id") Long tenantId);
    
    /**
     * 统计指定门店的车辆数量
     */
    @GetMapping("/api/vehicles/count/store/{storeId}")
    ApiResponse<Integer> countVehiclesByStore(@PathVariable("storeId") Long storeId);
    
    /**
     * 统计租户各状态车辆数量
     */
    @GetMapping("/api/vehicles/count/status")
    ApiResponse<Map<String, Integer>> countVehiclesByStatusWithTenant(@RequestHeader("X-Tenant-Id") Long tenantId);
}
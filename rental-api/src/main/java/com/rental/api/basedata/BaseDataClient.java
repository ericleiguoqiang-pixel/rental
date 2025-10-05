package com.rental.api.basedata;

import com.rental.api.basedata.response.CarModelResponse;
import com.rental.api.basedata.response.ServiceAreaResponse;
import com.rental.api.basedata.response.StoreResponse;
import com.rental.api.basedata.response.VehicleResponse;
import com.rental.saas.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
}
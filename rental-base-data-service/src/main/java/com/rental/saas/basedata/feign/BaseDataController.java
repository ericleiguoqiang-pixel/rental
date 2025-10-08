package com.rental.saas.basedata.feign;

import com.rental.api.basedata.BaseDataClient;
import com.rental.api.basedata.response.CarModelResponse;
import com.rental.api.basedata.response.ServiceAreaResponse;
import com.rental.api.basedata.response.StoreResponse;
import com.rental.api.basedata.response.VehicleResponse;
import com.rental.saas.basedata.entity.ServiceArea;
import com.rental.saas.basedata.entity.Store;
import com.rental.saas.basedata.service.CarModelService;
import com.rental.saas.basedata.service.ServiceAreaService;
import com.rental.saas.basedata.service.StoreService;
import com.rental.saas.basedata.service.VehicleService;
import com.rental.saas.common.response.ApiResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class BaseDataController implements BaseDataClient {

    @Autowired
    private VehicleService vehicleService;
    
    @Autowired
    private CarModelService carModelService;
    
    @Autowired
    private StoreService storeService;
    
    @Autowired
    private ServiceAreaService serviceAreaService;

    @Override
    @GetMapping("/api/feign/vehicles/store/{storeId}")
    public ApiResponse<List<VehicleResponse>> getVehiclesByStore(@PathVariable("storeId") Long storeId) {
        List<VehicleResponse> vehicles = vehicleService.getVehiclesByStore(storeId, null);
        return ApiResponse.success("查询成功", vehicles);
    }

    @Override
    @GetMapping("/api/feign/car-models/batch/{ids}")
    public ApiResponse<List<CarModelResponse>> getCarModelsByIds(@PathVariable("ids") String ids) {
        List<CarModelResponse> carModels = carModelService.getCarModelsByIds(ids);
        return ApiResponse.success("查询成功", carModels);
    }
    
    @Override
    @GetMapping("/api/feign/stores/nearby")
    public ApiResponse<List<StoreResponse>> getNearbyStores(
            @RequestParam("longitude") Double longitude,
            @RequestParam("latitude") Double latitude,
            @RequestParam("distance") Double distance) {
        // 获取指定范围内的门店
        List<StoreResponse> stores = storeService.getStoresInRange(longitude, latitude, distance);
        
        return ApiResponse.success("查询成功", stores);
    }
    
    @Override
    @GetMapping("/api/feign/stores/{id}")
    public ApiResponse<StoreResponse> getStoreById(@PathVariable("id") Long id) {
        Store store = storeService.getById(id);
        if (store == null) {
            return ApiResponse.error("门店不存在");
        }
        
        StoreResponse response = new StoreResponse();
        // 手动复制属性
        response.setId(store.getId());
        response.setStoreName(store.getStoreName());
        response.setCity(store.getCity());
        response.setAddress(store.getAddress());
        response.setLongitude(store.getLongitude());
        response.setLatitude(store.getLatitude());
        response.setBusinessStartTime(store.getBusinessStartTime());
        response.setBusinessEndTime(store.getBusinessEndTime());
        response.setMinAdvanceHours(store.getMinAdvanceHours());
        response.setMaxAdvanceDays(store.getMaxAdvanceDays());
        response.setServiceFee(store.getServiceFee());
        
        return ApiResponse.success("查询成功", response);
    }
    
    @Override
    @GetMapping("/api/feign/service-areas/store/{storeId}")
    public ApiResponse<List<ServiceAreaResponse>> getServiceAreasByStore(@PathVariable("storeId") Long storeId) {
        List<ServiceArea> serviceAreas = serviceAreaService.listByStoreId(storeId, null);
        List<ServiceAreaResponse> areaResponses = serviceAreas.stream().map(area -> {
            ServiceAreaResponse response = new ServiceAreaResponse();
            // 手动复制属性
            response.setId(area.getId());
            response.setStoreId(area.getStoreId());
            response.setAreaName(area.getAreaName());
            response.setAreaType(area.getAreaType());
            response.setFenceCoordinates(area.getFenceCoordinates());
            response.setAdvanceHours(area.getAdvanceHours());
            response.setServiceStartTime(area.getServiceStartTime());
            response.setServiceEndTime(area.getServiceEndTime());
            response.setDoorToDoorDelivery(area.getDoorToDoorDelivery());
            response.setDeliveryFee(area.getDeliveryFee());
            response.setFreePickupToStore(area.getFreePickupToStore());
            return response;
        }).collect(Collectors.toList());
        
        return ApiResponse.success("查询成功", areaResponses);
    }
    
    @Override
    @GetMapping("/api/feign/vehicles/count/status")
    public ApiResponse<Map<String, Integer>> countVehiclesByStatus(@RequestHeader("X-Tenant-Id") Long tenantId) {
        Map<String, Integer> vehicleCount = vehicleService.countVehiclesByStatus(tenantId);
        return ApiResponse.success(vehicleCount);
    }
    
    @Override
    @GetMapping("/api/feign/vehicles/count/pending")
    public ApiResponse<Integer> countPendingVehicles() {
        int count = vehicleService.countPendingVehicles();
        return ApiResponse.success(count);
    }
    
    @Override
    @GetMapping("/api/feign/stores/count/pending")
    public ApiResponse<Integer> countPendingStores() {
        int count = storeService.countPendingStores();
        return ApiResponse.success(count);
    }
    
    @Override
    @GetMapping("/api/feign/operation/vehicles/count/status")
    public ApiResponse<Map<String, Integer>> countVehiclesByAuditStatus() {
        Map<String, Integer> vehicleCount = vehicleService.countVehiclesByAuditStatus();
        return ApiResponse.success(vehicleCount);
    }
    
    @Override
    @GetMapping("/api/feign/operation/stores/count/status")
    public ApiResponse<Map<String, Integer>> countStoresByAuditStatus() {
        Map<String, Integer> storeCount = storeService.countStoresByAuditStatus();
        return ApiResponse.success(storeCount);
    }
}
package com.rental.saas.basedata.feign;

import com.rental.api.basedata.BaseDataClient;
import com.rental.api.basedata.response.CarModelResponse;
import com.rental.api.basedata.response.VehicleResponse;
import com.rental.saas.basedata.service.CarModelService;
import com.rental.saas.basedata.service.VehicleService;
import com.rental.saas.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BaseDataController implements BaseDataClient {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private CarModelService carModelService;

    @Override
    @GetMapping("/api/vehicles/store/{storeId}")
    public ApiResponse<List<VehicleResponse>> getVehiclesByStore(Long storeId) {
        List<VehicleResponse> vehicles = vehicleService.getVehiclesByStore(storeId, null);
        return ApiResponse.success("查询成功", vehicles);
    }

    @Override
    @GetMapping("/api/car-models/batch/{ids}")
    public ApiResponse<List<CarModelResponse>> getCarModelsByIds(String ids) {
        List<CarModelResponse> carModels = carModelService.getCarModelsByIds(ids);
        return ApiResponse.success("查询成功", carModels);
    }
}
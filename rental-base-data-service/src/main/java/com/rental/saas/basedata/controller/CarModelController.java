package com.rental.saas.basedata.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.saas.basedata.dto.request.CarModelCreateRequest;
import com.rental.api.basedata.response.CarModelResponse;
import com.rental.saas.basedata.service.CarModelService;
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
 * 车型管理控制器
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/car-models")
@RequiredArgsConstructor
@Validated
@Tag(name = "车型管理", description = "车型管理相关接口")
public class CarModelController {

    private final CarModelService carModelService;

    @PostMapping
    @Operation(summary = "创建车型", description = "创建新的车型")
    @OperationLog(type = "车型管理", description = "创建车型")
    public ApiResponse<Long> createCarModel(@Valid @RequestBody CarModelCreateRequest request) {
        Long carModelId = carModelService.createCarModel(request);
        return ApiResponse.success("车型创建成功", carModelId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新车型", description = "更新车型信息")
    @OperationLog(type = "车型管理", description = "更新车型")
    public ApiResponse<Void> updateCarModel(@PathVariable @NotNull @Min(1) Long id,
                                            @Valid @RequestBody CarModelCreateRequest request) {
        carModelService.updateCarModel(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除车型", description = "删除指定车型")
    @OperationLog(type = "车型管理", description = "删除车型")
    public ApiResponse<Void> deleteCarModel(@PathVariable @NotNull @Min(1) Long id) {
        carModelService.deleteCarModel(id);
        return ApiResponse.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询车型详情", description = "根据ID查询车型详细信息")
    public ApiResponse<CarModelResponse> getCarModel(@PathVariable @NotNull @Min(1) Long id) {
        CarModelResponse carModel = carModelService.getCarModelById(id);
        return ApiResponse.success("查询成功", carModel);
    }

    @GetMapping
    @Operation(summary = "分页查询车型列表", description = "分页查询车型列表")
    public ApiResponse<PageResponse<CarModelResponse>> getCarModelList(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") @Min(1) Integer current,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "品牌") @RequestParam(required = false) String brand,
            @Parameter(description = "车系") @RequestParam(required = false) String series,
            @Parameter(description = "座位数") @RequestParam(required = false) Integer seatCount,
            @Parameter(description = "档位类型") @RequestParam(required = false) Integer transmission,
            @Parameter(description = "驱动类型") @RequestParam(required = false) Integer driveType,
            @Parameter(description = "起始年款") @RequestParam(required = false) Integer startYear,
            @Parameter(description = "结束年款") @RequestParam(required = false) Integer endYear) {
        
        Page<CarModelResponse> page = carModelService.getCarModelList(
                current, size, brand, series, seatCount, transmission, driveType, startYear, endYear);
        PageResponse<CarModelResponse> pageResponse = PageResponse.of(
                current, size, page.getTotal(), page.getRecords());
        return ApiResponse.success("查询成功", pageResponse);
    }

    @GetMapping("/all")
    @Operation(summary = "查询全部车型", description = "查询所有车型")
    public ApiResponse<List<CarModelResponse>> getAllCarModels() {
        List<CarModelResponse> carModels = carModelService.getAllCarModels();
        return ApiResponse.success("查询成功", carModels);
    }

    @GetMapping("/brand/{brand}")
    @Operation(summary = "按品牌查询车型", description = "根据品牌查询车型列表")
    public ApiResponse<List<CarModelResponse>> getCarModelsByBrand(@PathVariable String brand) {
        List<CarModelResponse> carModels = carModelService.getCarModelsByBrand(brand);
        return ApiResponse.success("查询成功", carModels);
    }

    @GetMapping("/series/{series}")
    @Operation(summary = "按车系查询车型", description = "根据车系查询车型列表")
    public ApiResponse<List<CarModelResponse>> getCarModelsBySeries(@PathVariable String series) {
        List<CarModelResponse> carModels = carModelService.getCarModelsBySeries(series);
        return ApiResponse.success("查询成功", carModels);
    }

    @GetMapping("/brand-series")
    @Operation(summary = "按品牌车系查询车型", description = "根据品牌和车系查询车型列表")
    public ApiResponse<List<CarModelResponse>> getCarModelsByBrandAndSeries(
            @Parameter(description = "品牌") @RequestParam String brand,
            @Parameter(description = "车系") @RequestParam String series) {
        List<CarModelResponse> carModels = carModelService.getCarModelsByBrandAndSeries(brand, series);
        return ApiResponse.success("查询成功", carModels);
    }

    @GetMapping("/seat-count/{seatCount}")
    @Operation(summary = "按座位数查询车型", description = "根据座位数查询车型列表")
    public ApiResponse<List<CarModelResponse>> getCarModelsBySeatCount(@PathVariable @Min(2) Integer seatCount) {
        List<CarModelResponse> carModels = carModelService.getCarModelsBySeatCount(seatCount);
        return ApiResponse.success("查询成功", carModels);
    }

    @GetMapping("/transmission/{transmission}")
    @Operation(summary = "按档位查询车型", description = "根据档位类型查询车型列表")
    public ApiResponse<List<CarModelResponse>> getCarModelsByTransmission(@PathVariable @Min(1) Integer transmission) {
        List<CarModelResponse> carModels = carModelService.getCarModelsByTransmission(transmission);
        return ApiResponse.success("查询成功", carModels);
    }

    @GetMapping("/drive-type/{driveType}")
    @Operation(summary = "按驱动类型查询车型", description = "根据驱动类型查询车型列表")
    public ApiResponse<List<CarModelResponse>> getCarModelsByDriveType(@PathVariable @Min(1) Integer driveType) {
        List<CarModelResponse> carModels = carModelService.getCarModelsByDriveType(driveType);
        return ApiResponse.success("查询成功", carModels);
    }

    @GetMapping("/brands")
    @Operation(summary = "查询所有品牌", description = "查询系统中所有车辆品牌")
    public ApiResponse<List<String>> getAllBrands() {
        List<String> brands = carModelService.getAllBrands();
        return ApiResponse.success("查询成功", brands);
    }

    @GetMapping("/series")
    @Operation(summary = "查询品牌车系", description = "根据品牌查询车系列表")
    public ApiResponse<List<String>> getSeriesByBrand(@Parameter(description = "品牌") @RequestParam String brand) {
        List<String> series = carModelService.getSeriesByBrand(brand);
        return ApiResponse.success("查询成功", series);
    }

    @GetMapping("/search")
    @Operation(summary = "多条件查询车型", description = "根据多个条件查询车型")
    public ApiResponse<List<CarModelResponse>> searchCarModels(
            @Parameter(description = "品牌") @RequestParam(required = false) String brand,
            @Parameter(description = "车系") @RequestParam(required = false) String series,
            @Parameter(description = "座位数") @RequestParam(required = false) Integer seatCount,
            @Parameter(description = "档位类型") @RequestParam(required = false) Integer transmission,
            @Parameter(description = "驱动类型") @RequestParam(required = false) Integer driveType,
            @Parameter(description = "起始年款") @RequestParam(required = false) Integer startYear,
            @Parameter(description = "结束年款") @RequestParam(required = false) Integer endYear) {
        
        List<CarModelResponse> carModels = carModelService.getCarModelsByConditions(
                brand, series, seatCount, transmission, driveType, startYear, endYear);
        return ApiResponse.success("查询成功", carModels);
    }

    @GetMapping("/check")
    @Operation(summary = "检查车型是否存在", description = "检查指定车型是否已存在")
    public ApiResponse<Boolean> checkCarModelExists(
            @Parameter(description = "品牌") @RequestParam String brand,
            @Parameter(description = "车系") @RequestParam String series,
            @Parameter(description = "车型") @RequestParam String model,
            @Parameter(description = "年款") @RequestParam Integer year,
            @Parameter(description = "排除的车型ID") @RequestParam(required = false) Long excludeId) {
        
        boolean exists = carModelService.checkCarModelExists(brand, series, model, year, excludeId);
        return ApiResponse.success("查询成功", exists);
    }

    @PostMapping("/batch-import")
    @Operation(summary = "批量导入车型", description = "批量导入车型数据")
    @OperationLog(type = "车型管理", description = "批量导入车型")
    public ApiResponse<Void> batchImportCarModels(@Valid @RequestBody List<CarModelCreateRequest> requests) {
        carModelService.batchImportCarModels(requests);
        return ApiResponse.success();
    }
}
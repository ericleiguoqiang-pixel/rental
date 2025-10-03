package com.rental.saas.basedata.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.saas.basedata.dto.request.CarModelCreateRequest;
import com.rental.api.basedata.response.CarModelResponse;
import com.rental.saas.basedata.service.CarModelService;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 运营车型管理控制器
 * 提供运营人员对车型信息的管理功能
 *
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/operation/car-models")
@RequiredArgsConstructor
@Tag(name = "运营车型管理接口", description = "运营MIS系统车型管理相关接口")
public class OperationCarModelController {

    private final CarModelService carModelService;

    /**
     * 创建车型
     */
    @PostMapping
    @Operation(summary = "创建车型", description = "创建新的车型")
    public ApiResponse<Long> createCarModel(@Valid @RequestBody CarModelCreateRequest request) {
        Long carModelId = carModelService.createCarModel(request);
        return ApiResponse.success("车型创建成功", carModelId);
    }

    /**
     * 更新车型信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新车型", description = "更新车型信息")
    public ApiResponse<Void> updateCarModel(@PathVariable @NotNull @Min(1) Long id,
                                            @Valid @RequestBody CarModelCreateRequest request) {
        carModelService.updateCarModel(id, request);
        return ApiResponse.success();
    }

    /**
     * 删除车型
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除车型", description = "删除指定车型")
    public ApiResponse<Void> deleteCarModel(@PathVariable @NotNull @Min(1) Long id) {
        carModelService.deleteCarModel(id);
        return ApiResponse.success();
    }

    /**
     * 查询车型详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询车型详情", description = "根据ID查询车型详细信息")
    public ApiResponse<CarModelResponse> getCarModel(@PathVariable @NotNull @Min(1) Long id) {
        CarModelResponse carModel = carModelService.getCarModelById(id);
        return ApiResponse.success("查询成功", carModel);
    }

    /**
     * 分页查询车型列表
     */
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

    /**
     * 查询全部车型
     */
    @GetMapping("/all")
    @Operation(summary = "查询全部车型", description = "查询所有车型")
    public ApiResponse<List<CarModelResponse>> getAllCarModels() {
        List<CarModelResponse> carModels = carModelService.getAllCarModels();
        return ApiResponse.success("查询成功", carModels);
    }
}
package com.rental.saas.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.api.basedata.BaseDataClient;
import com.rental.api.basedata.response.CarModelResponse;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import com.rental.saas.product.dto.request.CarModelProductRequest;
import com.rental.saas.product.dto.response.CarModelProductResponse;
import com.rental.saas.product.entity.CarModelProduct;
import com.rental.saas.product.service.CarModelProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 车型商品控制器
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/car-model-products")
@RequiredArgsConstructor
@Validated
@Tag(name = "车型商品", description = "车型商品管理相关接口")
public class CarModelProductController {

    private final CarModelProductService carModelProductService;

    @Autowired
    private BaseDataClient baseDataClient;

    @PostMapping
    @Operation(summary = "创建车型商品", description = "创建新的车型商品")
    public ApiResponse<Long> createProduct(@RequestHeader("X-Tenant-Id") Long tenantId,
                                         @Valid @RequestBody CarModelProductRequest request) {
        log.info("创建车型商品: 租户ID={}, 门店ID={}, 车型ID={}", 
                tenantId, request.getStoreId(), request.getCarModelId());
        
        CarModelProduct product = new CarModelProduct();
        BeanUtils.copyProperties(request, product);
        product.setTenantId(tenantId);
        
        boolean result = carModelProductService.createProduct(product);
        if (result) {
            return ApiResponse.success("创建成功", product.getId());
        } else {
            return ApiResponse.error("创建失败");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新车型商品", description = "更新车型商品信息")
    public ApiResponse<Void> updateProduct(@RequestHeader("X-Tenant-Id") Long tenantId,
                                          @PathVariable @NotNull @Min(1) Long id,
                                          @Valid @RequestBody CarModelProductRequest request) {
        log.info("更新车型商品: ID={}", id);
        
        CarModelProduct product = new CarModelProduct();
        BeanUtils.copyProperties(request, product);
        product.setId(id);
        product.setTenantId(tenantId);
        
        boolean result = carModelProductService.updateProduct(product);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除车型商品", description = "删除指定的车型商品")
    public ApiResponse<Void> deleteProduct(@RequestHeader("X-Tenant-Id") Long tenantId,
                                         @PathVariable @NotNull @Min(1) Long id) {
        log.info("删除车型商品: ID={}", id);
        
        // 先检查商品是否存在且属于当前租户
        CarModelProduct product = carModelProductService.getProductById(id);
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        
        if (!tenantId.equals(product.getTenantId())) {
            return ApiResponse.error("无权限操作该商品");
        }
        
        boolean result = carModelProductService.deleteProduct(id);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("删除失败");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取车型商品详情", description = "根据ID获取车型商品详细信息")
    public ApiResponse<CarModelProductResponse> getProduct(@RequestHeader("X-Tenant-Id") Long tenantId,
                                                        @PathVariable @NotNull @Min(1) Long id) {
        log.info("获取车型商品详情: ID={}", id);
        
        CarModelProduct product = carModelProductService.getProductById(id);
        if (product != null) {
            // 验证商品是否属于当前租户
            if (!tenantId.equals(product.getTenantId())) {
                return ApiResponse.error("无权限访问该商品");
            }
            
            CarModelProductResponse response = new CarModelProductResponse();
            BeanUtils.copyProperties(product, response);
            return ApiResponse.success("查询成功", response);
        } else {
            return ApiResponse.error("商品不存在");
        }
    }

    @GetMapping
    @Operation(summary = "分页查询车型商品", description = "分页查询车型商品列表")
    public ApiResponse<PageResponse<CarModelProductResponse>> getProductList(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") @Min(1) Integer current,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "商品名称") @RequestParam(required = false) String productName,
            @Parameter(description = "门店ID") @RequestParam(required = false) Long storeId,
            @Parameter(description = "车型ID") @RequestParam(required = false) Long carModelId,
            @Parameter(description = "上架状态") @RequestParam(required = false) Integer onlineStatus) {
        
        log.info("分页查询车型商品: current={}, size={}, tenantId={}, productName={}, storeId={}, carModelId={}, onlineStatus={}", 
                current, size, tenantId, productName, storeId, carModelId, onlineStatus);
        
        Page<CarModelProduct> page = carModelProductService.getProductList(current, size, tenantId, productName, storeId, carModelId, onlineStatus);
        
        List<CarModelProductResponse> responseList = page.getRecords().stream()
                .map(product -> {
                    CarModelProductResponse response = new CarModelProductResponse();
                    BeanUtils.copyProperties(product, response);
                    return response;
                })
                .collect(Collectors.toList());
        if (responseList.isEmpty()) {
            return ApiResponse.success("查询成功", PageResponse.empty(current, size));
        }

        List<Long> modelIds = responseList.stream()
                .map(CarModelProductResponse::getCarModelId)
                .distinct()
                .toList();
        ApiResponse<List<CarModelResponse>> modelResponses = baseDataClient.getCarModelsByIds(String.join(",", modelIds.stream().map(String::valueOf).toList()));
        Map<Long, CarModelResponse> modelMap = modelResponses.getData().stream()
                .collect(Collectors.toMap(CarModelResponse::getId, model -> model));

        responseList.forEach(response -> {
            CarModelResponse model = modelMap.get(response.getCarModelId());
            if (model != null) {
                response.setCarModelName(String.format("%s %s %s %s", model.getBrand(), model.getSeries(), model.getYear(), model.getModel()));
            }
        });
        
        PageResponse<CarModelProductResponse> pageResponse = PageResponse.of(
                current, size, page.getTotal(), responseList);
        
        return ApiResponse.success("查询成功", pageResponse);
    }

    @PutMapping("/{id}/online")
    @Operation(summary = "上架车型商品", description = "上架指定的车型商品")
    public ApiResponse<Void> onlineProduct(@RequestHeader("X-Tenant-Id") Long tenantId,
                                        @PathVariable @NotNull @Min(1) Long id) {
        log.info("上架车型商品: ID={}", id);
        
        // 先检查商品是否存在且属于当前租户
        CarModelProduct product = carModelProductService.getProductById(id);
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        
        if (!tenantId.equals(product.getTenantId())) {
            return ApiResponse.error("无权限操作该商品");
        }
        
        boolean result = carModelProductService.onlineProduct(id);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("上架失败");
        }
    }

    @PutMapping("/{id}/offline")
    @Operation(summary = "下架车型商品", description = "下架指定的车型商品")
    public ApiResponse<Void> offlineProduct(@RequestHeader("X-Tenant-Id") Long tenantId,
                                         @PathVariable @NotNull @Min(1) Long id) {
        log.info("下架车型商品: ID={}", id);
        
        // 先检查商品是否存在且属于当前租户
        CarModelProduct product = carModelProductService.getProductById(id);
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        
        if (!tenantId.equals(product.getTenantId())) {
            return ApiResponse.error("无权限操作该商品");
        }
        
        boolean result = carModelProductService.offlineProduct(id);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("下架失败");
        }
    }
}
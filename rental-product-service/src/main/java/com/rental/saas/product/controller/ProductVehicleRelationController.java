package com.rental.saas.product.controller;

import com.rental.api.basedata.response.VehicleResponse;
import com.rental.saas.common.response.ApiResponse;
import com.rental.api.basedata.BaseDataClient;
import com.rental.saas.product.dto.request.ProductVehicleRelationRequest;
import com.rental.saas.product.dto.response.VehicleInfoResponse;
import com.rental.saas.product.entity.CarModelProduct;
import com.rental.saas.product.entity.ProductVehicleRelation;
import com.rental.saas.product.service.CarModelProductService;
import com.rental.saas.product.service.ProductVehicleRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 车型商品关联车辆控制器
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/product-vehicle-relations")
@RequiredArgsConstructor
@Validated
@Tag(name = "商品车辆关联", description = "车型商品关联车辆管理相关接口")
public class ProductVehicleRelationController {

    private final ProductVehicleRelationService productVehicleRelationService;
    private final CarModelProductService carModelProductService;
    
    @Autowired
    private BaseDataClient baseDataClient;

    @PostMapping
    @Operation(summary = "创建商品车辆关联", description = "创建商品与车辆的关联关系")
    public ApiResponse<Long> createRelation(@RequestHeader("X-Tenant-Id") Long tenantId,
                                       @RequestBody ProductVehicleRelation relation) {
        log.info("创建商品车辆关联: 商品ID={}, 车辆ID={}", relation.getProductId(), relation.getVehicleId());
        
        // 设置租户ID
        relation.setTenantId(tenantId);
        
        boolean result = productVehicleRelationService.createRelation(relation);
        if (result) {
            return ApiResponse.success("创建成功", relation.getId());
        } else {
            return ApiResponse.error("创建失败，关联可能已存在");
        }
    }

    @PostMapping("/batch")
    @Operation(summary = "批量创建商品车辆关联", description = "为指定商品批量关联车辆")
    public ApiResponse<Void> batchCreateRelations(@RequestHeader("X-Tenant-Id") Long tenantId,
                                            @Validated @RequestBody ProductVehicleRelationRequest request) {
        log.info("批量创建商品车辆关联: 商品ID={}, 车辆数量={}", request.getProductId(), request.getVehicleIds().size());
        
        // 先检查商品是否属于当前租户
        CarModelProduct product = carModelProductService.getProductById(request.getProductId());
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        
        if (!tenantId.equals(product.getTenantId())) {
            return ApiResponse.error("无权限操作该商品");
        }
        
        // 先删除该商品的所有现有关联
        productVehicleRelationService.deleteRelationsByProduct(request.getProductId());
        
        // 创建新的关联
        List<ProductVehicleRelation> relations = request.getVehicleIds().stream()
                .map(vehicleId -> {
                    ProductVehicleRelation relation = new ProductVehicleRelation();
                    relation.setProductId(request.getProductId());
                    relation.setVehicleId(vehicleId);
                    // 设置租户ID
                    relation.setTenantId(tenantId);
                    return relation;
                })
                .toList();
        
        boolean result = productVehicleRelationService.batchCreateRelations(relations);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("关联失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品车辆关联", description = "删除指定的商品车辆关联")
    public ApiResponse<Void> deleteRelation(@RequestHeader("X-Tenant-Id") Long tenantId,
                                      @PathVariable @NotNull @Min(1) Long id) {
        log.info("删除商品车辆关联: ID={}", id);
        
        // 先检查关联是否存在且属于当前租户
        ProductVehicleRelation relation = productVehicleRelationService.getRelationById(id);
        if (relation == null) {
            return ApiResponse.error("关联不存在");
        }
        
        if (!tenantId.equals(relation.getTenantId())) {
            return ApiResponse.error("无权限操作该关联");
        }
        
        boolean result = productVehicleRelationService.deleteRelation(id);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("删除失败");
        }
    }

    @DeleteMapping("/product/{productId}")
    @Operation(summary = "删除商品的所有关联", description = "删除指定商品的所有车辆关联")
    public ApiResponse<Void> deleteRelationsByProduct(@RequestHeader("X-Tenant-Id") Long tenantId,
                                           @PathVariable @NotNull @Min(1) Long productId) {
        log.info("删除商品的所有关联: 商品ID={}", productId);
        
        // 先检查商品是否属于当前租户
        CarModelProduct product = carModelProductService.getProductById(productId);
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        
        if (!tenantId.equals(product.getTenantId())) {
            return ApiResponse.error("无权限操作该商品");
        }
        
        boolean result = productVehicleRelationService.deleteRelationsByProduct(productId);
        if (result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("删除失败");
        }
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "获取商品关联的车辆", description = "获取指定商品关联的所有车辆")
    public ApiResponse<List<ProductVehicleRelation>> getRelationsByProduct(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @PathVariable @NotNull @Min(1) Long productId) {
        log.info("获取商品关联的车辆: 商品ID={}", productId);
        
        // 先检查商品是否属于当前租户
        CarModelProduct product = carModelProductService.getProductById(productId);
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        
        if (!tenantId.equals(product.getTenantId())) {
            return ApiResponse.error("无权限访问该商品");
        }
        
        List<ProductVehicleRelation> relations = productVehicleRelationService.getRelationsByProduct(productId);
        return ApiResponse.success("查询成功", relations);
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "获取车辆关联的商品", description = "获取指定车辆关联的所有商品")
    public ApiResponse<List<ProductVehicleRelation>> getRelationsByVehicle(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @PathVariable @NotNull @Min(1) Long vehicleId) {
        log.info("获取车辆关联的商品: 车辆ID={}", vehicleId);
        
        List<ProductVehicleRelation> relations = productVehicleRelationService.getRelationsByVehicle(vehicleId);
        
        // 过滤掉不属于当前租户的关联
        List<ProductVehicleRelation> filteredRelations = relations.stream()
                .filter(relation -> tenantId.equals(relation.getTenantId()))
                .collect(Collectors.toList());
        
        return ApiResponse.success("查询成功", filteredRelations);
    }

    @GetMapping("/check")
    @Operation(summary = "检查商品车辆关联是否存在", description = "检查指定商品与车辆的关联是否存在")
    public ApiResponse<Boolean> checkRelationExists(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @Parameter(description = "商品ID") @RequestParam @Min(1) Long productId,
            @Parameter(description = "车辆ID") @RequestParam @Min(1) Long vehicleId) {
        log.info("检查商品车辆关联是否存在: 商品ID={}, 车辆ID={}", productId, vehicleId);
        
        // 先检查商品是否属于当前租户
        CarModelProduct product = carModelProductService.getProductById(productId);
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        
        if (!tenantId.equals(product.getTenantId())) {
            return ApiResponse.error("无权限访问该商品");
        }
        
        boolean exists = productVehicleRelationService.checkRelationExists(productId, vehicleId);
        return ApiResponse.success("查询成功", exists);
    }
    
    @GetMapping("/product/{productId}/vehicles")
    @Operation(summary = "获取门店下指定车型的可关联车辆", description = "获取指定门店下指定车型的所有车辆，标记已关联的车辆")
    public ApiResponse<List<VehicleInfoResponse>> getAvailableVehiclesForProduct(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @PathVariable @NotNull @Min(1) Long productId) {
        log.info("获取门店下指定车型的可关联车辆: 商品ID={}", productId);
        
        // 获取商品信息
        CarModelProduct product = carModelProductService.getProductById(productId);
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        
        // 验证商品是否属于当前租户
        if (!tenantId.equals(product.getTenantId())) {
            return ApiResponse.error("无权限访问该商品");
        }
        
        // 调用基础数据服务获取门店下该车型的所有车辆
        ApiResponse<List<VehicleResponse>> vehicleResponse = baseDataClient.getVehiclesByStore(
                product.getStoreId());
        
        if (vehicleResponse.getCode() != 200) {
            return ApiResponse.error("获取车辆信息失败");
        }
        
        List<VehicleInfoResponse> allVehicles = vehicleResponse
                .getData()
                .stream()
                .filter(vehicle -> vehicle.getCarModelId().equals(product.getCarModelId()))
                .map(vehicle -> {
            VehicleInfoResponse vehicleInfoResponse = new VehicleInfoResponse();
            vehicleInfoResponse.setId(vehicle.getId());
            vehicleInfoResponse.setLicensePlate(vehicle.getLicensePlate());
            vehicleInfoResponse.setVehicleStatus(vehicle.getVehicleStatus());
            vehicleInfoResponse.setRelated(false);
            return vehicleInfoResponse;
        }).toList();
        
        // 获取已关联的车辆ID列表
        List<ProductVehicleRelation> relations = productVehicleRelationService.getRelationsByProduct(productId);
        List<Long> relatedVehicleIds = relations.stream()
                .map(ProductVehicleRelation::getVehicleId)
                .toList();
        
        // 标记已关联的车辆
        List<VehicleInfoResponse> result = allVehicles.stream()
                .map(vehicle -> {
                    vehicle.setRelated(relatedVehicleIds.contains(vehicle.getId()));
                    return vehicle;
                })
                .toList();
        
        return ApiResponse.success("查询成功", result);
    }
}
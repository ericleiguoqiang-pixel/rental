package com.rental.saas.mcp.vehicle;

import com.rental.api.basedata.BaseDataClient;
import com.rental.api.basedata.response.VehicleResponse;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车辆管理MCP工具
 * 提供车辆相关的操作接口供AI Agent调用
 *
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleMcpTool {

    private final BaseDataClient baseDataClient;

    /**
     * 创建车辆
     */
    @Tool(name = "create_vehicle", description = "创建新的车辆")
    public Long createVehicle(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "归属门店ID") Long storeId,
            @ToolParam(description = "车牌号") String licensePlate,
            @ToolParam(description = "车型ID") Long carModelId,
            @ToolParam(description = "牌照类型：1-普通，2-京牌，3-沪牌，4-深牌，5-粤A牌，6-杭州牌") Integer licenseType,
            @ToolParam(description = "注册日期") String registerDate,
            @ToolParam(description = "车架号") String vin,
            @ToolParam(description = "发动机号") String engineNo,
            @ToolParam(description = "使用性质：1-营运，2-非营运") Integer usageNature,
            @ToolParam(description = "总里程(公里)") Integer mileage) {
        
        log.info("MCP调用创建车辆: 租户ID={}, 门店ID={}", tenantId, storeId);
        
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("storeId", storeId);
            request.put("licensePlate", licensePlate);
            request.put("carModelId", carModelId);
            request.put("licenseType", licenseType);
            request.put("registerDate", LocalDate.parse(registerDate));
            request.put("vin", vin);
            request.put("engineNo", engineNo);
            request.put("usageNature", usageNature);
            request.put("mileage", mileage);
            
            ApiResponse<Long> response = baseDataClient.createVehicle(tenantId, request);
            if (response.isSuccess()) {
                return response.getData();
            } else {
                throw new RuntimeException("创建车辆失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("创建车辆异常: ", e);
            throw new RuntimeException("创建车辆异常: " + e.getMessage());
        }
    }

    /**
     * 更新车辆
     */
    @Tool(name = "update_vehicle", description = "更新车辆信息")
    public void updateVehicle(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "车辆ID") Long vehicleId,
            @ToolParam(description = "归属门店ID") Long storeId,
            @ToolParam(description = "车牌号") String licensePlate,
            @ToolParam(description = "车型ID") Long carModelId,
            @ToolParam(description = "牌照类型：1-普通，2-京牌，3-沪牌，4-深牌，5-粤A牌，6-杭州牌") Integer licenseType,
            @ToolParam(description = "车架号") String vin,
            @ToolParam(description = "发动机号") String engineNo,
            @ToolParam(description = "使用性质：1-营运，2-非营运") Integer usageNature) {
        
        log.info("MCP调用更新车辆: 租户ID={}, 车辆ID={}", tenantId, vehicleId);
        
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("storeId", storeId);
            request.put("licensePlate", licensePlate);
            request.put("carModelId", carModelId);
            request.put("licenseType", licenseType);
            request.put("vin", vin);
            request.put("engineNo", engineNo);
            request.put("usageNature", usageNature);
            
            ApiResponse<Void> response = baseDataClient.updateVehicle(vehicleId, tenantId, request);
            if (!response.isSuccess()) {
                throw new RuntimeException("更新车辆失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("更新车辆异常: ", e);
            throw new RuntimeException("更新车辆异常: " + e.getMessage());
        }
    }

    /**
     * 删除车辆
     */
    @Tool(name = "delete_vehicle", description = "删除车辆")
    public void deleteVehicle(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "车辆ID") Long vehicleId) {
        
        log.info("MCP调用删除车辆: 租户ID={}, 车辆ID={}", tenantId, vehicleId);
        
        try {
            ApiResponse<Void> response = baseDataClient.deleteVehicle(vehicleId, tenantId);
            if (!response.isSuccess()) {
                throw new RuntimeException("删除车辆失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("删除车辆异常: ", e);
            throw new RuntimeException("删除车辆异常: " + e.getMessage());
        }
    }

    /**
     * 查询车辆详情
     */
    @Tool(name = "get_vehicle_detail", description = "查询车辆详情")
    public VehicleResponse getVehicleDetail(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "车辆ID") Long vehicleId) {
        
        log.info("MCP调用查询车辆详情: 租户ID={}, 车辆ID={}", tenantId, vehicleId);
        
        try {
            ApiResponse<VehicleResponse> response = baseDataClient.getVehicleDetail(vehicleId, tenantId);
            if (response.isSuccess()) {
                return response.getData();
            } else {
                throw new RuntimeException("查询车辆详情失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("查询车辆详情异常: ", e);
            throw new RuntimeException("查询车辆详情异常: " + e.getMessage());
        }
    }

    /**
     * 分页查询车辆列表
     */
    @Tool(name = "get_vehicle_list", description = "分页查询车辆列表")
    public List<VehicleResponse> getVehicleList(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "当前页") Integer current,
            @ToolParam(description = "页大小") Integer size) {
        
        log.info("MCP调用分页查询车辆列表: 租户ID={}, 当前页={}, 页大小={}", tenantId, current, size);
        
        try {
            ApiResponse<PageResponse<VehicleResponse>> response = baseDataClient.getVehicleList(current, size, tenantId);
            if (response.isSuccess()) {
                return response.getData().getRecords();
            } else {
                throw new RuntimeException("分页查询车辆列表失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("分页查询车辆列表异常: ", e);
            throw new RuntimeException("分页查询车辆列表异常: " + e.getMessage());
        }
    }

    /**
     * 根据车型查询车辆列表
     */
    @Tool(name = "get_vehicles_by_model", description = "根据车型查询车辆列表")
    public List<VehicleResponse> getVehiclesByModel(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "车型ID") Long carModelId) {
        
        log.info("MCP调用根据车型查询车辆列表: 租户ID={}, 车型ID={}", tenantId, carModelId);
        
        try {
            ApiResponse<List<VehicleResponse>> response = baseDataClient.getVehiclesByCarModel(carModelId);
            if (response.isSuccess()) {
                return response.getData();
            } else {
                throw new RuntimeException("根据车型查询车辆列表失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("根据车型查询车辆列表异常: ", e);
            throw new RuntimeException("根据车型查询车辆列表异常: " + e.getMessage());
        }
    }

    /**
     * 根据车牌号查询车辆
     */
    @Tool(name = "get_vehicle_by_license_plate", description = "根据车牌号查询车辆")
    public VehicleResponse getVehicleByLicensePlate(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "车牌号") String licensePlate) {
        
        log.info("MCP调用根据车牌号查询车辆: 租户ID={}, 车牌号={}", tenantId, licensePlate);
        
        try {
            ApiResponse<VehicleResponse> response = baseDataClient.getVehicleByLicensePlate(licensePlate, tenantId);
            if (response.isSuccess()) {
                return response.getData();
            } else {
                throw new RuntimeException("根据车牌号查询车辆失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("根据车牌号查询车辆异常: ", e);
            throw new RuntimeException("根据车牌号查询车辆异常: " + e.getMessage());
        }
    }

    /**
     * 设置车辆为上架状态
     */
    @Tool(name = "online_vehicle", description = "设置车辆为上架状态")
    public void onlineVehicle(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "车辆ID") Long vehicleId) {
        
        log.info("MCP调用车辆上架: 租户ID={}, 车辆ID={}", tenantId, vehicleId);
        
        try {
            ApiResponse<Void> response = baseDataClient.onlineVehicle(vehicleId, tenantId);
            if (!response.isSuccess()) {
                throw new RuntimeException("车辆上架失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("车辆上架异常: ", e);
            throw new RuntimeException("车辆上架异常: " + e.getMessage());
        }
    }

    /**
     * 设置车辆为下架状态
     */
    @Tool(name = "offline_vehicle", description = "设置车辆为下架状态")
    public void offlineVehicle(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "车辆ID") Long vehicleId) {
        
        log.info("MCP调用车辆下架: 租户ID={}, 车辆ID={}", tenantId, vehicleId);
        
        try {
            ApiResponse<Void> response = baseDataClient.offlineVehicle(vehicleId, tenantId);
            if (!response.isSuccess()) {
                throw new RuntimeException("车辆下架失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("车辆下架异常: ", e);
            throw new RuntimeException("车辆下架异常: " + e.getMessage());
        }
    }

    /**
     * 更新车辆总里程数
     */
    @Tool(name = "update_vehicle_mileage", description = "更新车辆总里程数")
    public void updateVehicleMileage(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "车辆ID") Long vehicleId,
            @ToolParam(description = "里程数(公里)") Integer mileage) {
        
        log.info("MCP调用更新车辆里程: 租户ID={}, 车辆ID={}, 里程数={}", tenantId, vehicleId, mileage);
        
        try {
            ApiResponse<Void> response = baseDataClient.updateVehicleMileage(vehicleId, mileage, tenantId);
            if (!response.isSuccess()) {
                throw new RuntimeException("更新车辆里程失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("更新车辆里程异常: ", e);
            throw new RuntimeException("更新车辆里程异常: " + e.getMessage());
        }
    }

    /**
     * 统计租户车辆总数
     */
    @Tool(name = "count_tenant_vehicles", description = "统计租户车辆总数")
    public Integer countTenantVehicles(
            @ToolParam(description = "租户ID") Long tenantId) {
        
        log.info("MCP调用统计租户车辆总数: 租户ID={}", tenantId);
        
        try {
            ApiResponse<Integer> response = baseDataClient.countTenantVehicles(tenantId);
            if (response.isSuccess()) {
                return response.getData();
            } else {
                throw new RuntimeException("统计租户车辆总数失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("统计租户车辆总数异常: ", e);
            throw new RuntimeException("统计租户车辆总数异常: " + e.getMessage());
        }
    }

    /**
     * 统计指定门店的车辆数量
     */
    @Tool(name = "count_store_vehicles", description = "统计指定门店的车辆数量")
    public Integer countStoreVehicles(
            @ToolParam(description = "门店ID") Long storeId) {
        
        log.info("MCP调用统计门店车辆数量: 门店ID={}", storeId);
        
        try {
            ApiResponse<Integer> response = baseDataClient.countVehiclesByStore(storeId);
            if (response.isSuccess()) {
                return response.getData();
            } else {
                throw new RuntimeException("统计门店车辆数量失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("统计门店车辆数量异常: ", e);
            throw new RuntimeException("统计门店车辆数量异常: " + e.getMessage());
        }
    }

    /**
     * 统计租户各状态车辆数量
     */
    @Tool(name = "count_vehicles_by_status", description = "统计租户各状态车辆数量")
    public Map<String, Integer> countVehiclesByStatus(
            @ToolParam(description = "租户ID") Long tenantId) {
        
        log.info("MCP调用统计各状态车辆数量: 租户ID={}", tenantId);
        
        try {
            ApiResponse<Map<String, Integer>> response = baseDataClient.countVehiclesByStatusWithTenant(tenantId);
            if (response.isSuccess()) {
                return response.getData();
            } else {
                throw new RuntimeException("统计各状态车辆数量失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("统计各状态车辆数量异常: ", e);
            throw new RuntimeException("统计各状态车辆数量异常: " + e.getMessage());
        }
    }
}
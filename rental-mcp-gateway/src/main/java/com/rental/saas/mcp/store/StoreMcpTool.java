package com.rental.saas.mcp.store;

import com.rental.api.basedata.BaseDataClient;
import com.rental.api.basedata.response.StoreResponse;
import com.rental.saas.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 门店管理MCP工具
 * 提供门店相关的操作接口供AI Agent调用
 *
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StoreMcpTool {

    private final BaseDataClient baseDataClient;

    /**
     * 创建门店
     */
    @Tool(name = "create_store", description = "创建新的门店")
    public Long createStore(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "门店名称") String storeName,
            @ToolParam(description = "所在城市") String city,
            @ToolParam(description = "详细地址") String address,
            @ToolParam(description = "经度") BigDecimal longitude,
            @ToolParam(description = "纬度") BigDecimal latitude,
            @ToolParam(description = "营业开始时间") String businessStartTime,
            @ToolParam(description = "营业结束时间") String businessEndTime,
            @ToolParam(description = "最小提前预定时间(小时)") Integer minAdvanceHours,
            @ToolParam(description = "最大提前预定天数") Integer maxAdvanceDays,
            @ToolParam(description = "车行手续费(分)") Integer serviceFee) {
        
        log.info("MCP调用创建门店: 租户ID={}, 门店名称={}", tenantId, storeName);
        
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("storeName", storeName);
            request.put("city", city);
            request.put("address", address);
            request.put("longitude", longitude);
            request.put("latitude", latitude);
            request.put("businessStartTime", LocalTime.parse(businessStartTime));
            request.put("businessEndTime", LocalTime.parse(businessEndTime));
            request.put("minAdvanceHours", minAdvanceHours);
            request.put("maxAdvanceDays", maxAdvanceDays);
            request.put("serviceFee", serviceFee);
            
            ApiResponse<Long> response = baseDataClient.createStore(tenantId, request);
            if (response.isSuccess()) {
                return response.getData();
            } else {
                throw new RuntimeException("创建门店失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("创建门店异常: ", e);
            throw new RuntimeException("创建门店异常: " + e.getMessage());
        }
    }

    /**
     * 更新门店
     */
    @Tool(name = "update_store", description = "更新门店信息")
    public void updateStore(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "门店ID") Long storeId,
            @ToolParam(description = "门店名称") String storeName,
            @ToolParam(description = "所在城市") String city,
            @ToolParam(description = "详细地址") String address,
            @ToolParam(description = "经度") BigDecimal longitude,
            @ToolParam(description = "纬度") BigDecimal latitude,
            @ToolParam(description = "营业开始时间") String businessStartTime,
            @ToolParam(description = "营业结束时间") String businessEndTime,
            @ToolParam(description = "最小提前预定时间(小时)") Integer minAdvanceHours,
            @ToolParam(description = "最大提前预定天数") Integer maxAdvanceDays,
            @ToolParam(description = "车行手续费(分)") Integer serviceFee) {
        
        log.info("MCP调用更新门店: 租户ID={}, 门店ID={}", tenantId, storeId);
        
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("storeName", storeName);
            request.put("city", city);
            request.put("address", address);
            request.put("longitude", longitude);
            request.put("latitude", latitude);
            request.put("businessStartTime", LocalTime.parse(businessStartTime));
            request.put("businessEndTime", LocalTime.parse(businessEndTime));
            request.put("minAdvanceHours", minAdvanceHours);
            request.put("maxAdvanceDays", maxAdvanceDays);
            request.put("serviceFee", serviceFee);
            
            ApiResponse<Void> response = baseDataClient.updateStore(storeId, tenantId, request);
            if (!response.isSuccess()) {
                throw new RuntimeException("更新门店失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("更新门店异常: ", e);
            throw new RuntimeException("更新门店异常: " + e.getMessage());
        }
    }

    /**
     * 查询门店详情
     */
    @Tool(name = "get_store_detail", description = "查询门店详情")
    public StoreResponse getStoreDetail(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "门店ID") Long storeId) {
        
        log.info("MCP调用查询门店详情: 租户ID={}, 门店ID={}", tenantId, storeId);
        
        try {
            ApiResponse<StoreResponse> response = baseDataClient.getStoreDetail(storeId, tenantId);
            if (response.isSuccess()) {
                return response.getData();
            } else {
                throw new RuntimeException("查询门店详情失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("查询门店详情异常: ", e);
            throw new RuntimeException("查询门店详情异常: " + e.getMessage());
        }
    }

    /**
     * 查询全部门店
     */
    @Tool(name = "get_all_stores", description = "查询租户下所有门店")
    public List<StoreResponse> getAllStores(
            @ToolParam(description = "租户ID") Long tenantId) {
        
        log.info("MCP调用查询全部门店: 租户ID={}", tenantId);
        
        try {
            ApiResponse<List<StoreResponse>> response = baseDataClient.getAllStores(tenantId);
            if (response.isSuccess()) {
                return response.getData();
            } else {
                throw new RuntimeException("查询全部门店失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("查询全部门店异常: ", e);
            throw new RuntimeException("查询全部门店异常: " + e.getMessage());
        }
    }

    /**
     * 设置门店上架状态
     */
    @Tool(name = "online_store", description = "设置门店为上架状态")
    public void onlineStore(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "门店ID") Long storeId) {
        
        log.info("MCP调用门店上架: 租户ID={}, 门店ID={}", tenantId, storeId);
        
        try {
            ApiResponse<Void> response = baseDataClient.onlineStore(storeId, tenantId);
            if (!response.isSuccess()) {
                throw new RuntimeException("门店上架失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("门店上架异常: ", e);
            throw new RuntimeException("门店上架异常: " + e.getMessage());
        }
    }

    /**
     * 设置门店下架状态
     */
    @Tool(name = "offline_store", description = "设置门店为下架状态")
    public void offlineStore(
            @ToolParam(description = "租户ID") Long tenantId,
            @ToolParam(description = "门店ID") Long storeId) {
        
        log.info("MCP调用门店下架: 租户ID={}, 门店ID={}", tenantId, storeId);
        
        try {
            ApiResponse<Void> response = baseDataClient.offlineStore(storeId, tenantId);
            if (!response.isSuccess()) {
                throw new RuntimeException("门店下架失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("门店下架异常: ", e);
            throw new RuntimeException("门店下架异常: " + e.getMessage());
        }
    }

    /**
     * 统计租户门店数
     */
    @Tool(name = "count_tenant_stores", description = "统计租户门店数量")
    public Integer countTenantStores(
            @ToolParam(description = "租户ID") Long tenantId) {
        
        log.info("MCP调用统计租户门店数: 租户ID={}", tenantId);
        
        try {
            ApiResponse<Integer> response = baseDataClient.countTenantStores(tenantId);
            if (response.isSuccess()) {
                return response.getData();
            } else {
                throw new RuntimeException("统计租户门店数失败: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("统计租户门店数异常: ", e);
            throw new RuntimeException("统计租户门店数异常: " + e.getMessage());
        }
    }
}
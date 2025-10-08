package com.rental.saas.user.service.impl;

import com.rental.api.basedata.BaseDataClient;
import com.rental.api.order.OrderClient;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.user.dto.DashboardStats;
import com.rental.saas.user.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 仪表盘服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    
    private final OrderClient orderClient;
    private final BaseDataClient baseDataClient;
    
    /**
     * 获取仪表盘统计数据
     * @param tenantId 租户ID
     * @return 仪表盘统计数据
     */
    @Override
    public DashboardStats getDashboardStats(Long tenantId) {
        DashboardStats stats = new DashboardStats();
        
        try {
            // 从订单服务获取今日订单数
            ApiResponse<Integer> orderCountResponse = orderClient.countTodayOrdersByTenantId(tenantId);
            if (orderCountResponse != null && orderCountResponse.getData() != null) {
                stats.setTodayOrders(orderCountResponse.getData());
            } else {
                stats.setTodayOrders(0);
            }
        } catch (Exception e) {
            log.warn("获取今日订单数失败: {}", e.getMessage());
            stats.setTodayOrders(0);
        }
        
        try {
            // 从订单服务获取今日收入
            ApiResponse<Double> revenueResponse = orderClient.sumTodayRevenueByTenantId(tenantId);
            if (revenueResponse != null && revenueResponse.getData() != null) {
                stats.setTodayRevenue(revenueResponse.getData());
            } else {
                stats.setTodayRevenue(0.0);
            }
        } catch (Exception e) {
            log.warn("获取今日收入失败: {}", e.getMessage());
            stats.setTodayRevenue(0.0);
        }
        
        try {
            // 从车辆服务获取各状态车辆数量
            ApiResponse<Map<String, Integer>> vehicleCountResponse = baseDataClient.countVehiclesByStatus(tenantId);
            if (vehicleCountResponse != null && vehicleCountResponse.getData() != null) {
                Map<String, Integer> statusCount = vehicleCountResponse.getData();
                // 可用车辆数（状态为1的车辆）
                stats.setAvailableVehicles(statusCount.getOrDefault("1", 0));
                // 租出车辆数（状态为2的车辆）
                stats.setRentedVehicles(statusCount.getOrDefault("2", 0));
            } else {
                stats.setAvailableVehicles(0);
                stats.setRentedVehicles(0);
            }
        } catch (Exception e) {
            log.warn("获取车辆统计数据失败: {}", e.getMessage());
            stats.setAvailableVehicles(0);
            stats.setRentedVehicles(0);
        }
        
        return stats;
    }
}
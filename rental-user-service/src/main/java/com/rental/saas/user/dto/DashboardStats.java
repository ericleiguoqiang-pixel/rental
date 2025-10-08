package com.rental.saas.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 仪表盘统计数据DTO
 */
@Data
@Schema(description = "仪表盘统计数据")
public class DashboardStats {
    
    @Schema(description = "今日订单数")
    private int todayOrders;
    
    @Schema(description = "今日收入")
    private double todayRevenue;
    
    @Schema(description = "可用车辆数")
    private int availableVehicles;
    
    @Schema(description = "租出车辆数")
    private int rentedVehicles;
}
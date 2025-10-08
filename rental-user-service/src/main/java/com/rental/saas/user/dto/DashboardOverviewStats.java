package com.rental.saas.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 运营仪表盘概览统计数据DTO
 */
@Data
@Schema(description = "运营仪表盘概览统计数据")
public class DashboardOverviewStats {
    
    @Schema(description = "待审核商户数")
    private int pendingMerchants;
    
    @Schema(description = "待审核车辆数")
    private int pendingVehicles;
    
    @Schema(description = "待审核门店数")
    private int pendingStores;
    
    @Schema(description = "总审核数")
    private int totalAudits;
}
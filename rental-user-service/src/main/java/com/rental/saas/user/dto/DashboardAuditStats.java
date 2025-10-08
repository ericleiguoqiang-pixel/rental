package com.rental.saas.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 运营仪表盘审核统计数据DTO
 */
@Data
@Schema(description = "运营仪表盘审核统计数据")
public class DashboardAuditStats {
    
    @Schema(description = "商户审核统计")
    private Map<String, Integer> merchantStats;
    
    @Schema(description = "车辆审核统计")
    private Map<String, Integer> vehicleStats;
    
    @Schema(description = "门店审核统计")
    private Map<String, Integer> storeStats;
}
package com.rental.api.basedata.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 门店响应DTO
 */
@Data
@Schema(description = "门店响应")
public class StoreResponse {
    
    @Schema(description = "门店ID")
    private Long id;

    @Schema(description = "门店名称")
    private String storeName;

    @Schema(description = "商户ID")
    private Long tenantId;
    
    @Schema(description = "所在城市")
    private String city;
    
    @Schema(description = "详细地址")
    private String address;
    
    @Schema(description = "经度")
    private BigDecimal longitude;
    
    @Schema(description = "纬度")
    private BigDecimal latitude;
    
    @Schema(description = "营业开始时间")
    private LocalTime businessStartTime;
    
    @Schema(description = "营业结束时间")
    private LocalTime businessEndTime;
    
    @Schema(description = "最小提前预定时间(小时)")
    private Integer minAdvanceHours;
    
    @Schema(description = "最大提前预定天数")
    private Integer maxAdvanceDays;
    
    @Schema(description = "车行手续费(分)")
    private Integer serviceFee;

    @Schema(description = "门店审核状态")
    private Integer auditStatus;

    @Schema(description = "门店审核状态描述")
    private String auditStatusDesc;

    @Schema(description = "门店状态")
    private Integer onlineStatus;

    @Schema(description = "门店状态描述")
    private String onlineStatusDesc;

    @Schema(description = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间", example = "2024-01-01 12:00:00")
    private LocalDateTime updatedTime;
}
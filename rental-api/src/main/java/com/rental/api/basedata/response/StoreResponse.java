package com.rental.api.basedata.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
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
}
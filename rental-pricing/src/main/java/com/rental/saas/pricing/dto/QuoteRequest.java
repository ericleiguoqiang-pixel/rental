package com.rental.saas.pricing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 报价请求DTO
 */
@Data
@Schema(description = "报价请求参数")
public class QuoteRequest {
    
    @Schema(description = "用车日期")
    private LocalDate date;
    
    @Schema(description = "用车时间")
    private LocalTime time;
    
    @Schema(description = "经度")
    private Double longitude;
    
    @Schema(description = "纬度")
    private Double latitude;
}
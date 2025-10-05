package com.rental.api.basedata.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 服务范围响应DTO
 */
@Data
@Schema(description = "服务范围响应")
public class ServiceAreaResponse {
    
    @Schema(description = "服务范围ID")
    private Long id;
    
    @Schema(description = "门店ID")
    private Long storeId;
    
    @Schema(description = "区域名称")
    private String areaName;
    
    @Schema(description = "区域类型:1-取车区域,2-还车区域")
    private Integer areaType;
    
    @Schema(description = "电子围栏坐标(JSON格式)")
    private String fenceCoordinates;
    
    @Schema(description = "提前预定时间(小时)")
    private Integer advanceHours;
    
    @Schema(description = "服务开始时间")
    private String serviceStartTime;
    
    @Schema(description = "服务结束时间")
    private String serviceEndTime;
    
    @Schema(description = "是否提供送车上门:0-否,1-是")
    private Integer doorToDoorDelivery;
    
    @Schema(description = "送车上门费用(分)")
    private Integer deliveryFee;
    
    @Schema(description = "是否免费接至门店:0-否,1-是")
    private Integer freePickupToStore;
}
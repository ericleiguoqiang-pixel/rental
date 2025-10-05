package com.rental.saas.pricing.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报价实体类
 */
@Data
@Schema(description = "报价实体")
public class Quote {
    
    @Schema(description = "报价ID")
    private String id;
    
    @Schema(description = "商品ID")
    private Long productId;
    
    @Schema(description = "商品名称")
    private String productName;
    
    @Schema(description = "车型ID")
    private Long modelId;
    
    @Schema(description = "车型名称")
    private String modelName;
    
    @Schema(description = "门店ID")
    private Long storeId;
    
    @Schema(description = "门店名称")
    private String storeName;
    
    @Schema(description = "日租金")
    private BigDecimal dailyRate;
    
    @Schema(description = "取车服务费")
    private BigDecimal pickupFee;
    
    @Schema(description = "还车服务费")
    private BigDecimal returnFee;
    
    @Schema(description = "门店手续费")
    private BigDecimal storeFee;
    
    @Schema(description = "基本保障价格")
    private BigDecimal baseProtectionPrice;
    
    @Schema(description = "总价格")
    private BigDecimal totalPrice;
    
    @Schema(description = "取还方式：上门取送车/用户到店自取")
    private String deliveryType;
    
//    @Schema(description = "创建时间")
//    private LocalDateTime createdAt;
}
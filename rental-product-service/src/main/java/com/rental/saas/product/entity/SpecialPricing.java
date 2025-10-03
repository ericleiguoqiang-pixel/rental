package com.rental.saas.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 特殊定价实体
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("special_pricing")
@Schema(description = "特殊定价")
public class SpecialPricing extends TenantBaseEntity {

    /**
     * 商品ID
     */
    @TableField("product_id")
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    /**
     * 定价日期
     */
    @TableField("price_date")
    @Schema(description = "定价日期", example = "2024-01-01")
    private LocalDate priceDate;

    /**
     * 价格(分)
     */
    @TableField("price")
    @Schema(description = "价格(分)", example = "50000")
    private Integer price;
}
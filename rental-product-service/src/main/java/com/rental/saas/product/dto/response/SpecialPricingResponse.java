package com.rental.saas.product.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 特殊定价响应DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "特殊定价响应DTO")
public class SpecialPricingResponse {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1")
    private Long id;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    /**
     * 定价日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "定价日期", example = "2024-01-01")
    private LocalDate priceDate;

    /**
     * 价格(分)
     */
    @Schema(description = "价格(分)", example = "50000")
    private Integer price;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间", example = "2024-01-01 12:00:00")
    private LocalDateTime updatedTime;
}
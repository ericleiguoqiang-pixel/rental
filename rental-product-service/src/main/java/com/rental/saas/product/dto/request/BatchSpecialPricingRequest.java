package com.rental.saas.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * 批量特殊定价请求DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "批量特殊定价请求")
public class BatchSpecialPricingRequest {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    @Min(value = 1, message = "商品ID必须大于0")
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    /**
     * 特殊定价列表
     */
    @NotNull(message = "特殊定价列表不能为空")
    @Schema(description = "特殊定价列表")
    private List<SpecialPricingItem> pricings;

    /**
     * 特殊定价项
     */
    @Data
    @Schema(description = "特殊定价项")
    public static class SpecialPricingItem {
        
        /**
         * 定价日期
         */
        @NotNull(message = "定价日期不能为空")
        @Schema(description = "定价日期", example = "2024-01-01")
        private LocalDate priceDate;

        /**
         * 价格(分)
         */
        @NotNull(message = "价格不能为空")
        @Min(value = 0, message = "价格不能小于0")
        @Schema(description = "价格(分)", example = "50000")
        private Integer price;
    }
}
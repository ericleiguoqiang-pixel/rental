package com.rental.saas.pricing.dto;

import com.rental.api.pricing.entity.Quote;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 报价响应DTO
 */
@Data
@Schema(description = "报价响应结果")
public class QuoteResponse {
    
    @Schema(description = "报价列表")
    private List<Quote> quotes;
}
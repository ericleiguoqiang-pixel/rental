package com.rental.saas.pricing.dto;

import com.rental.saas.pricing.entity.Quote;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 报价详情响应DTO
 */
@Data
@Schema(description = "报价详情响应结果")
public class QuoteDetailResponse {
    
    @Schema(description = "报价信息")
    private Quote quote;
    
    @Schema(description = "增值服务模板列表")
    private List<Object> vasTemplates;
    
    @Schema(description = "取消规则")
    private Object cancellationPolicy;
    
    @Schema(description = "服务政策")
    private Object servicePolicy;
}
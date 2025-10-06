package com.rental.api.pricing.dto;

import com.rental.api.pricing.entity.Quote;
import com.rental.api.product.response.CancellationRuleTemplateResponse;
import com.rental.api.product.response.ServicePolicyTemplateResponse;
import com.rental.api.product.response.ValueAddedServiceTemplateResponse;
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
    private List<ValueAddedServiceTemplateResponse> vasTemplates;
    
    @Schema(description = "取消规则")
    private CancellationRuleTemplateResponse cancellationPolicy;
    
    @Schema(description = "服务政策")
    private ServicePolicyTemplateResponse servicePolicy;
}
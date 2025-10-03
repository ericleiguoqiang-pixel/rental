package com.rental.saas.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 取消规则模板请求DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "取消规则模板请求DTO")
public class CancellationRuleTemplateRequest {

    /**
     * 模板名称
     */
    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称", example = "标准取消规则")
    private String templateName;

    /**
     * 平日取消规则
     */
    @NotBlank(message = "平日取消规则不能为空")
    @Schema(description = "平日取消规则", example = "取车前24小时以上免费取消，24小时内取消收取10%手续费")
    private String weekdayRule;

    /**
     * 节假日取消规则
     */
    @NotBlank(message = "节假日取消规则不能为空")
    @Schema(description = "节假日取消规则", example = "取车前48小时以上免费取消，48小时内取消收取20%手续费")
    private String holidayRule;
}
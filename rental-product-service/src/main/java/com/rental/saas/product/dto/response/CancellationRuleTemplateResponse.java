package com.rental.saas.product.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 取消规则模板响应DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "取消规则模板响应DTO")
public class CancellationRuleTemplateResponse {

    /**
     * 模板ID
     */
    @Schema(description = "模板ID", example = "1")
    private Long id;

    /**
     * 模板名称
     */
    @Schema(description = "模板名称", example = "标准取消规则")
    private String templateName;

    /**
     * 平日取消规则
     */
    @Schema(description = "平日取消规则", example = "取车前24小时以上免费取消，24小时内取消收取10%手续费")
    private String weekdayRule;

    /**
     * 节假日取消规则
     */
    @Schema(description = "节假日取消规则", example = "取车前48小时以上免费取消，48小时内取消收取20%手续费")
    private String holidayRule;

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
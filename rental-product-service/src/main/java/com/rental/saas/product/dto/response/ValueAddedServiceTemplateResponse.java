package com.rental.saas.product.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 增值服务模板响应DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "增值服务模板响应DTO")
public class ValueAddedServiceTemplateResponse {

    /**
     * 模板ID
     */
    @Schema(description = "模板ID", example = "1")
    private Long id;

    /**
     * 模板名称
     */
    @Schema(description = "模板名称", example = "基础保障")
    private String templateName;

    /**
     * 服务类型:1-基础保障,2-优享保障,3-尊享保障
     */
    @Schema(description = "服务类型:1-基础保障,2-优享保障,3-尊享保障", example = "1")
    private Integer serviceType;

    /**
     * 价格(分)
     */
    @Schema(description = "价格(分)", example = "2000")
    private Integer price;

    /**
     * 起赔额(元)
     */
    @Schema(description = "起赔额(元)", example = "1500")
    private Integer deductible;

    /**
     * 包含轮胎损失:0-否,1-是
     */
    @Schema(description = "包含轮胎损失:0-否,1-是", example = "0")
    private Integer includeTireDamage;

    /**
     * 包含玻璃损失:0-否,1-是
     */
    @Schema(description = "包含玻璃损失:0-否,1-是", example = "0")
    private Integer includeGlassDamage;

    /**
     * 第三方保障(万元)
     */
    @Schema(description = "第三方保障(万元)", example = "30")
    private Integer thirdPartyCoverage;

    /**
     * 收取折旧费:0-否,1-是
     */
    @Schema(description = "收取折旧费:0-否,1-是", example = "1")
    private Integer chargeDepreciation;

    /**
     * 折旧费免赔额(元)
     */
    @Schema(description = "折旧费免赔额(元)", example = "500")
    private Integer depreciationDeductible;

    /**
     * 折旧费收取比例(%)
     */
    @Schema(description = "折旧费收取比例(%)", example = "20")
    private Integer depreciationRate;

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
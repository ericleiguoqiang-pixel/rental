package com.rental.saas.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 增值服务模板请求DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "增值服务模板请求DTO")
public class ValueAddedServiceTemplateRequest {

    /**
     * 模板名称
     */
    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称", example = "基础保障")
    private String templateName;

    /**
     * 服务类型:1-基础保障,2-优享保障,3-尊享保障
     */
    @NotNull(message = "服务类型不能为空")
    @Schema(description = "服务类型:1-基础保障,2-优享保障,3-尊享保障", example = "1")
    private Integer serviceType;

    /**
     * 价格(分)
     */
    @NotNull(message = "价格不能为空")
    @Schema(description = "价格(分)", example = "2000")
    private Integer price;

    /**
     * 起赔额(元)
     */
    @NotNull(message = "起赔额不能为空")
    @Schema(description = "起赔额(元)", example = "1500")
    private Integer deductible;

    /**
     * 包含轮胎损失:0-否,1-是
     */
    @NotNull(message = "包含轮胎损失标识不能为空")
    @Schema(description = "包含轮胎损失:0-否,1-是", example = "0")
    private Integer includeTireDamage;

    /**
     * 包含玻璃损失:0-否,1-是
     */
    @NotNull(message = "包含玻璃损失标识不能为空")
    @Schema(description = "包含玻璃损失:0-否,1-是", example = "0")
    private Integer includeGlassDamage;

    /**
     * 第三方保障(万元)
     */
    @NotNull(message = "第三方保障不能为空")
    @Schema(description = "第三方保障(万元)", example = "30")
    private Integer thirdPartyCoverage;

    /**
     * 收取折旧费:0-否,1-是
     */
    @NotNull(message = "收取折旧费标识不能为空")
    @Schema(description = "收取折旧费:0-否,1-是", example = "1")
    private Integer chargeDepreciation;

    /**
     * 折旧费免赔额(元)
     */
    @NotNull(message = "折旧费免赔额不能为空")
    @Schema(description = "折旧费免赔额(元)", example = "500")
    private Integer depreciationDeductible;

    /**
     * 折旧费收取比例(%)
     */
    @NotNull(message = "折旧费收取比例不能为空")
    @Schema(description = "折旧费收取比例(%)", example = "20")
    private Integer depreciationRate;
}
package com.rental.saas.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 增值服务模板实体
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("value_added_service_template")
@Schema(description = "增值服务模板")
public class ValueAddedServiceTemplate extends TenantBaseEntity {

    /**
     * 模板名称
     */
    @TableField("template_name")
    @Schema(description = "模板名称", example = "基础保障")
    private String templateName;

    /**
     * 服务类型:1-基础保障,2-优享保障,3-尊享保障
     */
    @TableField("service_type")
    @Schema(description = "服务类型:1-基础保障,2-优享保障,3-尊享保障", example = "1")
    private Integer serviceType;

    /**
     * 价格(分)
     */
    @TableField("price")
    @Schema(description = "价格(分)", example = "2000")
    private Integer price;

    /**
     * 起赔额(元)
     */
    @TableField("deductible")
    @Schema(description = "起赔额(元)", example = "1500")
    private Integer deductible;

    /**
     * 包含轮胎损失:0-否,1-是
     */
    @TableField("include_tire_damage")
    @Schema(description = "包含轮胎损失:0-否,1-是", example = "0")
    private Integer includeTireDamage;

    /**
     * 包含玻璃损失:0-否,1-是
     */
    @TableField("include_glass_damage")
    @Schema(description = "包含玻璃损失:0-否,1-是", example = "0")
    private Integer includeGlassDamage;

    /**
     * 第三方保障(万元)
     */
    @TableField("third_party_coverage")
    @Schema(description = "第三方保障(万元)", example = "30")
    private Integer thirdPartyCoverage;

    /**
     * 收取折旧费:0-否,1-是
     */
    @TableField("charge_depreciation")
    @Schema(description = "收取折旧费:0-否,1-是", example = "1")
    private Integer chargeDepreciation;

    /**
     * 折旧费免赔额(元)
     */
    @TableField("depreciation_deductible")
    @Schema(description = "折旧费免赔额(元)", example = "500")
    private Integer depreciationDeductible;

    /**
     * 折旧费收取比例(%)
     */
    @TableField("depreciation_rate")
    @Schema(description = "折旧费收取比例(%)", example = "20")
    private Integer depreciationRate;
}
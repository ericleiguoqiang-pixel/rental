package com.rental.saas.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 车型商品实体
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("car_model_product")
@Schema(description = "车型商品")
public class CarModelProduct extends TenantBaseEntity {

    /**
     * 门店ID
     */
    @TableField("store_id")
    @Schema(description = "门店ID", example = "1")
    private Long storeId;

    /**
     * 车型ID
     */
    @TableField("car_model_id")
    @Schema(description = "车型ID", example = "1")
    private Long carModelId;

    /**
     * 商品名称
     */
    @TableField("product_name")
    @Schema(description = "商品名称", example = "大众朗逸经济型")
    private String productName;

    /**
     * 商品描述
     */
    @TableField("product_description")
    @Schema(description = "商品描述", example = "经济实用的家用轿车")
    private String productDescription;

    /**
     * 车损押金(分)
     */
    @TableField("damage_deposit")
    @Schema(description = "车损押金(分)", example = "200000")
    private Integer damageDeposit;

    /**
     * 违章押金(分)
     */
    @TableField("violation_deposit")
    @Schema(description = "违章押金(分)", example = "100000")
    private Integer violationDeposit;

    /**
     * 周中价格(分)
     */
    @TableField("weekday_price")
    @Schema(description = "周中价格(分)", example = "30000")
    private Integer weekdayPrice;

    /**
     * 周末价格(分)
     */
    @TableField("weekend_price")
    @Schema(description = "周末价格(分)", example = "40000")
    private Integer weekendPrice;

    /**
     * 周中定义
     */
    @TableField("weekday_definition")
    @Schema(description = "周中定义", example = "1,2,3,4,5")
    private String weekdayDefinition;

    /**
     * 周末定义
     */
    @TableField("weekend_definition")
    @Schema(description = "周末定义", example = "6,7")
    private String weekendDefinition;

    /**
     * 车型标签(JSON格式)
     */
    @TableField("tags")
    @Schema(description = "车型标签(JSON格式)", example = "[\"经济型\",\"舒适型\"]")
    private String tags;

    /**
     * 增值服务模板ID
     */
    @TableField("vas_template_id")
    @Schema(description = "增值服务模板ID", example = "1")
    private Long vasTemplateId;

    /**
     * 优享保障模板ID
     */
    @TableField("vas_template_id_vip")
    @Schema(description = "优享保障模板ID", example = "2")
    private Long vasTemplateIdVip;

    /**
     * 尊享保障模板ID
     */
    @TableField("vas_template_id_vvip")
    @Schema(description = "尊享保障模板ID", example = "3")
    private Long vasTemplateIdVvip;

    /**
     * 取消规则模板ID
     */
    @TableField("cancellation_template_id")
    @Schema(description = "取消规则模板ID", example = "1")
    private Long cancellationTemplateId;

    /**
     * 服务政策模板ID
     */
    @TableField("service_policy_template_id")
    @Schema(description = "服务政策模板ID", example = "1")
    private Long servicePolicyTemplateId;

    /**
     * 上架状态:0-下架,1-上架
     */
    @TableField("online_status")
    @Schema(description = "上架状态:0-下架,1-上架", example = "1")
    private Integer onlineStatus;
}
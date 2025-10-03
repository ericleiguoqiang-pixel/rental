package com.rental.saas.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 服务政策模板实体
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("service_policy_template")
@Schema(description = "服务政策模板")
public class ServicePolicyTemplate extends TenantBaseEntity {

    /**
     * 模板名称
     */
    @TableField("template_name")
    @Schema(description = "模板名称", example = "标准服务政策")
    private String templateName;

    /**
     * 里程限制
     */
    @TableField("mileage_limit")
    @Schema(description = "里程限制", example = "日限里程300公里，总里程无限制")
    private String mileageLimit;

    /**
     * 提前取车
     */
    @TableField("early_pickup")
    @Schema(description = "提前取车", example = "可提前1小时取车，超出按小时计费")
    private String earlyPickup;

    /**
     * 延迟取车
     */
    @TableField("late_pickup")
    @Schema(description = "延迟取车", example = "延迟1小时内免费，超出按小时计费")
    private String latePickup;

    /**
     * 提前还车
     */
    @TableField("early_return")
    @Schema(description = "提前还车", example = "提前还车不退费")
    private String earlyReturn;

    /**
     * 续租
     */
    @TableField("renewal")
    @Schema(description = "续租", example = "可在线申请续租")
    private String renewal;

    /**
     * 强行续租
     */
    @TableField("forced_renewal")
    @Schema(description = "强行续租", example = "逾期未还强制续租，加收20%费用")
    private String forcedRenewal;

    /**
     * 取车材料
     */
    @TableField("pickup_materials")
    @Schema(description = "取车材料", example = "需要驾驶证、身份证原件")
    private String pickupMaterials;

    /**
     * 城市限行规则
     */
    @TableField("city_restriction")
    @Schema(description = "城市限行规则", example = "遵守当地限行规定")
    private String cityRestriction;

    /**
     * 用车区域限制
     */
    @TableField("usage_area_limit")
    @Schema(description = "用车区域限制", example = "禁止出省使用")
    private String usageAreaLimit;

    /**
     * 油费电费
     */
    @TableField("fuel_fee")
    @Schema(description = "油费电费", example = "还车时请加满油/充满电")
    private String fuelFee;

    /**
     * 随车物品损失
     */
    @TableField("personal_belongings_loss")
    @Schema(description = "随车物品损失", example = "车内物品丢失概不负责")
    private String personalBelongingsLoss;

    /**
     * 违章处理
     */
    @TableField("violation_handling")
    @Schema(description = "违章处理", example = "违章由客户承担并处理")
    private String violationHandling;

    /**
     * 道路救援
     */
    @TableField("roadside_assistance")
    @Schema(description = "道路救援", example = "24小时道路救援服务")
    private String roadsideAssistance;

    /**
     * 强制收车
     */
    @TableField("forced_recovery")
    @Schema(description = "强制收车", example = "逾期未还车辆将强制收回")
    private String forcedRecovery;

    /**
     * ETC费用
     */
    @TableField("etc_fee")
    @Schema(description = "ETC费用", example = "ETC费用实际产生实际承担")
    private String etcFee;

    /**
     * 清洁费
     */
    @TableField("cleaning_fee")
    @Schema(description = "清洁费", example = "还车时请保持车内清洁")
    private String cleaningFee;

    /**
     * 发票说明
     */
    @TableField("invoice_info")
    @Schema(description = "发票说明", example = "可开具租车费发票")
    private String invoiceInfo;
}
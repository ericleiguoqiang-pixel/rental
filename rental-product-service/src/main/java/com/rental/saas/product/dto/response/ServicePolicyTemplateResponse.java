package com.rental.saas.product.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 服务政策模板响应DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "服务政策模板响应DTO")
public class ServicePolicyTemplateResponse {

    /**
     * 模板ID
     */
    @Schema(description = "模板ID", example = "1")
    private Long id;

    /**
     * 模板名称
     */
    @Schema(description = "模板名称", example = "标准服务政策")
    private String templateName;

    /**
     * 里程限制
     */
    @Schema(description = "里程限制", example = "日限里程300公里，总里程无限制")
    private String mileageLimit;

    /**
     * 提前取车
     */
    @Schema(description = "提前取车", example = "可提前1小时取车，超出按小时计费")
    private String earlyPickup;

    /**
     * 延迟取车
     */
    @Schema(description = "延迟取车", example = "延迟1小时内免费，超出按小时计费")
    private String latePickup;

    /**
     * 提前还车
     */
    @Schema(description = "提前还车", example = "提前还车不退费")
    private String earlyReturn;

    /**
     * 续租
     */
    @Schema(description = "续租", example = "可在线申请续租")
    private String renewal;

    /**
     * 强行续租
     */
    @Schema(description = "强行续租", example = "逾期未还强制续租，加收20%费用")
    private String forcedRenewal;

    /**
     * 取车材料
     */
    @Schema(description = "取车材料", example = "需要驾驶证、身份证原件")
    private String pickupMaterials;

    /**
     * 城市限行规则
     */
    @Schema(description = "城市限行规则", example = "遵守当地限行规定")
    private String cityRestriction;

    /**
     * 用车区域限制
     */
    @Schema(description = "用车区域限制", example = "禁止出省使用")
    private String usageAreaLimit;

    /**
     * 油费电费
     */
    @Schema(description = "油费电费", example = "还车时请加满油/充满电")
    private String fuelFee;

    /**
     * 随车物品损失
     */
    @Schema(description = "随车物品损失", example = "车内物品丢失概不负责")
    private String personalBelongingsLoss;

    /**
     * 违章处理
     */
    @Schema(description = "违章处理", example = "违章由客户承担并处理")
    private String violationHandling;

    /**
     * 道路救援
     */
    @Schema(description = "道路救援", example = "24小时道路救援服务")
    private String roadsideAssistance;

    /**
     * 强制收车
     */
    @Schema(description = "强制收车", example = "逾期未还车辆将强制收回")
    private String forcedRecovery;

    /**
     * ETC费用
     */
    @Schema(description = "ETC费用", example = "ETC费用实际产生实际承担")
    private String etcFee;

    /**
     * 清洁费
     */
    @Schema(description = "清洁费", example = "还车时请保持车内清洁")
    private String cleaningFee;

    /**
     * 发票说明
     */
    @Schema(description = "发票说明", example = "可开具租车费发票")
    private String invoiceInfo;

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
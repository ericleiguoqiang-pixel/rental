package com.rental.saas.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 取消规则模板实体
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cancellation_rule_template")
@Schema(description = "取消规则模板")
public class CancellationRuleTemplate extends TenantBaseEntity {

    /**
     * 模板名称
     */
    @TableField("template_name")
    @Schema(description = "模板名称", example = "标准取消规则")
    private String templateName;

    /**
     * 平日取消规则
     */
    @TableField("weekday_rule")
    @Schema(description = "平日取消规则", example = "取车前24小时以上免费取消，24小时内取消收取10%手续费")
    private String weekdayRule;

    /**
     * 节假日取消规则
     */
    @TableField("holiday_rule")
    @Schema(description = "节假日取消规则", example = "取车前48小时以上免费取消，48小时内取消收取20%手续费")
    private String holidayRule;
}
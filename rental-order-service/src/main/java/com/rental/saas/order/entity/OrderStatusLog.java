package com.rental.saas.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 订单状态变更记录实体
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_status_log")
@Schema(description = "订单状态变更记录")
public class OrderStatusLog extends BaseEntity {

    /**
     * 订单ID
     */
    @TableField("order_id")
    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    /**
     * 原状态
     */
    @TableField("old_status")
    @Schema(description = "原状态", example = "1")
    private Integer oldStatus;

    /**
     * 新状态
     */
    @TableField("new_status")
    @Schema(description = "新状态", example = "2")
    private Integer newStatus;

    /**
     * 变更原因
     */
    @TableField("change_reason")
    @Schema(description = "变更原因", example = "用户支付完成")
    private String changeReason;

    /**
     * 操作人ID
     */
    @TableField("operator_id")
    @Schema(description = "操作人ID", example = "1")
    private Long operatorId;

    /**
     * 操作人姓名
     */
    @TableField("operator_name")
    @Schema(description = "操作人姓名", example = "管理员")
    private String operatorName;

    /**
     * 变更时间
     */
    @TableField("change_time")
    @Schema(description = "变更时间")
    private LocalDateTime changeTime;
}
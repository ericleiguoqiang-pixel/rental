package com.rental.saas.basedata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * 门店实体
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("store")
@Schema(description = "门店")
public class Store extends TenantBaseEntity {

    /**
     * 门店名称
     */
    @TableField("store_name")
    @NotBlank(message = "门店名称不能为空")
    @Size(max = 100, message = "门店名称长度不能超过100个字符")
    @Schema(description = "门店名称", example = "北京朝阳门店")
    private String storeName;

    /**
     * 所在城市
     */
    @TableField("city")
    @NotBlank(message = "所在城市不能为空")
    @Size(max = 50, message = "城市名称长度不能超过50个字符")
    @Schema(description = "所在城市", example = "北京")
    private String city;

    /**
     * 详细地址
     */
    @TableField("address")
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "地址长度不能超过200个字符")
    @Schema(description = "详细地址", example = "北京市朝阳区某某街道某某号")
    private String address;

    /**
     * 经度
     */
    @TableField("longitude")
    @DecimalMin(value = "-180.0", message = "经度必须在-180到180之间")
    @DecimalMax(value = "180.0", message = "经度必须在-180到180之间")
    @Schema(description = "经度", example = "116.397428")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @TableField("latitude")
    @DecimalMin(value = "-90.0", message = "纬度必须在-90到90之间")
    @DecimalMax(value = "90.0", message = "纬度必须在-90到90之间")
    @Schema(description = "纬度", example = "39.90923")
    private BigDecimal latitude;

    /**
     * 营业开始时间
     */
    @TableField("business_start_time")
    @NotNull(message = "营业开始时间不能为空")
    @Schema(description = "营业开始时间", example = "08:00:00")
    private LocalTime businessStartTime;

    /**
     * 营业结束时间
     */
    @TableField("business_end_time")
    @NotNull(message = "营业结束时间不能为空")
    @Schema(description = "营业结束时间", example = "22:00:00")
    private LocalTime businessEndTime;

    /**
     * 审核状态：0-待审核，1-审核通过，2-审核拒绝
     */
    @TableField("audit_status")
    @NotNull(message = "审核状态不能为空")
    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    /**
     * 上架状态：0-下架，1-上架
     */
    @TableField("online_status")
    @NotNull(message = "上架状态不能为空")
    @Schema(description = "上架状态", example = "1")
    private Integer onlineStatus;

    /**
     * 最小提前预定时间(小时)
     */
    @TableField("min_advance_hours")
    @NotNull(message = "最小提前预定时间不能为空")
    @Min(value = 1, message = "最小提前预定时间不能少于1小时")
    @Schema(description = "最小提前预定时间", example = "2")
    private Integer minAdvanceHours;

    /**
     * 最大提前预定天数
     */
    @TableField("max_advance_days")
    @NotNull(message = "最大提前预定天数不能为空")
    @Min(value = 1, message = "最大提前预定天数不能少于1天")
    @Max(value = 365, message = "最大提前预定天数不能超过365天")
    @Schema(description = "最大提前预定天数", example = "30")
    private Integer maxAdvanceDays;

    /**
     * 车行手续费(分)
     */
    @TableField("service_fee")
    @NotNull(message = "车行手续费不能为空")
    @Min(value = 0, message = "车行手续费不能为负数")
    @Schema(description = "车行手续费", example = "1000")
    private Integer serviceFee;

    /**
     * 审核备注
     */
    @TableField("audit_remark")
    @Schema(description = "审核备注", example = "材料齐全，准予通过")
    private String auditRemark;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    @Schema(description = "审核时间", example = "2024-01-01 12:00:00")
    private java.time.LocalDateTime auditTime;

    /**
     * 审核人ID
     */
    @TableField("auditor_id")
    @Schema(description = "审核人ID", example = "1")
    private Long auditorId;
}
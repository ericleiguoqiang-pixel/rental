package com.rental.saas.basedata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.*;

/**
 * 服务范围实体
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("service_area")
@Schema(description = "服务范围")
public class ServiceArea extends TenantBaseEntity {

    /**
     * 门店ID
     */
    @TableField("store_id")
    @NotNull(message = "门店ID不能为空")
    @Schema(description = "门店ID", example = "1")
    private Long storeId;

    /**
     * 区域名称
     */
    @TableField("area_name")
    @NotBlank(message = "区域名称不能为空")
    @Size(max = 100, message = "区域名称长度不能超过100个字符")
    @Schema(description = "区域名称", example = "朝阳区取车区域")
    private String areaName;

    /**
     * 区域类型:1-取车区域,2-还车区域
     */
    @TableField("area_type")
    @NotNull(message = "区域类型不能为空")
    @Min(value = 1, message = "区域类型必须是1或2")
    @Max(value = 2, message = "区域类型必须是1或2")
    @Schema(description = "区域类型:1-取车区域,2-还车区域", example = "1")
    private Integer areaType;

    /**
     * 电子围栏坐标(JSON格式)
     */
    @TableField("fence_coordinates")
    @Schema(description = "电子围栏坐标(JSON格式)")
    private String fenceCoordinates;

    /**
     * 提前预定时间(小时)
     */
    @TableField("advance_hours")
    @NotNull(message = "提前预定时间不能为空")
    @Min(value = 0, message = "提前预定时间不能为负数")
    @Schema(description = "提前预定时间(小时)", example = "2")
    private Integer advanceHours;

    /**
     * 服务开始时间
     */
    @TableField("service_start_time")
    @NotBlank(message = "服务开始时间不能为空")
    @Size(max = 5, message = "服务开始时间格式应为HH:MM")
    @Schema(description = "服务开始时间(HH:MM)", example = "08:00")
    private String serviceStartTime;

    /**
     * 服务结束时间
     */
    @TableField("service_end_time")
    @NotBlank(message = "服务结束时间不能为空")
    @Size(max = 5, message = "服务结束时间格式应为HH:MM")
    @Schema(description = "服务结束时间(HH:MM)", example = "22:00")
    private String serviceEndTime;

    /**
     * 是否提供送车上门:0-否,1-是
     */
    @TableField("door_to_door_delivery")
    @NotNull(message = "是否提供送车上门不能为空")
    @Min(value = 0, message = "是否提供送车上门必须是0或1")
    @Max(value = 1, message = "是否提供送车上门必须是0或1")
    @Schema(description = "是否提供送车上门:0-否,1-是", example = "1")
    private Integer doorToDoorDelivery;

    /**
     * 送车上门费用(分)
     */
    @TableField("delivery_fee")
    @NotNull(message = "送车上门费用不能为空")
    @Min(value = 0, message = "送车上门费用不能为负数")
    @Schema(description = "送车上门费用(分)", example = "2000")
    private Integer deliveryFee;

    /**
     * 是否免费接至门店:0-否,1-是
     */
    @TableField("free_pickup_to_store")
    @NotNull(message = "是否免费接至门店不能为空")
    @Min(value = 0, message = "是否免费接至门店必须是0或1")
    @Max(value = 1, message = "是否免费接至门店必须是0或1")
    @Schema(description = "是否免费接至门店:0-否,1-是", example = "0")
    private Integer freePickupToStore;
}
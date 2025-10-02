package com.rental.saas.basedata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * 车辆实体
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vehicle")
@Schema(description = "车辆")
public class Vehicle extends TenantBaseEntity {

    /**
     * 归属门店ID
     */
    @TableField("store_id")
    @NotNull(message = "归属门店不能为空")
    @Schema(description = "归属门店ID", example = "1")
    private Long storeId;

    /**
     * 车牌号
     */
    @TableField("license_plate")
    @NotBlank(message = "车牌号不能为空")
    @Pattern(regexp = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳]$", 
             message = "车牌号格式不正确")
    @Schema(description = "车牌号", example = "京A12345")
    private String licensePlate;

    /**
     * 车型ID
     */
    @TableField("car_model_id")
    @NotNull(message = "车型不能为空")
    @Schema(description = "车型ID", example = "1")
    private Long carModelId;

    /**
     * 牌照类型：1-普通，2-京牌，3-沪牌，4-深牌，5-粤A牌，6-杭州牌
     */
    @TableField("license_type")
    @NotNull(message = "牌照类型不能为空")
    @Min(value = 1, message = "牌照类型值错误")
    @Max(value = 6, message = "牌照类型值错误")
    @Schema(description = "牌照类型", example = "1")
    private Integer licenseType;

    /**
     * 注册日期
     */
    @TableField("register_date")
    @NotNull(message = "注册日期不能为空")
    @Past(message = "注册日期不能是未来日期")
    @Schema(description = "注册日期", example = "2023-01-01")
    private LocalDate registerDate;

    /**
     * 车架号
     */
    @TableField("vin")
    @NotBlank(message = "车架号不能为空")
    @Pattern(regexp = "^[A-HJ-NPR-Z\\d]{17}$", message = "车架号格式不正确")
    @Schema(description = "车架号", example = "LSGBB54E4ES123456")
    private String vin;

    /**
     * 发动机号
     */
    @TableField("engine_no")
    @NotBlank(message = "发动机号不能为空")
    @Size(max = 50, message = "发动机号长度不能超过50个字符")
    @Schema(description = "发动机号", example = "ABC123456")
    private String engineNo;

    /**
     * 使用性质：1-营运，2-非营运
     */
    @TableField("usage_nature")
    @NotNull(message = "使用性质不能为空")
    @Min(value = 1, message = "使用性质值错误")
    @Max(value = 2, message = "使用性质值错误")
    @Schema(description = "使用性质", example = "1")
    private Integer usageNature;

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
     * 车辆状态：1-空闲，2-租出，3-维修，4-报废
     */
    @TableField("vehicle_status")
    @NotNull(message = "车辆状态不能为空")
    @Min(value = 1, message = "车辆状态值错误")
    @Max(value = 4, message = "车辆状态值错误")
    @Schema(description = "车辆状态", example = "1")
    private Integer vehicleStatus;

    /**
     * 总里程(公里)
     */
    @TableField("mileage")
    @Min(value = 0, message = "总里程不能为负数")
    @Schema(description = "总里程", example = "50000")
    private Integer mileage;

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
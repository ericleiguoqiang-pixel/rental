package com.rental.saas.basedata.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * 门店创建请求DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "门店创建请求")
public class StoreCreateRequest {

    /**
     * 门店名称
     */
    @NotBlank(message = "门店名称不能为空")
    @Size(max = 100, message = "门店名称长度不能超过100个字符")
    @Schema(description = "门店名称", example = "北京朝阳门店")
    private String storeName;

    /**
     * 所在城市
     */
    @NotBlank(message = "所在城市不能为空")
    @Size(max = 50, message = "城市名称长度不能超过50个字符")
    @Schema(description = "所在城市", example = "北京")
    private String city;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "地址长度不能超过200个字符")
    @Schema(description = "详细地址", example = "北京市朝阳区某某街道某某号")
    private String address;

    /**
     * 经度
     */
    @DecimalMin(value = "-180.0", message = "经度必须在-180到180之间")
    @DecimalMax(value = "180.0", message = "经度必须在-180到180之间")
    @Schema(description = "经度", example = "116.397428")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @DecimalMin(value = "-90.0", message = "纬度必须在-90到90之间")
    @DecimalMax(value = "90.0", message = "纬度必须在-90到90之间")
    @Schema(description = "纬度", example = "39.90923")
    private BigDecimal latitude;

    /**
     * 营业开始时间
     */
    @NotNull(message = "营业开始时间不能为空")
    @Schema(description = "营业开始时间", example = "08:00:00")
    private LocalTime businessStartTime;

    /**
     * 营业结束时间
     */
    @NotNull(message = "营业结束时间不能为空")
    @Schema(description = "营业结束时间", example = "22:00:00")
    private LocalTime businessEndTime;

    /**
     * 最小提前预定时间(小时)
     */
    @NotNull(message = "最小提前预定时间不能为空")
    @Min(value = 1, message = "最小提前预定时间不能少于1小时")
    @Schema(description = "最小提前预定时间", example = "2")
    private Integer minAdvanceHours;

    /**
     * 最大提前预定天数
     */
    @NotNull(message = "最大提前预定天数不能为空")
    @Min(value = 1, message = "最大提前预定天数不能少于1天")
    @Max(value = 365, message = "最大提前预定天数不能超过365天")
    @Schema(description = "最大提前预定天数", example = "30")
    private Integer maxAdvanceDays;

    /**
     * 车行手续费(分)
     */
    @NotNull(message = "车行手续费不能为空")
    @Min(value = 0, message = "车行手续费不能为负数")
    @Schema(description = "车行手续费", example = "1000")
    private Integer serviceFee;
}
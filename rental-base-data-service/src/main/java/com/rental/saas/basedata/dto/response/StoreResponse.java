package com.rental.saas.basedata.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 门店响应DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "门店信息响应")
public class StoreResponse {

    /**
     * 门店ID
     */
    @Schema(description = "门店ID", example = "1")
    private Long id;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID", example = "1")
    private Long tenantId;

    /**
     * 门店名称
     */
    @Schema(description = "门店名称", example = "北京朝阳门店")
    private String storeName;

    /**
     * 所在城市
     */
    @Schema(description = "所在城市", example = "北京")
    private String city;

    /**
     * 详细地址
     */
    @Schema(description = "详细地址", example = "北京市朝阳区某某街道某某号")
    private String address;

    /**
     * 经度
     */
    @Schema(description = "经度", example = "116.397428")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @Schema(description = "纬度", example = "39.90923")
    private BigDecimal latitude;

    /**
     * 营业开始时间
     */
    @Schema(description = "营业开始时间", example = "08:00:00")
    private LocalTime businessStartTime;

    /**
     * 营业结束时间
     */
    @Schema(description = "营业结束时间", example = "22:00:00")
    private LocalTime businessEndTime;

    /**
     * 审核状态：0-待审核，1-审核通过，2-审核拒绝
     */
    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    /**
     * 审核状态描述
     */
    @Schema(description = "审核状态描述", example = "审核通过")
    private String auditStatusDesc;

    /**
     * 上架状态：0-下架，1-上架
     */
    @Schema(description = "上架状态", example = "1")
    private Integer onlineStatus;

    /**
     * 上架状态描述
     */
    @Schema(description = "上架状态描述", example = "上架")
    private String onlineStatusDesc;

    /**
     * 最小提前预定时间(小时)
     */
    @Schema(description = "最小提前预定时间", example = "2")
    private Integer minAdvanceHours;

    /**
     * 最大提前预定天数
     */
    @Schema(description = "最大提前预定天数", example = "30")
    private Integer maxAdvanceDays;

    /**
     * 车行手续费(分)
     */
    @Schema(description = "车行手续费", example = "1000")
    private Integer serviceFee;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2024-01-01 12:00:00")
    private LocalDateTime updatedTime;

    /**
     * 车辆数量统计
//     */
//    @Schema(description = "车辆数量", example = "10")
//    private Integer vehicleCount;
}
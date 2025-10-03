package com.rental.api.basedata.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 车辆响应DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "车辆信息响应")
public class VehicleResponse {

    /**
     * 车辆ID
     */
    @Schema(description = "车辆ID", example = "1")
    private Long id;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID", example = "1")
    private Long tenantId;

    /**
     * 归属门店ID
     */
    @Schema(description = "归属门店ID", example = "1")
    private Long storeId;

    /**
     * 门店名称
     */
    @Schema(description = "门店名称", example = "北京朝阳门店")
    private String storeName;

    /**
     * 车牌号
     */
    @Schema(description = "车牌号", example = "京A12345")
    private String licensePlate;

    /**
     * 车型ID
     */
    @Schema(description = "车型ID", example = "1")
    private Long carModelId;

    /**
     * 车型信息
     */
    @Schema(description = "车型信息")
    private CarModelResponse carModel;

    /**
     * 牌照类型：1-普通，2-京牌，3-沪牌，4-深牌，5-粤A牌，6-杭州牌
     */
    @Schema(description = "牌照类型", example = "1")
    private Integer licenseType;

    /**
     * 牌照类型描述
     */
    @Schema(description = "牌照类型描述", example = "普通")
    private String licenseTypeDesc;

    /**
     * 注册日期
     */
    @Schema(description = "注册日期", example = "2023-01-01")
    private LocalDate registerDate;

    /**
     * 车架号
     */
    @Schema(description = "车架号", example = "LSGBB54E4ES123456")
    private String vin;

    /**
     * 发动机号
     */
    @Schema(description = "发动机号", example = "ABC123456")
    private String engineNo;

    /**
     * 使用性质：1-营运，2-非营运
     */
    @Schema(description = "使用性质", example = "1")
    private Integer usageNature;

    /**
     * 使用性质描述
     */
    @Schema(description = "使用性质描述", example = "营运")
    private String usageNatureDesc;

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
     * 车辆状态：1-空闲，2-租出，3-维修，4-报废
     */
    @Schema(description = "车辆状态", example = "1")
    private Integer vehicleStatus;

    /**
     * 车辆状态描述
     */
    @Schema(description = "车辆状态描述", example = "空闲")
    private String vehicleStatusDesc;

    /**
     * 总里程(公里)
     */
    @Schema(description = "总里程", example = "50000")
    private Integer mileage;

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


}
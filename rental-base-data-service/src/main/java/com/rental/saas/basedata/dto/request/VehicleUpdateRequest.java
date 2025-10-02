package com.rental.saas.basedata.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * 车辆更新请求DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "车辆更新请求")
public class VehicleUpdateRequest {

    /**
     * 归属门店ID
     */
    @Schema(description = "归属门店ID", example = "1")
    private Long storeId;

    /**
     * 车牌号
     */
    @Pattern(regexp = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳]$", 
             message = "车牌号格式不正确")
    @Schema(description = "车牌号", example = "京A12345")
    private String licensePlate;

    /**
     * 车型ID
     */
    @Schema(description = "车型ID", example = "1")
    private Long carModelId;

    /**
     * 牌照类型：1-普通，2-京牌，3-沪牌，4-深牌，5-粤A牌，6-杭州牌
     */
    @Min(value = 1, message = "牌照类型值错误")
    @Max(value = 6, message = "牌照类型值错误")
    @Schema(description = "牌照类型", example = "1")
    private Integer licenseType;

    /**
     * 注册日期
     */
    @Past(message = "注册日期不能是未来日期")
    @Schema(description = "注册日期", example = "2023-01-01")
    private LocalDate registerDate;

    /**
     * 发动机号
     */
    @Size(max = 50, message = "发动机号长度不能超过50个字符")
    @Schema(description = "发动机号", example = "ABC123456")
    private String engineNo;

    /**
     * 使用性质：1-营运，2-非营运
     */
    @Min(value = 1, message = "使用性质值错误")
    @Max(value = 2, message = "使用性质值错误")
    @Schema(description = "使用性质", example = "1")
    private Integer usageNature;

    /**
     * 车辆状态：1-空闲，2-租出，3-维修，4-报废
     */
    @Min(value = 1, message = "车辆状态值错误")
    @Max(value = 4, message = "车辆状态值错误")
    @Schema(description = "车辆状态", example = "1")
    private Integer vehicleStatus;

    /**
     * 总里程(公里)
     */
    @Min(value = 0, message = "总里程不能为负数")
    @Schema(description = "总里程", example = "50000")
    private Integer mileage;
}
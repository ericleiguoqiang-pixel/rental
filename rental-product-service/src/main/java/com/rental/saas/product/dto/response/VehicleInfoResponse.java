package com.rental.saas.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 车辆信息响应DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "车辆信息响应")
public class VehicleInfoResponse {

    /**
     * 车辆ID
     */
    @Schema(description = "车辆ID", example = "1")
    private Long id;

    /**
     * 车牌号
     */
    @Schema(description = "车牌号", example = "沪A12345")
    private String licensePlate;

    /**
     * 车辆状态
     */
    @Schema(description = "车辆状态", example = "1")
    private Integer vehicleStatus;

    /**
     * 是否已关联
     */
    @Schema(description = "是否已关联到当前商品", example = "true")
    private Boolean related;
}
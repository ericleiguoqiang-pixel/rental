package com.rental.saas.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 商品车辆关联请求DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "商品车辆关联请求")
public class ProductVehicleRelationRequest {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    @Min(value = 1, message = "商品ID必须大于0")
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    /**
     * 车辆ID列表
     */
    @NotNull(message = "车辆ID列表不能为空")
    @Schema(description = "车辆ID列表")
    private List<Long> vehicleIds;
}
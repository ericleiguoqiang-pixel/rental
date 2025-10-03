package com.rental.saas.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 车型商品关联车辆实体
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_vehicle_relation")
@Schema(description = "车型商品关联车辆")
public class ProductVehicleRelation extends TenantBaseEntity {

    /**
     * 商品ID
     */
    @TableField("product_id")
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    /**
     * 车辆ID
     */
    @TableField("vehicle_id")
    @Schema(description = "车辆ID", example = "1")
    private Long vehicleId;
}
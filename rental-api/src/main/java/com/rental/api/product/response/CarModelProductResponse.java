package com.rental.api.product.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 车型商品响应DTO
 */
@Data
@Schema(description = "车型商品响应")
public class CarModelProductResponse {
    
    @Schema(description = "商品ID")
    private Long id;
    
    @Schema(description = "门店ID")
    private Long storeId;

    @Schema(description = "租户ID")
    private Long tenantId;
    
    @Schema(description = "车型ID")
    private Long carModelId;
    
    @Schema(description = "商品名称")
    private String productName;
    
    @Schema(description = "商品描述")
    private String productDescription;
    
    @Schema(description = "车损押金(分)")
    private Integer damageDeposit;
    
    @Schema(description = "违章押金(分)")
    private Integer violationDeposit;
    
    @Schema(description = "周中价格(分)")
    private Integer weekdayPrice;
    
    @Schema(description = "周末价格(分)")
    private Integer weekendPrice;
    
    @Schema(description = "增值服务模板ID")
    private Long vasTemplateId;
    
    @Schema(description = "优享保障模板ID")
    private Long vasTemplateIdVip;
    
    @Schema(description = "尊享保障模板ID")
    private Long vasTemplateIdVvip;
    
    @Schema(description = "取消规则模板ID")
    private Long cancellationTemplateId;
    
    @Schema(description = "服务政策模板ID")
    private Long servicePolicyTemplateId;
}
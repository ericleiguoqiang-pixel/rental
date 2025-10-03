package com.rental.saas.product.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 车型商品响应DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "车型商品响应DTO")
public class CarModelProductResponse {

    /**
     * 商品ID
     */
    @Schema(description = "商品ID", example = "1")
    private Long id;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID", example = "1")
    private Long tenantId;

    /**
     * 门店ID
     */
    @Schema(description = "门店ID", example = "1")
    private Long storeId;

    /**
     * 车型ID
     */
    @Schema(description = "车型ID", example = "1")
    private Long carModelId;

    /**
     * 车型名称
     */
    @Schema(description = "车型名称", example = "大众朗逸2025款")
    private String carModelName;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称", example = "大众朗逸经济型")
    private String productName;

    /**
     * 商品描述
     */
    @Schema(description = "商品描述", example = "经济实用的家用轿车")
    private String productDescription;

    /**
     * 车损押金(分)
     */
    @Schema(description = "车损押金(分)", example = "200000")
    private Integer damageDeposit;

    /**
     * 违章押金(分)
     */
    @Schema(description = "违章押金(分)", example = "100000")
    private Integer violationDeposit;

    /**
     * 周中价格(分)
     */
    @Schema(description = "周中价格(分)", example = "30000")
    private Integer weekdayPrice;

    /**
     * 周末价格(分)
     */
    @Schema(description = "周末价格(分)", example = "40000")
    private Integer weekendPrice;

    /**
     * 周中定义
     */
    @Schema(description = "周中定义", example = "1,2,3,4,5")
    private String weekdayDefinition;

    /**
     * 周末定义
     */
    @Schema(description = "周末定义", example = "6,7")
    private String weekendDefinition;

    /**
     * 车型标签(JSON格式)
     */
    @Schema(description = "车型标签(JSON格式)", example = "[\"经济型\",\"舒适型\"]")
    private String tags;

    /**
     * 增值服务模板ID
     */
    @Schema(description = "增值服务模板ID", example = "1")
    private Long vasTemplateId;

    /**
     * 优享保障模板ID
     */
    @Schema(description = "优享保障模板ID", example = "2")
    private Long vasTemplateIdVip;

    /**
     * 尊享保障模板ID
     */
    @Schema(description = "尊享保障模板ID", example = "3")
    private Long vasTemplateIdVvip;

    /**
     * 取消规则模板ID
     */
    @Schema(description = "取消规则模板ID", example = "1")
    private Long cancellationTemplateId;

    /**
     * 服务政策模板ID
     */
    @Schema(description = "服务政策模板ID", example = "1")
    private Long servicePolicyTemplateId;

    /**
     * 上架状态:0-下架,1-上架
     */
    @Schema(description = "上架状态:0-下架,1-上架", example = "1")
    private Integer onlineStatus;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间", example = "2024-01-01 12:00:00")
    private LocalDateTime updatedTime;
}
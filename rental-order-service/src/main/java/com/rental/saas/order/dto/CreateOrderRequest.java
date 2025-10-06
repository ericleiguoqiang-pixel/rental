package com.rental.saas.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建订单请求DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "创建订单请求DTO")
public class CreateOrderRequest {

    @Schema(description = "报价ID")
    private String quoteId;

    @Schema(description = "驾驶人姓名")
    private String driverName;

    @Schema(description = "驾驶人身份证号")
    private String driverIdCard;

    @Schema(description = "驾驶人手机号")
    private String driverPhone;

    @Schema(description = "租车开始时间")
    private LocalDateTime startTime;

    @Schema(description = "租车结束时间")
    private LocalDateTime endTime;

    @Schema(description = "下单位置")
    private String orderLocation;

    @Schema(description = "选中的增值服务ID列表")
    private List<Long> selectedVasIds;

    @Schema(description = "总金额")
    private Integer totalAmount;

    @Schema(description = "车损押金")
    private Integer damageDeposit;

    @Schema(description = "违章押金")
    private Integer violationDeposit;
    
    @Schema(description = "用户ID")
    private Long userId;
}
package com.rental.saas.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.CsideBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 订单实体
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("rental_order")
@Schema(description = "订单")
public class Order extends CsideBaseEntity {

    /**
     * 订单号
     */
    @TableField("order_no")
    @Schema(description = "订单号", example = "R202312010001")
    private String orderNo;

    /**
     * 订单状态
     */
    @TableField("order_status")
    @Schema(description = "订单状态:1-待支付,2-待取车,3-已取车,4-已完成,5-已取消", example = "1")
    private Integer orderStatus;

    /**
     * 下单时间
     */
    @TableField("create_time")
    @Schema(description = "下单时间")
    private LocalDateTime createTime;

    /**
     * 取消时间
     */
    @TableField("cancel_time")
    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    /**
     * 驾驶人姓名
     */
    @TableField("driver_name")
    @Schema(description = "驾驶人姓名", example = "张三")
    private String driverName;

    /**
     * 驾驶人身份证号(加密)
     */
    @TableField("driver_id_card")
    @Schema(description = "驾驶人身份证号(加密)")
    private String driverIdCard;

    /**
     * 驾驶人手机号(加密)
     */
    @TableField("driver_phone")
    @Schema(description = "驾驶人手机号(加密)")
    private String driverPhone;

    /**
     * 租车开始时间
     */
    @TableField("start_time")
    @Schema(description = "租车开始时间")
    private LocalDateTime startTime;

    /**
     * 租车结束时间
     */
    @TableField("end_time")
    @Schema(description = "租车结束时间")
    private LocalDateTime endTime;

    /**
     * 实际取车时间
     */
    @TableField("actual_pickup_time")
    @Schema(description = "实际取车时间")
    private LocalDateTime actualPickupTime;

    /**
     * 实际还车时间
     */
    @TableField("actual_return_time")
    @Schema(description = "实际还车时间")
    private LocalDateTime actualReturnTime;

    /**
     * 下单位置
     */
    @TableField("order_location")
    @Schema(description = "下单位置")
    private String orderLocation;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 车型ID
     */
    @TableField("car_model_id")
    @Schema(description = "车型ID", example = "1")
    private Long carModelId;

    /**
     * 车型商品ID
     */
    @TableField("product_id")
    @Schema(description = "车型商品ID", example = "1")
    private Long productId;

    /**
     * 车牌号
     */
    @TableField("license_plate")
    @Schema(description = "车牌号", example = "沪A12345")
    private String licensePlate;

    /**
     * 取车方式:1-门店自取,2-送车上门
     */
    @TableField("pickup_type")
    @Schema(description = "取车方式:1-门店自取,2-送车上门", example = "1")
    private Integer pickupType;

    /**
     * 还车方式:1-门店归还,2-上门取车
     */
    @TableField("return_type")
    @Schema(description = "还车方式:1-门店归还,2-上门取车", example = "1")
    private Integer returnType;

    /**
     * 取车门店ID
     */
    @TableField("pickup_store_id")
    @Schema(description = "取车门店ID", example = "1")
    private Long pickupStoreId;

    /**
     * 还车门店ID
     */
    @TableField("return_store_id")
    @Schema(description = "还车门店ID", example = "1")
    private Long returnStoreId;

    /**
     * 取车司机
     */
    @TableField("pickup_driver")
    @Schema(description = "取车司机", example = "李司机")
    private String pickupDriver;

    /**
     * 还车司机
     */
    @TableField("return_driver")
    @Schema(description = "还车司机", example = "王司机")
    private String returnDriver;

    /**
     * 订单金额(分)
     */
    @TableField("order_amount")
    @Schema(description = "订单金额(分)", example = "300000")
    private Integer orderAmount;

    /**
     * 基础租车费(分)
     */
    @TableField("basic_rental_fee")
    @Schema(description = "基础租车费(分)", example = "30000")
    private Integer basicRentalFee;

    /**
     * 服务费(分)
     */
    @TableField("service_fee")
    @Schema(description = "服务费(分)", example = "5000")
    private Integer serviceFee;

    /**
     * 保障费(分)
     */
    @TableField("insurance_fee")
    @Schema(description = "保障费(分)", example = "3000")
    private Integer insuranceFee;

    /**
     * 车损押金(分)
     */
    @TableField("damage_deposit")
    @Schema(description = "车损押金(分)", example = "200000")
    private Integer damageDeposit;

    /**
     * 违章押金(分)
     */
    @TableField("violation_deposit")
    @Schema(description = "违章押金(分)", example = "100000")
    private Integer violationDeposit;

    /**
     * 实际押金(分)
     */
    @TableField("actual_deposit")
    @Schema(description = "实际押金(分)", example = "300000")
    private Integer actualDeposit;

    /**
     * 实际押金(分)
     */
    @TableField("is_deposit_paid")
    @Schema(description = "实际押金(分)", example = "300000")
    private Integer isDepositPaid;

    /**
     * 服务保障快照(JSON)
     */
    @TableField("vas_snapshot")
    @Schema(description = "服务保障快照(JSON)")
    private String vasSnapshot;

    /**
     * 取消规则快照(JSON)
     */
    @TableField("cancellation_rule_snapshot")
    @Schema(description = "取消规则快照(JSON)")
    private String cancellationRuleSnapshot;

    /**
     * 服务政策快照(JSON)
     */
    @TableField("service_policy_snapshot")
    @Schema(description = "服务政策快照(JSON)")
    private String servicePolicySnapshot;

}
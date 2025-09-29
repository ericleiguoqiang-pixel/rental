package com.rental.saas.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 * 定义了系统中所有可能的响应状态码
 * 
 * @author Rental SaaS Team
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {

    // ========== 通用状态码 ==========
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // ========== 业务状态码 10xxx ==========
    // 参数校验相关 100xx
    VALIDATION_ERROR(10001, "参数校验失败"),
    REQUIRED_PARAMETER_MISSING(10002, "必填参数缺失"),
    PARAMETER_FORMAT_ERROR(10003, "参数格式错误"),
    PARAMETER_VALUE_INVALID(10004, "参数值无效"),

    // 认证授权相关 101xx
    TOKEN_INVALID(10101, "令牌无效"),
    TOKEN_EXPIRED(10102, "令牌已过期"),
    REFRESH_TOKEN_INVALID(10103, "刷新令牌无效"),
    LOGIN_FAILED(10104, "登录失败"),
    PASSWORD_ERROR(10105, "密码错误"),
    ACCOUNT_DISABLED(10106, "账号已禁用"),
    ACCOUNT_LOCKED(10107, "账号已锁定"),
    PERMISSION_DENIED(10108, "权限不足"),
    CAPTCHA_ERROR(10109, "验证码错误"),

    // 用户相关 102xx
    USER_NOT_FOUND(10201, "用户不存在"),
    USER_ALREADY_EXISTS(10202, "用户已存在"),
    USERNAME_DUPLICATE(10203, "用户名重复"),
    PHONE_DUPLICATE(10204, "手机号重复"),
    EMPLOYEE_NOT_FOUND(10205, "员工不存在"),

    // 商户相关 103xx
    TENANT_NOT_FOUND(10301, "商户不存在"),
    TENANT_DISABLED(10302, "商户已禁用"),
    TENANT_EXPIRED(10303, "商户已过期"),
    APPLICATION_NOT_FOUND(10304, "申请记录不存在"),
    APPLICATION_ALREADY_PROCESSED(10305, "申请已处理"),
    UNIFIED_SOCIAL_CREDIT_CODE_DUPLICATE(10306, "统一社会信用代码重复"),

    // 门店相关 104xx
    STORE_NOT_FOUND(10401, "门店不存在"),
    STORE_DISABLED(10402, "门店已禁用"),
    STORE_NOT_AUDITED(10403, "门店未通过审核"),
    STORE_NAME_DUPLICATE(10404, "门店名称重复"),

    // 车型车辆相关 105xx
    CAR_MODEL_NOT_FOUND(10501, "车型不存在"),
    VEHICLE_NOT_FOUND(10502, "车辆不存在"),
    VEHICLE_NOT_AVAILABLE(10503, "车辆不可用"),
    LICENSE_PLATE_DUPLICATE(10504, "车牌号重复"),
    VIN_DUPLICATE(10505, "车架号重复"),
    VEHICLE_ALREADY_RENTED(10506, "车辆已被租出"),

    // 商品相关 106xx
    PRODUCT_NOT_FOUND(10601, "商品不存在"),
    PRODUCT_OFFLINE(10602, "商品已下架"),
    PRODUCT_OUT_OF_STOCK(10603, "商品库存不足"),
    PRICING_NOT_FOUND(10604, "价格配置不存在"),

    // 订单相关 107xx
    ORDER_NOT_FOUND(10701, "订单不存在"),
    ORDER_STATUS_ERROR(10702, "订单状态错误"),
    ORDER_CANNOT_CANCEL(10703, "订单无法取消"),
    ORDER_ALREADY_PAID(10704, "订单已支付"),
    ORDER_EXPIRED(10705, "订单已过期"),
    PICKUP_TIME_INVALID(10706, "取车时间无效"),
    RETURN_TIME_INVALID(10707, "还车时间无效"),

    // 支付相关 108xx
    PAYMENT_NOT_FOUND(10801, "支付记录不存在"),
    PAYMENT_FAILED(10802, "支付失败"),
    PAYMENT_AMOUNT_ERROR(10803, "支付金额错误"),
    REFUND_FAILED(10804, "退款失败"),
    PAYMENT_METHOD_NOT_SUPPORTED(10805, "不支持的支付方式"),

    // 文件相关 109xx
    FILE_UPLOAD_FAILED(10901, "文件上传失败"),
    FILE_NOT_FOUND(10902, "文件不存在"),
    FILE_TYPE_NOT_SUPPORTED(10903, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(10904, "文件大小超出限制"),

    // 限流相关 110xx
    RATE_LIMIT_EXCEEDED(11001, "请求过于频繁"),
    CONCURRENT_LIMIT_EXCEEDED(11002, "并发限制超出"),

    // 外部服务相关 111xx
    THIRD_PARTY_SERVICE_ERROR(11101, "第三方服务错误"),
    SMS_SEND_FAILED(11102, "短信发送失败"),
    OSS_UPLOAD_FAILED(11103, "文件上传失败"),

    // 数据相关 112xx
    DATA_NOT_FOUND(11201, "数据不存在"),
    DATA_ALREADY_EXISTS(11202, "数据已存在"),
    DATA_INTEGRITY_VIOLATION(11203, "数据完整性违反"),
    OPTIMISTIC_LOCK_FAILURE(11204, "乐观锁更新失败");

    /**
     * 响应码
     */
    private final Integer code;

    /**
     * 响应消息
     */
    private final String message;

    /**
     * 根据状态码查找枚举
     */
    public static ResponseCode getByCode(Integer code) {
        for (ResponseCode responseCode : values()) {
            if (responseCode.getCode().equals(code)) {
                return responseCode;
            }
        }
        return null;
    }
}
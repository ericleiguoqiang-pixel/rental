package com.rental.saas.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应包装类
 * 用于统一API响应格式
 * 
 * @author Rental SaaS Team
 * @param <T> 响应数据类型
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "统一响应包装类")
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码
     */
    @Schema(description = "响应状态码", example = "200")
    private Integer code;

    /**
     * 响应消息
     */
    @Schema(description = "响应消息", example = "操作成功")
    private String message;

    /**
     * 响应数据
     */
    @Schema(description = "响应数据")
    private T data;

    /**
     * 请求追踪ID
     */
    @Schema(description = "请求追踪ID")
    private String traceId;

    /**
     * 时间戳
     */
    @Schema(description = "响应时间戳")
    private Long timestamp;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public ApiResponse(Integer code, String message, T data) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error() {
        return new ApiResponse<>(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), ResponseCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    /**
     * 失败响应（自定义消息）
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), message);
    }

    /**
     * 失败响应（自定义状态码和消息）
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message);
    }

    /**
     * 失败响应（使用响应码枚举）
     */
    public static <T> ApiResponse<T> error(ResponseCode responseCode) {
        return new ApiResponse<>(responseCode.getCode(), responseCode.getMessage());
    }

    /**
     * 失败响应（使用响应码枚举和自定义消息）
     */
    public static <T> ApiResponse<T> error(ResponseCode responseCode, String message) {
        return new ApiResponse<>(responseCode.getCode(), message);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return ResponseCode.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 设置追踪ID
     */
    public ApiResponse<T> traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
}
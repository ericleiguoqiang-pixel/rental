package com.rental.saas.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 租户实体
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tenant")
@Schema(description = "租户")
public class Tenant extends BaseEntity {

    /**
     * 租户编码
     */
    @TableField("tenant_code")
    @NotBlank(message = "租户编码不能为空")
    @Size(max = 32, message = "租户编码长度不能超过32个字符")
    @Schema(description = "租户编码", example = "T202401010001")
    private String tenantCode;

    /**
     * 企业名称
     */
    @TableField("company_name")
    @NotBlank(message = "企业名称不能为空")
    @Size(max = 100, message = "企业名称长度不能超过100个字符")
    @Schema(description = "企业名称", example = "某某汽车租赁有限公司")
    private String companyName;

    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名长度不能超过50个字符")
    @Schema(description = "联系人姓名", example = "张三")
    private String contactName;

    /**
     * 联系人手机号(加密)
     */
    @TableField("contact_phone")
    @NotBlank(message = "联系人手机号不能为空")
    @Schema(description = "联系人手机号", example = "13800138000")
    private String contactPhone;

    /**
     * 状态：0-禁用，1-启用
     */
    @TableField("status")
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态", example = "1")
    private Integer status;

    /**
     * 到期时间
     */
    @TableField("expire_time")
    @Schema(description = "到期时间", example = "2025-01-01 00:00:00")
    private java.time.LocalDateTime expireTime;

    /**
     * 套餐类型：1-基础版，2-标准版，3-企业版
     */
    @TableField("package_type")
    @NotNull(message = "套餐类型不能为空")
    @Schema(description = "套餐类型", example = "1")
    private Integer packageType;
}
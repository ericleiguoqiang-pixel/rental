package com.rental.saas.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 商户入驻申请实体
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant_application")
@Schema(description = "商户入驻申请")
public class MerchantApplication extends BaseEntity {

    /**
     * 申请编号
     */
    @TableField("application_no")
    @Schema(description = "申请编号", example = "APP202401010001")
    private String applicationNo;

    /**
     * 企业名称
     */
    @TableField("company_name")
    @NotBlank(message = "企业名称不能为空")
    @Size(max = 100, message = "企业名称长度不能超过100个字符")
    @Schema(description = "企业名称", example = "某某汽车租赁有限公司")
    private String companyName;

    /**
     * 统一社会信用代码
     */
    @TableField("unified_social_credit_code")
    @NotBlank(message = "统一社会信用代码不能为空")
    @Pattern(regexp = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$", message = "统一社会信用代码格式不正确")
    @Schema(description = "统一社会信用代码", example = "91110000000000000A")
    private String unifiedSocialCreditCode;

    /**
     * 法人姓名
     */
    @TableField("legal_person_name")
    @NotBlank(message = "法人姓名不能为空")
    @Size(max = 50, message = "法人姓名长度不能超过50个字符")
    @Schema(description = "法人姓名", example = "张三")
    private String legalPersonName;

    /**
     * 法人身份证号(加密)
     */
    @TableField("legal_person_id_card")
    @NotBlank(message = "法人身份证号不能为空")
    @Schema(description = "法人身份证号", example = "110101199001010001")
    private String legalPersonIdCard;

    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名长度不能超过50个字符")
    @Schema(description = "联系人姓名", example = "李四")
    private String contactName;

    /**
     * 联系人手机号(加密)
     */
    @TableField("contact_phone")
    @NotBlank(message = "联系人手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "联系人手机号", example = "13800138000")
    private String contactPhone;

    /**
     * 企业地址
     */
    @TableField("company_address")
    @NotBlank(message = "企业地址不能为空")
    @Size(max = 200, message = "企业地址长度不能超过200个字符")
    @Schema(description = "企业地址", example = "北京市朝阳区某某街道某某号")
    private String companyAddress;

    /**
     * 营业执照URL
     */
    @TableField("business_license_url")
    @Schema(description = "营业执照URL", example = "https://oss.example.com/license.jpg")
    private String businessLicenseUrl;

    /**
     * 道路运输经营许可证URL
     */
    @TableField("transport_license_url")
    @Schema(description = "道路运输经营许可证URL", example = "https://oss.example.com/transport.jpg")
    private String transportLicenseUrl;

    /**
     * 申请状态：0-待审核，1-审核通过，2-审核拒绝
     */
    @TableField("application_status")
    @NotNull(message = "申请状态不能为空")
    @Schema(description = "申请状态", example = "0")
    private Integer applicationStatus;

    /**
     * 审核备注
     */
    @TableField("audit_remark")
    @Schema(description = "审核备注", example = "材料齐全，准予通过")
    private String auditRemark;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    @Schema(description = "审核时间", example = "2024-01-01 12:00:00")
    private java.time.LocalDateTime auditTime;

    /**
     * 审核人ID
     */
    @TableField("auditor_id")
    @Schema(description = "审核人ID", example = "1")
    private Long auditorId;

    /**
     * 租户ID(审核通过后生成)
     */
    @TableField("tenant_id")
    @Schema(description = "租户ID", example = "1")
    private Long tenantId;
}
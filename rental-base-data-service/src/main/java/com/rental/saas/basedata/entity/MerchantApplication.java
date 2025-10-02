package com.rental.saas.basedata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户入驻申请实体（用于运营审核）
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
    @Schema(description = "企业名称", example = "某某汽车租赁有限公司")
    private String companyName;

    /**
     * 统一社会信用代码
     */
    @TableField("unified_social_credit_code")
    @Schema(description = "统一社会信用代码", example = "91110000000000000A")
    private String unifiedSocialCreditCode;

    /**
     * 法人姓名
     */
    @TableField("legal_person_name")
    @Schema(description = "法人姓名", example = "张三")
    private String legalPersonName;

    /**
     * 法人身份证号(加密)
     */
    @TableField("legal_person_id_card")
    @Schema(description = "法人身份证号", example = "110101199001010001")
    private String legalPersonIdCard;

    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    @Schema(description = "联系人姓名", example = "李四")
    private String contactName;

    /**
     * 联系人手机号(加密)
     */
    @TableField("contact_phone")
    @Schema(description = "联系人手机号", example = "13800138000")
    private String contactPhone;

    /**
     * 企业地址
     */
    @TableField("company_address")
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
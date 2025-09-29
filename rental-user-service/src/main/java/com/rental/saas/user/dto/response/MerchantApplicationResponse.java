package com.rental.saas.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rental.saas.common.annotation.Desensitize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商户入驻申请响应DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "商户入驻申请响应")
public class MerchantApplicationResponse {

    /**
     * 申请ID
     */
    @Schema(description = "申请ID", example = "1")
    private Long id;

    /**
     * 申请编号
     */
    @Schema(description = "申请编号", example = "APP202401010001")
    private String applicationNo;

    /**
     * 企业名称
     */
    @Schema(description = "企业名称", example = "某某汽车租赁有限公司")
    private String companyName;

    /**
     * 统一社会信用代码
     */
    @Schema(description = "统一社会信用代码", example = "91110000000000000A")
    private String unifiedSocialCreditCode;

    /**
     * 法人姓名
     */
    @Desensitize(type = Desensitize.DesensitizeType.NAME)
    @Schema(description = "法人姓名", example = "张*")
    private String legalPersonName;

    /**
     * 法人身份证号(脱敏)
     */
    @Desensitize(type = Desensitize.DesensitizeType.ID_CARD)
    @Schema(description = "法人身份证号", example = "1101**********0001")
    private String legalPersonIdCard;

    /**
     * 联系人姓名
     */
    @Desensitize(type = Desensitize.DesensitizeType.NAME)
    @Schema(description = "联系人姓名", example = "李*")
    private String contactName;

    /**
     * 联系人手机号(脱敏)
     */
    @Desensitize(type = Desensitize.DesensitizeType.PHONE)
    @Schema(description = "联系人手机号", example = "138****8000")
    private String contactPhone;

    /**
     * 企业地址
     */
    @Schema(description = "企业地址", example = "北京市朝阳区某某街道某某号")
    private String companyAddress;

    /**
     * 营业执照URL
     */
    @Schema(description = "营业执照URL", example = "https://oss.example.com/license.jpg")
    private String businessLicenseUrl;

    /**
     * 道路运输经营许可证URL
     */
    @Schema(description = "道路运输经营许可证URL", example = "https://oss.example.com/transport.jpg")
    private String transportLicenseUrl;

    /**
     * 申请状态：0-待审核，1-审核通过，2-审核拒绝
     */
    @Schema(description = "申请状态", example = "0")
    private Integer applicationStatus;

    /**
     * 申请状态描述
     */
    @Schema(description = "申请状态描述", example = "待审核")
    private String applicationStatusDesc;

    /**
     * 审核备注
     */
    @Schema(description = "审核备注", example = "材料齐全，准予通过")
    private String auditRemark;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "审核时间", example = "2024-01-01 12:00:00")
    private LocalDateTime auditTime;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID", example = "1")
    private Long tenantId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    private LocalDateTime createdTime;
}
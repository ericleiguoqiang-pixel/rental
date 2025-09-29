package com.rental.saas.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 商户入驻申请请求DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "商户入驻申请请求")
public class MerchantApplicationRequest {

    /**
     * 企业名称
     */
    @NotBlank(message = "企业名称不能为空")
    @Size(max = 100, message = "企业名称长度不能超过100个字符")
    @Schema(description = "企业名称", example = "某某汽车租赁有限公司", required = true)
    private String companyName;

    /**
     * 统一社会信用代码
     */
    @NotBlank(message = "统一社会信用代码不能为空")
    @Pattern(regexp = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$", message = "统一社会信用代码格式不正确")
    @Schema(description = "统一社会信用代码", example = "91110000000000000A", required = true)
    private String unifiedSocialCreditCode;

    /**
     * 法人姓名
     */
    @NotBlank(message = "法人姓名不能为空")
    @Size(max = 50, message = "法人姓名长度不能超过50个字符")
    @Schema(description = "法人姓名", example = "张三", required = true)
    private String legalPersonName;

    /**
     * 法人身份证号
     */
    @NotBlank(message = "法人身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "身份证号格式不正确")
    @Schema(description = "法人身份证号", example = "110101199001010001", required = true)
    private String legalPersonIdCard;

    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名长度不能超过50个字符")
    @Schema(description = "联系人姓名", example = "李四", required = true)
    private String contactName;

    /**
     * 联系人手机号
     */
    @NotBlank(message = "联系人手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "联系人手机号", example = "13800138000", required = true)
    private String contactPhone;

    /**
     * 企业地址
     */
    @NotBlank(message = "企业地址不能为空")
    @Size(max = 200, message = "企业地址长度不能超过200个字符")
    @Schema(description = "企业地址", example = "北京市朝阳区某某街道某某号", required = true)
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
}
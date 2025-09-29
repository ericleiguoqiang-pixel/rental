package com.rental.saas.user.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.saas.common.enums.ApplicationStatus;
import com.rental.saas.common.exception.BusinessException;
import com.rental.saas.common.response.PageResponse;
import com.rental.saas.common.response.ResponseCode;
import com.rental.saas.common.utils.CryptoUtil;
import com.rental.saas.user.dto.request.MerchantApplicationRequest;
import com.rental.saas.user.dto.response.MerchantApplicationResponse;
import com.rental.saas.user.entity.MerchantApplication;
import com.rental.saas.user.entity.Tenant;
import com.rental.saas.user.mapper.MerchantApplicationMapper;
import com.rental.saas.user.service.MerchantApplicationService;
import com.rental.saas.user.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户入驻申请服务实现
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantApplicationServiceImpl extends ServiceImpl<MerchantApplicationMapper, MerchantApplication>
        implements MerchantApplicationService {

    private final TenantService tenantService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitApplication(MerchantApplicationRequest request) {
        log.info("提交商户入驻申请: {}", request.getCompanyName());

        // 检查统一社会信用代码是否已存在
        LambdaQueryWrapper<MerchantApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantApplication::getUnifiedSocialCreditCode, request.getUnifiedSocialCreditCode());
        MerchantApplication existingApplication = getOne(queryWrapper);
        if (existingApplication != null) {
            throw new BusinessException(ResponseCode.UNIFIED_SOCIAL_CREDIT_CODE_DUPLICATE);
        }

        // 生成申请编号
        String applicationNo = generateApplicationNo();

        // 创建申请记录
        MerchantApplication application = new MerchantApplication();
        BeanUtils.copyProperties(request, application);
        application.setApplicationNo(applicationNo);
        application.setApplicationStatus(ApplicationStatus.PENDING.getCode());

        // 加密敏感信息
        application.setLegalPersonIdCard(CryptoUtil.aesEncrypt(request.getLegalPersonIdCard()));
        application.setContactPhone(CryptoUtil.aesEncrypt(request.getContactPhone()));

        save(application);

        log.info("商户入驻申请提交成功，申请编号: {}", applicationNo);
        return applicationNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditApplication(Long id, Integer status, String remark, Long auditorId) {
        log.info("审核商户入驻申请: id={}, status={}", id, status);

        MerchantApplication application = getById(id);
        if (application == null) {
            throw new BusinessException(ResponseCode.APPLICATION_NOT_FOUND);
        }

        if (!ApplicationStatus.PENDING.getCode().equals(application.getApplicationStatus())) {
            throw new BusinessException(ResponseCode.APPLICATION_ALREADY_PROCESSED);
        }

        // 更新申请状态
        application.setApplicationStatus(status);
        application.setAuditRemark(remark);
        application.setAuditTime(LocalDateTime.now());
        application.setAuditorId(auditorId);

        // 如果审核通过，创建租户
        if (ApplicationStatus.APPROVED.getCode().equals(status)) {
            Long tenantId = createTenant(application);
            application.setTenantId(tenantId);
        }

        updateById(application);
        log.info("商户入驻申请审核完成: id={}, status={}", id, status);
    }

    @Override
    public PageResponse<MerchantApplicationResponse> pageApplications(Integer pageNum, Integer pageSize,
                                                                      Integer status, String keyword) {
        Page<MerchantApplication> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MerchantApplication> queryWrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            queryWrapper.eq(MerchantApplication::getApplicationStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(MerchantApplication::getCompanyName, keyword)
                    .or()
                    .like(MerchantApplication::getApplicationNo, keyword)
                    .or()
                    .like(MerchantApplication::getContactName, keyword)
            );
        }

        queryWrapper.orderByDesc(MerchantApplication::getCreatedTime);
        IPage<MerchantApplication> pageResult = page(page, queryWrapper);

        List<MerchantApplicationResponse> responseList = pageResult.getRecords()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return PageResponse.of(pageNum, pageSize, pageResult.getTotal(), responseList);
    }

    @Override
    public MerchantApplicationResponse getApplicationById(Long id) {
        MerchantApplication application = getById(id);
        if (application == null) {
            throw new BusinessException(ResponseCode.APPLICATION_NOT_FOUND);
        }
        return convertToResponse(application);
    }

    @Override
    public MerchantApplicationResponse getApplicationByNo(String applicationNo) {
        LambdaQueryWrapper<MerchantApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantApplication::getApplicationNo, applicationNo);
        MerchantApplication application = getOne(queryWrapper);
        if (application == null) {
            throw new BusinessException(ResponseCode.APPLICATION_NOT_FOUND);
        }
        return convertToResponse(application);
    }

    /**
     * 生成申请编号
     */
    private String generateApplicationNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.valueOf((int)(Math.random() * 9000) + 1000);
        return "APP" + dateStr + randomStr;
    }

    /**
     * 创建租户
     */
    private Long createTenant(MerchantApplication application) {
        log.info("为申请创建租户: {}", application.getCompanyName());

        Tenant tenant = new Tenant();
        tenant.setTenantCode(generateTenantCode());
        tenant.setCompanyName(application.getCompanyName());
        tenant.setContactName(application.getContactName());
        tenant.setContactPhone(application.getContactPhone());
        tenant.setStatus(1); // 启用
        tenant.setPackageType(1); // 基础版
        tenant.setExpireTime(LocalDateTime.now().plusYears(1)); // 一年后到期

        tenantService.save(tenant);
        log.info("租户创建成功: tenantId={}, tenantCode={}", tenant.getId(), tenant.getTenantCode());

        return tenant.getId();
    }

    /**
     * 生成租户编码
     */
    private String generateTenantCode() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.valueOf((int)(Math.random() * 9000) + 1000);
        return "T" + dateStr + randomStr;
    }

    /**
     * 转换为响应DTO
     */
    private MerchantApplicationResponse convertToResponse(MerchantApplication application) {
        MerchantApplicationResponse response = new MerchantApplicationResponse();
        BeanUtils.copyProperties(application, response);

        // 解密敏感信息用于脱敏显示
        if (StringUtils.hasText(application.getLegalPersonIdCard())) {
            String decryptedIdCard = CryptoUtil.aesDecrypt(application.getLegalPersonIdCard());
            response.setLegalPersonIdCard(CryptoUtil.maskIdCard(decryptedIdCard));
        }
        if (StringUtils.hasText(application.getContactPhone())) {
            String decryptedPhone = CryptoUtil.aesDecrypt(application.getContactPhone());
            response.setContactPhone(CryptoUtil.maskPhone(decryptedPhone));
        }

        // 设置状态描述
        ApplicationStatus status = ApplicationStatus.getByCode(application.getApplicationStatus());
        if (status != null) {
            response.setApplicationStatusDesc(status.getDescription());
        }

        return response;
    }
}
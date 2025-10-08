package com.rental.saas.user.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.rental.saas.user.service.EmployeeService;
import com.rental.saas.user.dto.request.EmployeeRequest;
import com.rental.saas.user.entity.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final EmployeeService employeeService;
    private final MerchantApplicationMapper merchantApplicationMapper;

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
    public boolean auditApplication(Long id, String status, String remark, Long auditorId) {
        log.info("审核商户入驻申请: id={}, status={}", id, status);

        MerchantApplication application = getById(id);
        if (application == null) {
            throw new BusinessException(ResponseCode.APPLICATION_NOT_FOUND);
        }

        if (!ApplicationStatus.PENDING.getCode().equals(application.getApplicationStatus())) {
            throw new BusinessException(ResponseCode.APPLICATION_ALREADY_PROCESSED);
        }

        // 更新状态
        Integer statusValue;
        switch (status) {
            case "APPROVED":
                statusValue = ApplicationStatus.APPROVED.getCode(); // 审核通过
                break;
            case "REJECTED":
                statusValue = ApplicationStatus.REJECTED.getCode(); // 审核拒绝
                break;
            default:
                log.warn("无效的审核状态: {}", status);
                return false;
        }

        // 更新申请状态
        application.setApplicationStatus(statusValue);
        application.setAuditRemark(remark);
        application.setAuditTime(LocalDateTime.now());
        application.setAuditorId(auditorId);


        // 如果审核通过，创建租户和员工
        if (ApplicationStatus.APPROVED.getCode().equals(statusValue)) {
            Long tenantId = createTenant(application);
            application.setTenantId(tenantId);
            
            // 创建员工账号
            createEmployeeForMerchant(application, tenantId);
        }

        updateById(application);
        log.info("商户入驻申请审核完成: id={}, status={}", id, status);
        return true;
    }
    
    @Override
    public int countPendingApplications() {
        LambdaQueryWrapper<MerchantApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantApplication::getApplicationStatus, ApplicationStatus.PENDING.getCode());
        return Math.toIntExact(count(queryWrapper));
    }
    
    @Override
    public Map<String, Integer> countApplicationsByStatus() {
        QueryWrapper<MerchantApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("application_status as status", "COUNT(*) as count");
        queryWrapper.groupBy("application_status");
        
        List<Map<String, Object>> result = listMaps(queryWrapper);
        Map<String, Integer> statusCount = new HashMap<>();
        
        for (Map<String, Object> row : result) {
            Object statusObj = row.get("status");
            Object countObj = row.get("count");
            
            if (statusObj != null && countObj != null) {
                Integer status = ((Number) statusObj).intValue();
                Integer count = ((Number) countObj).intValue();
                statusCount.put(status.toString(), count);
            }
        }
        
        return statusCount;
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
     * 为商户创建员工账号
     */
    private void createEmployeeForMerchant(MerchantApplication application, Long tenantId) {
        log.info("为商户创建员工账号: {}", application.getContactName());
        
        try {
            // 创建员工请求
            EmployeeRequest employeeRequest = new EmployeeRequest();
            employeeRequest.setEmployeeName(application.getContactName());
            employeeRequest.setPhone(CryptoUtil.aesDecrypt(application.getContactPhone()));
            employeeRequest.setUsername(application.getContactName()); // 使用联系人姓名作为用户名
            employeeRequest.setPassword("123456"); // 默认密码
            employeeRequest.setStatus(1); // 启用
            employeeRequest.setRoleType(2); // 门店管理员
            employeeRequest.setStoreId(null); // 初始时没有门店关联

            // 设置租户ID
            // 注意：这里需要通过某种方式设置租户ID，可能需要修改EmployeeRequest或使用上下文
            
            // 创建员工
            employeeService.createEmployee(employeeRequest, tenantId);
            
            log.info("商户员工账号创建成功: contactName={}", application.getContactName());
        } catch (Exception e) {
            log.error("创建商户员工账号失败: contactName={}, error={}", application.getContactName(), e.getMessage(), e);
            // 不抛出异常，避免影响主流程
        }
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


    @Override
    public IPage<MerchantApplication> getPendingApplications(Integer current, Integer size) {
        LambdaQueryWrapper<MerchantApplication> wrapper = new LambdaQueryWrapper<>();
        // 只查询待审核的申请（状态为0）
        wrapper.eq(MerchantApplication::getApplicationStatus, 0);
        wrapper.orderByDesc(MerchantApplication::getId);

        Page<MerchantApplication> page = new Page<>(current, size);
        return merchantApplicationMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<MerchantApplication> getAllApplications(Integer current, Integer size, String status) {
        LambdaQueryWrapper<MerchantApplication> wrapper = new LambdaQueryWrapper<>();
        // 根据状态筛选
        if (status != null && !status.isEmpty()) {
            try {
                Integer statusValue = Integer.valueOf(status);
                wrapper.eq(MerchantApplication::getApplicationStatus, statusValue);
            } catch (NumberFormatException e) {
                log.warn("状态参数格式错误: {}", status);
            }
        }
        wrapper.orderByDesc(MerchantApplication::getId);

        Page<MerchantApplication> page = new Page<>(current, size);
        return merchantApplicationMapper.selectPage(page, wrapper);
    }

}
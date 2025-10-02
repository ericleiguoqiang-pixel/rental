package com.rental.saas.basedata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.saas.basedata.entity.MerchantApplication;
import com.rental.saas.basedata.mapper.MerchantApplicationMapper;
import com.rental.saas.basedata.service.MerchantApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 商户申请服务实现类（用于运营审核）
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantApplicationServiceImpl extends ServiceImpl<MerchantApplicationMapper, MerchantApplication> implements MerchantApplicationService {

    private final MerchantApplicationMapper merchantApplicationMapper;

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

    @Override
    public MerchantApplication getById(Long id) {
        return merchantApplicationMapper.selectById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean auditApplication(Long id, String status, String reason) {
        // 查询商户申请
        MerchantApplication application = merchantApplicationMapper.selectById(id);
        if (application == null) {
            log.warn("商户申请不存在: id={}", id);
            return false;
        }

        // 更新状态
        Integer statusValue;
        switch (status) {
            case "APPROVED":
                statusValue = 1; // 审核通过
                break;
            case "REJECTED":
                statusValue = 2; // 审核拒绝
                break;
            default:
                log.warn("无效的审核状态: {}", status);
                return false;
        }

        // 更新申请状态
        application.setApplicationStatus(statusValue);
        application.setAuditRemark(reason);
        application.setAuditTime(LocalDateTime.now());
        // 这里应该设置审核人ID，但运营管理员没有具体ID，暂时设为0
        application.setAuditorId(0L);

        int result = merchantApplicationMapper.updateById(application);
        return result > 0;
    }
}
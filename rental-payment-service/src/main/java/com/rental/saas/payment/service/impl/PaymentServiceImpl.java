package com.rental.saas.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rental.api.order.OrderClient;
import com.rental.saas.common.enums.PaymentStatus;
import com.rental.saas.common.exception.BusinessException;
import com.rental.saas.payment.dto.PaymentRequest;
import com.rental.saas.payment.dto.PaymentResponse;
import com.rental.saas.payment.entity.PaymentRecord;
import com.rental.saas.payment.mapper.PaymentRecordMapper;
import com.rental.saas.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付服务实现类
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRecordMapper paymentRecordMapper;
    private final OrderClient orderClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("处理支付请求: orderId={}, amount={}, paymentType={}", 
                request.getOrderId(), request.getAmount(), request.getPaymentType());
        
        try {
            // 生成支付单号
            String paymentNo = generatePaymentNo();
            
            // 创建支付记录
            PaymentRecord paymentRecord = new PaymentRecord();
            paymentRecord.setPaymentNo(paymentNo);
            paymentRecord.setOrderId(request.getOrderId());
            paymentRecord.setOrderNo(request.getOrderNo());
            paymentRecord.setPaymentType(request.getPaymentType());
            // 金额从元转换为分
            paymentRecord.setPaymentAmount(request.getAmount().multiply(new BigDecimal(100)).intValue());
            paymentRecord.setPaymentMethod(request.getPaymentMethod());
            paymentRecord.setPaymentStatus(PaymentStatus.PENDING.getCode());
            
            // 保存支付记录
            paymentRecordMapper.insert(paymentRecord);
            
            // 创建支付响应
            PaymentResponse response = new PaymentResponse();
            response.setId(paymentRecord.getId());
            response.setPaymentNo(paymentNo);
            response.setOrderId(request.getOrderId());
            response.setOrderNo(request.getOrderNo());
            response.setPaymentType(request.getPaymentType());
            response.setAmount(request.getAmount());
            response.setPaymentMethod(request.getPaymentMethod());
            response.setPaymentStatus(PaymentStatus.PENDING);
            response.setPaymentTime(LocalDateTime.now());
            
            log.info("支付请求处理成功: paymentNo={}", paymentNo);
            return response;
        } catch (Exception e) {
            log.error("处理支付请求失败: error={}", e.getMessage(), e);
            throw new BusinessException("处理支付请求失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePaymentCallback(String paymentNo) {
        log.info("处理支付回调: paymentNo={}", paymentNo);
        
        try {
            // 根据支付单号查询支付记录
            LambdaQueryWrapper<PaymentRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PaymentRecord::getPaymentNo, paymentNo);
            PaymentRecord paymentRecord = paymentRecordMapper.selectOne(queryWrapper);
            
            if (paymentRecord == null) {
                log.warn("支付记录不存在: paymentNo={}", paymentNo);
                return false;
            }
            
            // 更新支付状态为成功
            paymentRecord.setPaymentStatus(PaymentStatus.SUCCESS.getCode());
            paymentRecord.setPaymentTime(LocalDateTime.now());
            paymentRecordMapper.updateById(paymentRecord);
            
            // 调用订单服务更新订单状态
            try {
                orderClient.handlePaymentSuccess(paymentRecord.getOrderId(), paymentRecord.getPaymentType());
                log.info("成功通知订单服务更新订单状态: orderId={}, paymentType={}", 
                        paymentRecord.getOrderId(), paymentRecord.getPaymentType());
            } catch (Exception e) {
                log.error("调用订单服务更新订单状态失败: orderId={}, paymentType={}, error={}", 
                        paymentRecord.getOrderId(), paymentRecord.getPaymentType(), e.getMessage(), e);
                // 这里我们不抛出异常，因为支付本身已经成功，只是通知订单服务失败
            }
            
            log.info("支付回调处理成功: paymentNo={}", paymentNo);
            return true;
        } catch (Exception e) {
            log.error("处理支付回调失败: paymentNo={}, error={}", paymentNo, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 生成支付单号
     * 格式: P + yyyyMMddHHmmss + 6位随机数
     */
    private String generatePaymentNo() {
        String dateStr = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomStr = String.format("%06d", (int) (Math.random() * 1000000));
        return "P" + dateStr + randomStr;
    }
}
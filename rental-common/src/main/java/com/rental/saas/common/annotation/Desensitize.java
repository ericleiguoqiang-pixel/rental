package com.rental.saas.common.annotation;

import java.lang.annotation.*;

/**
 * 数据脱敏注解
 * 用于标记需要脱敏的字段
 * 
 * @author Rental SaaS Team
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Desensitize {

    /**
     * 脱敏类型
     */
    DesensitizeType type();

    /**
     * 脱敏类型枚举
     */
    enum DesensitizeType {
        /**
         * 手机号脱敏
         */
        PHONE,
        /**
         * 身份证号脱敏
         */
        ID_CARD,
        /**
         * 银行卡号脱敏
         */
        BANK_CARD,
        /**
         * 姓名脱敏
         */
        NAME,
        /**
         * 地址脱敏
         */
        ADDRESS,
        /**
         * 邮箱脱敏
         */
        EMAIL
    }
}
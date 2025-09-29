package com.rental.saas.common.constant;

/**
 * 通用常量
 * 
 * @author Rental SaaS Team
 */
public class CommonConstant {

    /**
     * 成功标识
     */
    public static final String SUCCESS = "SUCCESS";

    /**
     * 失败标识
     */
    public static final String FAIL = "FAIL";

    /**
     * 是否删除标识
     */
    public static final Integer DELETED_YES = 1;
    public static final Integer DELETED_NO = 0;

    /**
     * 启用禁用状态
     */
    public static final Integer STATUS_ENABLED = 1;
    public static final Integer STATUS_DISABLED = 0;

    /**
     * 上下架状态
     */
    public static final Integer ONLINE_STATUS_UP = 1;
    public static final Integer ONLINE_STATUS_DOWN = 0;

    /**
     * 默认页码和页大小
     */
    public static final Integer DEFAULT_PAGE_NUM = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final Integer MAX_PAGE_SIZE = 100;

    /**
     * 日期时间格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * 请求头
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_TENANT_ID = "Tenant-Id";
    public static final String HEADER_TRACE_ID = "Trace-Id";
    public static final String HEADER_USER_AGENT = "User-Agent";

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 缓存键前缀
     */
    public static final String CACHE_PREFIX = "rental:saas:";
    public static final String CACHE_TOKEN_PREFIX = CACHE_PREFIX + "token:";
    public static final String CACHE_USER_PREFIX = CACHE_PREFIX + "user:";
    public static final String CACHE_TENANT_PREFIX = CACHE_PREFIX + "tenant:";

    /**
     * 缓存过期时间（秒）
     */
    public static final long CACHE_EXPIRE_HOUR = 3600L;
    public static final long CACHE_EXPIRE_DAY = 86400L;
    public static final long CACHE_EXPIRE_WEEK = 604800L;

    /**
     * 文件相关
     */
    public static final String[] ALLOWED_IMAGE_TYPES = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
    public static final String[] ALLOWED_DOCUMENT_TYPES = {".pdf", ".doc", ".docx", ".xls", ".xlsx"};
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024L; // 10MB

    /**
     * 正则表达式
     */
    public static final String REGEX_PHONE = "^1[3-9]\\d{9}$";
    public static final String REGEX_ID_CARD = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
    public static final String REGEX_LICENSE_PLATE = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳]$";
    public static final String REGEX_VIN = "^[A-HJ-NPR-Z\\d]{17}$";

    /**
     * 系统配置
     */
    public static final String SYSTEM_USER = "system";
    public static final Long SYSTEM_TENANT_ID = 0L;

    /**
     * 消息队列主题
     */
    public static final String MQ_TOPIC_ORDER = "rental_order";
    public static final String MQ_TOPIC_PAYMENT = "rental_payment";
    public static final String MQ_TOPIC_NOTIFICATION = "rental_notification";

    /**
     * 消息队列标签
     */
    public static final String MQ_TAG_ORDER_CREATED = "order_created";
    public static final String MQ_TAG_ORDER_PAID = "order_paid";
    public static final String MQ_TAG_ORDER_CANCELLED = "order_cancelled";
    public static final String MQ_TAG_PAYMENT_SUCCESS = "payment_success";
    public static final String MQ_TAG_PAYMENT_FAILED = "payment_failed";

    private CommonConstant() {
        // 防止实例化
    }
}
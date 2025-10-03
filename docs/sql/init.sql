-- ===================================================================
-- 租车SaaS系统数据库初始化脚本
-- 作者: Rental SaaS Team
-- 创建时间: 2024-01-01
-- 描述: 创建所有业务表结构、索引和基础数据
-- ===================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `rentalsaas` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `rental_saas`;

-- ===================================================================
-- 商户相关表
-- ===================================================================

-- 商户入驻申请表
CREATE TABLE `merchant_application` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `application_no` VARCHAR(32) NOT NULL COMMENT '申请编号',
    `company_name` VARCHAR(100) NOT NULL COMMENT '企业名称',
    `unified_social_credit_code` VARCHAR(18) NOT NULL COMMENT '统一社会信用代码',
    `legal_person_name` VARCHAR(50) NOT NULL COMMENT '法人姓名',
    `legal_person_id_card` VARCHAR(255) NOT NULL COMMENT '法人身份证号(加密)',
    `contact_name` VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    `contact_phone` VARCHAR(255) NOT NULL COMMENT '联系人手机号(加密)',
    `company_address` VARCHAR(200) NOT NULL COMMENT '企业地址',
    `business_license_url` VARCHAR(500) COMMENT '营业执照URL',
    `transport_license_url` VARCHAR(500) COMMENT '道路运输经营许可证URL',
    `application_status` TINYINT NOT NULL DEFAULT 0 COMMENT '申请状态:0-待审核,1-审核通过,2-审核拒绝',
    `audit_remark` VARCHAR(500) COMMENT '审核备注',
    `audit_time` DATETIME COMMENT '审核时间',
    `auditor_id` BIGINT COMMENT '审核人ID',
    `tenant_id` BIGINT COMMENT '租户ID(审核通过后生成)',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人',
    `updated_by` BIGINT COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_application_no` (`application_no`),
    UNIQUE KEY `uk_unified_social_credit_code` (`unified_social_credit_code`),
    KEY `idx_application_status` (`application_status`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商户入驻申请表';

-- 租户表(商户)
CREATE TABLE `tenant` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '租户ID',
    `tenant_code` VARCHAR(32) NOT NULL COMMENT '租户编码',
    `company_name` VARCHAR(100) NOT NULL COMMENT '企业名称',
    `contact_name` VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    `contact_phone` VARCHAR(255) NOT NULL COMMENT '联系人手机号(加密)',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-启用',
    `expire_time` DATETIME COMMENT '到期时间',
    `package_type` TINYINT NOT NULL DEFAULT 1 COMMENT '套餐类型:1-基础版,2-标准版,3-企业版',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人',
    `updated_by` BIGINT COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_code` (`tenant_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户表';

-- 门店表
CREATE TABLE `store` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '门店ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `store_name` VARCHAR(100) NOT NULL COMMENT '门店名称',
    `city` VARCHAR(50) NOT NULL COMMENT '所在城市',
    `address` VARCHAR(200) NOT NULL COMMENT '详细地址',
    `longitude` DECIMAL(10,7) COMMENT '经度',
    `latitude` DECIMAL(10,7) COMMENT '纬度',
    `business_start_time` TIME NOT NULL COMMENT '营业开始时间',
    `business_end_time` TIME NOT NULL COMMENT '营业结束时间',
    `audit_status` TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态:0-待审核,1-审核通过,2-审核拒绝',
    `online_status` TINYINT NOT NULL DEFAULT 0 COMMENT '上架状态:0-下架,1-上架',
    `min_advance_hours` INT NOT NULL DEFAULT 2 COMMENT '最小提前预定时间(小时)',
    `max_advance_days` INT NOT NULL DEFAULT 30 COMMENT '最大提前预定天数',
    `service_fee` INT NOT NULL DEFAULT 0 COMMENT '车行手续费(分)',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人',
    `updated_by` BIGINT COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_city` (`city`),
    KEY `idx_audit_status` (`audit_status`),
    KEY `idx_online_status` (`online_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='门店表';

-- 门店联系人表
CREATE TABLE `store_contact` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `store_id` BIGINT NOT NULL COMMENT '门店ID',
    `contact_name` VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    `contact_phone` VARCHAR(255) NOT NULL COMMENT '联系人手机号(加密)',
    `contact_type` TINYINT NOT NULL COMMENT '联系人类型:1-普通联系人,2-紧急联系人',
    `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认:0-否,1-是',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`),
    KEY `idx_store_id` (`store_id`),
    KEY `idx_contact_type` (`contact_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='门店联系人表';

-- 服务范围表
CREATE TABLE `service_area` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `store_id` BIGINT NOT NULL COMMENT '门店ID',
    `area_name` VARCHAR(100) NOT NULL COMMENT '区域名称',
    `area_type` TINYINT NOT NULL COMMENT '区域类型:1-取车区域,2-还车区域',
    `fence_coordinates` TEXT COMMENT '电子围栏坐标(JSON格式)',
    `advance_hours` INT NOT NULL DEFAULT 2 COMMENT '提前预定时间(小时)',
    `service_start_time` TIME NOT NULL COMMENT '服务开始时间',
    `service_end_time` TIME NOT NULL COMMENT '服务结束时间',
    `door_to_door_delivery` TINYINT NOT NULL DEFAULT 0 COMMENT '是否提供送车上门:0-否,1-是',
    `delivery_fee` INT NOT NULL DEFAULT 0 COMMENT '送车上门费用(分)',
    `free_pickup_to_store` TINYINT NOT NULL DEFAULT 0 COMMENT '是否免费接至门店:0-否,1-是',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`),
    KEY `idx_store_id` (`store_id`),
    KEY `idx_area_type` (`area_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务范围表';

-- ===================================================================
-- 用户权限相关表
-- ===================================================================

-- 员工表
CREATE TABLE `employee` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '员工ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `store_id` BIGINT COMMENT '所属门店ID',
    `employee_name` VARCHAR(50) NOT NULL COMMENT '员工姓名',
    `phone` VARCHAR(255) NOT NULL COMMENT '手机号(加密)',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-启用',
    `role_type` TINYINT NOT NULL COMMENT '角色类型:1-超级管理员,2-门店管理员,3-车辆管理员,4-订单管理员,5-普通员工',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) COMMENT '最后登录IP',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人',
    `updated_by` BIGINT COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_username` (`tenant_id`, `username`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_store_id` (`store_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工表';

-- ===================================================================
-- 车型和车辆相关表
-- ===================================================================

-- 车型库表
CREATE TABLE `car_model` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '车型ID',
    `brand` VARCHAR(50) NOT NULL COMMENT '品牌',
    `series` VARCHAR(50) NOT NULL COMMENT '车系',
    `model` VARCHAR(100) NOT NULL COMMENT '车型',
    `year` INT NOT NULL COMMENT '年款',
    `transmission` TINYINT NOT NULL COMMENT '档位:1-自动,2-手动',
    `drive_type` TINYINT NOT NULL COMMENT '驱动类型:1-燃油,2-纯电,3-混动',
    `seat_count` TINYINT NOT NULL COMMENT '座位数',
    `door_count` TINYINT NOT NULL COMMENT '车门数',
    `category` VARCHAR(100) COMMENT '车型分类(多选,逗号分隔)',
    `image_urls` TEXT COMMENT '车型图片URLs(JSON格式)',
    `description` TEXT COMMENT '车型描述',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_brand_series_model_year` (`brand`, `series`, `model`, `year`),
    KEY `idx_brand` (`brand`),
    KEY `idx_drive_type` (`drive_type`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车型库表';

-- 车辆表
CREATE TABLE `vehicle` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `store_id` BIGINT NOT NULL COMMENT '归属门店ID',
    `license_plate` VARCHAR(20) NOT NULL COMMENT '车牌号',
    `car_model_id` BIGINT NOT NULL COMMENT '车型ID',
    `license_type` TINYINT NOT NULL DEFAULT 1 COMMENT '牌照类型:1-普通,2-京牌,3-沪牌,4-深牌,5-粤A牌,6-杭州牌',
    `register_date` DATE NOT NULL COMMENT '注册日期',
    `vin` VARCHAR(17) NOT NULL COMMENT '车架号',
    `engine_no` VARCHAR(50) NOT NULL COMMENT '发动机号',
    `usage_nature` TINYINT NOT NULL COMMENT '使用性质:1-营运,2-非营运',
    `audit_status` TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态:0-待审核,1-审核通过,2-审核拒绝',
    `online_status` TINYINT NOT NULL DEFAULT 0 COMMENT '上架状态:0-下架,1-上架',
    `vehicle_status` TINYINT NOT NULL DEFAULT 1 COMMENT '车辆状态:1-空闲,2-租出,3-维修,4-报废',
    `mileage` INT NOT NULL DEFAULT 0 COMMENT '总里程(公里)',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人',
    `updated_by` BIGINT COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_license_plate` (`license_plate`),
    UNIQUE KEY `uk_vin` (`vin`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_store_id` (`store_id`),
    KEY `idx_car_model_id` (`car_model_id`),
    KEY `idx_audit_status` (`audit_status`),
    KEY `idx_online_status` (`online_status`),
    KEY `idx_vehicle_status` (`vehicle_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆表';

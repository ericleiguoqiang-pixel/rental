-- 创建商品和定价相关表

-- 增值服务模板表
CREATE TABLE `value_added_service_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板ID',
    `template_name` VARCHAR(100) NOT NULL COMMENT '模板名称',
    `service_type` TINYINT NOT NULL COMMENT '服务类型:1-基础保障,2-优享保障,3-尊享保障',
    `price` INT NOT NULL COMMENT '价格(分)',
    `deductible` INT NOT NULL DEFAULT 0 COMMENT '起赔额(元)',
    `include_tire_damage` TINYINT NOT NULL DEFAULT 0 COMMENT '包含轮胎损失:0-否,1-是',
    `include_glass_damage` TINYINT NOT NULL DEFAULT 0 COMMENT '包含玻璃损失:0-否,1-是',
    `third_party_coverage` INT NOT NULL DEFAULT 0 COMMENT '第三方保障(万元)',
    `charge_depreciation` TINYINT NOT NULL DEFAULT 0 COMMENT '收取折旧费:0-否,1-是',
    `depreciation_deductible` INT NOT NULL DEFAULT 0 COMMENT '折旧费免赔额(元)',
    `depreciation_rate` INT NOT NULL DEFAULT 0 COMMENT '折旧费收取比例(%)',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人',
    `updated_by` BIGINT COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`),
    KEY `idx_service_type` (`service_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='增值服务模板表';

-- 取消规则模板表
CREATE TABLE `cancellation_rule_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板ID',
    `template_name` VARCHAR(100) NOT NULL COMMENT '模板名称',
    `weekday_rule` TEXT NOT NULL COMMENT '平日取消规则',
    `holiday_rule` TEXT NOT NULL COMMENT '节假日取消规则',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人',
    `updated_by` BIGINT COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='取消规则模板表';

-- 服务政策模板表
CREATE TABLE `service_policy_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板ID',
    `template_name` VARCHAR(100) NOT NULL COMMENT '模板名称',
    `mileage_limit` TEXT COMMENT '里程限制',
    `early_pickup` TEXT COMMENT '提前取车',
    `late_pickup` TEXT COMMENT '延迟取车',
    `early_return` TEXT COMMENT '提前还车',
    `renewal` TEXT COMMENT '续租',
    `forced_renewal` TEXT COMMENT '强行续租',
    `pickup_materials` TEXT COMMENT '取车材料',
    `city_restriction` TEXT COMMENT '城市限行规则',
    `usage_area_limit` TEXT COMMENT '用车区域限制',
    `fuel_fee` TEXT COMMENT '油费电费',
    `personal_belongings_loss` TEXT COMMENT '随车物品损失',
    `violation_handling` TEXT COMMENT '违章处理',
    `roadside_assistance` TEXT COMMENT '道路救援',
    `forced_recovery` TEXT COMMENT '强制收车',
    `etc_fee` TEXT COMMENT 'ETC费用',
    `cleaning_fee` TEXT COMMENT '清洁费',
    `invoice_info` TEXT COMMENT '发票说明',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人',
    `updated_by` BIGINT COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务政策模板表';

-- 车型商品表
CREATE TABLE `car_model_product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `store_id` BIGINT NOT NULL COMMENT '门店ID',
    `car_model_id` BIGINT NOT NULL COMMENT '车型ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `product_description` TEXT COMMENT '商品描述',
    `damage_deposit` INT NOT NULL DEFAULT 0 COMMENT '车损押金(分)',
    `violation_deposit` INT NOT NULL DEFAULT 0 COMMENT '违章押金(分)',
    `weekday_price` INT NOT NULL COMMENT '周中价格(分)',
    `weekend_price` INT NOT NULL COMMENT '周末价格(分)',
    `weekday_definition` VARCHAR(50) NOT NULL DEFAULT '1,2,3,4,5' COMMENT '周中定义',
    `weekend_definition` VARCHAR(50) NOT NULL DEFAULT '6,7' COMMENT '周末定义',
    `tags` VARCHAR(200) COMMENT '车型标签(JSON格式)',
    `vas_template_id` BIGINT COMMENT '增值服务模板ID',
    `cancellation_template_id` BIGINT COMMENT '取消规则模板ID',
    `service_policy_template_id` BIGINT COMMENT '服务政策模板ID',
    `online_status` TINYINT NOT NULL DEFAULT 0 COMMENT '上架状态:0-下架,1-上架',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人',
    `updated_by` BIGINT COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_store_model` (`tenant_id`, `store_id`, `car_model_id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_store_id` (`store_id`),
    KEY `idx_car_model_id` (`car_model_id`),
    KEY `idx_online_status` (`online_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车型商品表';

-- 车型商品关联车辆表
CREATE TABLE `product_vehicle_relation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `vehicle_id` BIGINT NOT NULL COMMENT '车辆ID',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人',
    `updated_by` BIGINT COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_vehicle` (`product_id`, `vehicle_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_vehicle_id` (`vehicle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车型商品关联车辆表';

-- 特殊定价表
CREATE TABLE `special_pricing` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `price_date` DATE NOT NULL COMMENT '定价日期',
    `price` INT NOT NULL COMMENT '价格(分)',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_date` (`product_id`, `price_date`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_price_date` (`price_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='特殊定价表';

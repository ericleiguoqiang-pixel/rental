-- ===================================================================
-- 商品和定价相关表
-- ===================================================================

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

-- ===================================================================
-- 订单相关表
-- ===================================================================

-- 订单表
CREATE TABLE `rental_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `order_status` TINYINT NOT NULL DEFAULT 1 COMMENT '订单状态:1-待支付,2-待取车,3-已取车,4-已完成,5-已取消',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `cancel_time` DATETIME COMMENT '取消时间',
    `driver_name` VARCHAR(50) NOT NULL COMMENT '驾驶人姓名',
    `driver_id_card` VARCHAR(255) NOT NULL COMMENT '驾驶人身份证号(加密)',
    `driver_phone` VARCHAR(255) NOT NULL COMMENT '驾驶人手机号(加密)',
    `start_time` DATETIME NOT NULL COMMENT '租车开始时间',
    `end_time` DATETIME NOT NULL COMMENT '租车结束时间',
    `actual_pickup_time` DATETIME COMMENT '实际取车时间',
    `actual_return_time` DATETIME COMMENT '实际还车时间',
    `order_location` VARCHAR(200) COMMENT '下单位置',
    `car_model_id` BIGINT NOT NULL COMMENT '车型ID',
    `product_id` BIGINT NOT NULL COMMENT '车型商品ID',
    `license_plate` VARCHAR(20) COMMENT '车牌号',
    `pickup_type` TINYINT NOT NULL COMMENT '取车方式:1-门店自取,2-送车上门',
    `return_type` TINYINT NOT NULL COMMENT '还车方式:1-门店归还,2-上门取车',
    `pickup_store_id` BIGINT COMMENT '取车门店ID',
    `return_store_id` BIGINT COMMENT '还车门店ID',
    `pickup_driver` VARCHAR(50) COMMENT '取车司机',
    `return_driver` VARCHAR(50) COMMENT '还车司机',
    `basic_rental_fee` INT NOT NULL DEFAULT 0 COMMENT '基础租车费(分)',
    `service_fee` INT NOT NULL DEFAULT 0 COMMENT '服务费(分)',
    `insurance_fee` INT NOT NULL DEFAULT 0 COMMENT '保障费(分)',
    `damage_deposit` INT NOT NULL DEFAULT 0 COMMENT '车损押金(分)',
    `violation_deposit` INT NOT NULL DEFAULT 0 COMMENT '违章押金(分)',
    `actual_deposit` INT NOT NULL DEFAULT 0 COMMENT '实际押金(分)',
    `vas_snapshot` TEXT COMMENT '服务保障快照(JSON)',
    `cancellation_rule_snapshot` TEXT COMMENT '取消规则快照(JSON)',
    `service_policy_snapshot` TEXT COMMENT '服务政策快照(JSON)',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-正常,1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_order_status` (`order_status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_driver_phone` (`driver_phone`),
    KEY `idx_license_plate` (`license_plate`),
    KEY `idx_pickup_store_id` (`pickup_store_id`),
    KEY `idx_return_store_id` (`return_store_id`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单状态变更记录表
CREATE TABLE `order_status_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `old_status` TINYINT COMMENT '原状态',
    `new_status` TINYINT NOT NULL COMMENT '新状态',
    `change_reason` VARCHAR(200) COMMENT '变更原因',
    `operator_id` BIGINT COMMENT '操作人ID',
    `operator_name` VARCHAR(50) COMMENT '操作人姓名',
    `change_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_change_time` (`change_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单状态变更记录表';

-- ===================================================================
-- 支付相关表
-- ===================================================================

-- 支付记录表
CREATE TABLE `payment_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
    `payment_no` VARCHAR(32) NOT NULL COMMENT '支付单号',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `payment_type` TINYINT NOT NULL COMMENT '支付类型:1-租车费,2-押金,3-违章费,4-其他费用',
    `payment_amount` INT NOT NULL COMMENT '支付金额(分)',
    `payment_method` TINYINT NOT NULL COMMENT '支付方式:1-微信,2-支付宝,3-银行卡',
    `payment_status` TINYINT NOT NULL DEFAULT 1 COMMENT '支付状态:1-待支付,2-支付成功,3-支付失败,4-已退款',
    `third_party_trade_no` VARCHAR(64) COMMENT '第三方交易号',
    `payment_time` DATETIME COMMENT '支付时间',
    `refund_amount` INT NOT NULL DEFAULT 0 COMMENT '退款金额(分)',
    `refund_time` DATETIME COMMENT '退款时间',
    `refund_reason` VARCHAR(200) COMMENT '退款原因',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_payment_status` (`payment_status`),
    KEY `idx_payment_time` (`payment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- ===================================================================
-- 系统管理相关表
-- ===================================================================

-- 操作日志表
CREATE TABLE `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `tenant_id` BIGINT COMMENT '租户ID',
    `operator_id` BIGINT COMMENT '操作人ID',
    `operator_name` VARCHAR(50) COMMENT '操作人姓名',
    `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `operation_desc` VARCHAR(500) NOT NULL COMMENT '操作描述',
    `request_method` VARCHAR(10) COMMENT '请求方法',
    `request_url` VARCHAR(200) COMMENT '请求URL',
    `request_params` TEXT COMMENT '请求参数',
    `operation_result` TINYINT NOT NULL COMMENT '操作结果:1-成功,2-失败',
    `error_message` VARCHAR(500) COMMENT '错误信息',
    `operation_ip` VARCHAR(50) COMMENT '操作IP',
    `user_agent` VARCHAR(500) COMMENT '用户代理',
    `operation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_operation_type` (`operation_type`),
    KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ===================================================================
-- 初始化基础数据
-- ===================================================================

-- 插入增值服务模板基础数据
INSERT INTO `value_added_service_template` (`template_name`, `service_type`, `price`, `deductible`, `include_tire_damage`, `include_glass_damage`, `third_party_coverage`, `charge_depreciation`, `depreciation_deductible`, `depreciation_rate`) VALUES
('基础保障', 1, 2000, 1500, 0, 0, 30, 1, 500, 20),
('优享保障', 2, 3500, 1000, 1, 0, 50, 1, 300, 15),
('尊享保障', 3, 5000, 500, 1, 1, 100, 0, 0, 0);

-- 插入取消规则模板基础数据
INSERT INTO `cancellation_rule_template` (`template_name`, `weekday_rule`, `holiday_rule`) VALUES
('标准取消规则', '取车前24小时以上免费取消，24小时内取消收取10%手续费', '取车前48小时以上免费取消，48小时内取消收取20%手续费'),
('严格取消规则', '取车前48小时以上免费取消，48小时内取消收取20%手续费', '取车前72小时以上免费取消，72小时内取消收取30%手续费');

-- 插入服务政策模板基础数据
INSERT INTO `service_policy_template` (`template_name`, `mileage_limit`, `early_pickup`, `late_pickup`, `early_return`, `renewal`, `forced_renewal`, `pickup_materials`, `city_restriction`, `usage_area_limit`, `fuel_fee`, `personal_belongings_loss`, `violation_handling`, `roadside_assistance`, `forced_recovery`, `etc_fee`, `cleaning_fee`, `invoice_info`) VALUES
('标准服务政策', '日限里程300公里，总里程无限制', '可提前1小时取车，超出按小时计费', '延迟1小时内免费，超出按小时计费', '提前还车不退费', '可在线申请续租', '逾期未还强制续租，加收20%费用', '需要驾驶证、身份证原件', '遵守当地限行规定', '禁止出省使用', '还车时请加满油/充满电', '车内物品丢失概不负责', '违章由客户承担并处理', '24小时道路救援服务', '逾期未还车辆将强制收回', 'ETC费用实际产生实际承担', '还车时请保持车内清洁', '可开具租车费发票');

-- 插入基础车型数据
INSERT INTO `car_model` (`brand`, `series`, `model`, `year`, `transmission`, `drive_type`, `seat_count`, `door_count`, `category`, `description`) VALUES
('大众', '朗逸', '朗逸 1.5L 自动舒适版', 2023, 1, 1, 5, 4, '经济型,舒适型', '经济实用的家用轿车'),
('丰田', '卡罗拉', '卡罗拉 1.2T CVT精英版', 2023, 1, 1, 5, 4, '经济型,舒适型', '可靠耐用的紧凑型轿车'),
('本田', 'CR-V', 'CR-V 1.5T 两驱舒适版', 2023, 1, 1, 5, 4, 'SUV,舒适型', '实用的紧凑型SUV'),
('奔驰', 'C级', 'C 260 L 豪华型', 2023, 1, 1, 5, 4, '豪华型,商务型', '豪华品牌中型轿车'),
('宝马', '3系', '325Li M运动套装', 2023, 1, 1, 5, 4, '豪华型,运动型', '运动豪华轿车'),
('特斯拉', 'Model 3', 'Model 3 标准续航版', 2023, 1, 2, 5, 4, '豪华型,新能源', '智能电动轿车'),
('比亚迪', '汉', '汉 EV 超长续航版豪华型', 2023, 1, 2, 5, 4, '豪华型,新能源', '国产豪华电动轿车');

-- 创建索引优化查询性能
-- 复合索引
CREATE INDEX `idx_tenant_store_status` ON `store` (`tenant_id`, `audit_status`, `online_status`);
CREATE INDEX `idx_tenant_vehicle_status` ON `vehicle` (`tenant_id`, `audit_status`, `online_status`);
CREATE INDEX `idx_tenant_order_status` ON `rental_order` (`tenant_id`, `order_status`, `create_time`);
CREATE INDEX `idx_order_time_range` ON `rental_order` (`start_time`, `end_time`);

-- ===================================================================
-- 数据库视图
-- ===================================================================

-- 门店汇总视图
CREATE VIEW `v_store_summary` AS
SELECT 
    s.id,
    s.tenant_id,
    s.store_name,
    s.city,
    s.audit_status,
    s.online_status,
    COUNT(v.id) as vehicle_count,
    COUNT(CASE WHEN v.vehicle_status = 1 THEN 1 END) as available_vehicle_count,
    COUNT(p.id) as product_count
FROM `store` s
LEFT JOIN `vehicle` v ON s.id = v.store_id AND v.deleted = 0
LEFT JOIN `car_model_product` p ON s.id = p.store_id AND p.deleted = 0
WHERE s.deleted = 0
GROUP BY s.id;

-- 订单统计视图
CREATE VIEW `v_order_statistics` AS
SELECT 
    tenant_id,
    DATE(create_time) as order_date,
    COUNT(*) as total_orders,
    COUNT(CASE WHEN order_status = 4 THEN 1 END) as completed_orders,
    COUNT(CASE WHEN order_status = 5 THEN 1 END) as cancelled_orders,
    SUM(basic_rental_fee + service_fee + insurance_fee) as total_revenue
FROM `rental_order`
WHERE deleted = 0
GROUP BY tenant_id, DATE(create_time);

COMMIT;

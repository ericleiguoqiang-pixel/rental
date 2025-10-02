-- 创建服务范围表
CREATE TABLE `service_area` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `store_id` bigint(20) NOT NULL COMMENT '门店ID',
  `area_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区域名称',
  `area_type` tinyint(4) NOT NULL COMMENT '区域类型:1-取车区域,2-还车区域',
  `fence_coordinates` text COLLATE utf8mb4_unicode_ci COMMENT '电子围栏坐标(JSON格式)',
  `advance_hours` int(11) NOT NULL DEFAULT '2' COMMENT '提前预定时间(小时)',
  `service_start_time` varchar(5) NOT NULL COMMENT '服务开始时间(HH:MM)',
  `service_end_time` varchar(5) NOT NULL COMMENT '服务结束时间(HH:MM)',
  `door_to_door_delivery` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否提供送车上门:0-否,1-是',
  `delivery_fee` int(11) NOT NULL DEFAULT '0' COMMENT '送车上门费用(分)',
  `free_pickup_to_store` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否免费接至门店:0-否,1-是',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除:0-正常,1-删除',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_store_id` (`store_id`),
  KEY `idx_area_type` (`area_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务范围表';

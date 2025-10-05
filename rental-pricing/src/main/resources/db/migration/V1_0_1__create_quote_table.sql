-- 创建报价表
CREATE TABLE `quote` (
  `id` varchar(32) NOT NULL COMMENT '报价ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `model_id` bigint NOT NULL COMMENT '车型ID',
  `model_name` varchar(100) NOT NULL COMMENT '车型名称',
  `store_id` bigint NOT NULL COMMENT '门店ID',
  `store_name` varchar(100) NOT NULL COMMENT '门店名称',
  `daily_rate` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '日租金',
  `pickup_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '取车服务费',
  `return_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '还车服务费',
  `store_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '门店手续费',
  `base_protection_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '基本保障价格',
  `total_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '总价格',
  `delivery_type` varchar(20) NOT NULL COMMENT '取还方式：上门取送车/用户到店自取',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报价表';
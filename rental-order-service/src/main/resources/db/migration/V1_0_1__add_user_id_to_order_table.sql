-- 添加用户ID字段到订单表
ALTER TABLE rental_order ADD COLUMN user_id BIGINT NULL COMMENT '用户ID' AFTER order_no;
-- 添加优享保障和尊享保障模板字段
ALTER TABLE `car_model_product` 
ADD COLUMN `vas_template_id_vip` BIGINT NULL COMMENT '优享保障模板ID' AFTER `vas_template_id`,
ADD COLUMN `vas_template_id_vvip` BIGINT NULL COMMENT '尊享保障模板ID' AFTER `vas_template_id_vip`;
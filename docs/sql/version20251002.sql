ALTER table special_pricing add COLUMN `tenant_id` BIGINT NOT NULL COMMENT '租户ID' after id;

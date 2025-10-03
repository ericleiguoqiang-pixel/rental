-- 插入基础数据

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
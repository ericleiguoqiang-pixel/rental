-- =====================================================
-- 汽车租赁SaaS平台测试数据SQL
-- =====================================================

-- 清理现有测试数据（可选，谨慎使用）
-- DELETE FROM employee WHERE id >= 1000;
-- DELETE FROM store WHERE id >= 1000;
-- DELETE FROM tenant WHERE id >= 1000;
-- DELETE FROM merchant_application WHERE id >= 1000;

-- =====================================================
-- 1. 商户入驻申请数据
-- =====================================================

-- 商户入驻申请1 - 已审核通过
INSERT INTO merchant_application (
    id, application_no, company_name, unified_social_credit_code, 
    legal_person_name, legal_person_id_card, contact_name, contact_phone,
    company_address, business_license_url, transport_license_url,
    application_status, audit_remark, audit_time, auditor_id, tenant_id,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1001, 'APP202412010001', '北京云驰汽车租赁有限公司', '91110108MA01ABCD12',
    '张建国', '110101198501156789', '李小明', '13800138001',
    '北京市海淀区中关村大街1号科技大厦A座15层', 
    'https://oss.example.com/license_001.jpg', 'https://oss.example.com/transport_001.jpg',
    1, '资料齐全，审核通过', '2024-12-01 10:30:00', 1, 1001,
    '2024-11-25 09:00:00', '2024-12-01 10:30:00', 1, 1, 0
);

-- 商户入驻申请2 - 已审核通过
INSERT INTO merchant_application (
    id, application_no, company_name, unified_social_credit_code, 
    legal_person_name, legal_person_id_card, contact_name, contact_phone,
    company_address, business_license_url, transport_license_url,
    application_status, audit_remark, audit_time, auditor_id, tenant_id,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1002, 'APP202412010002', '上海速行汽车服务有限公司', '91310115MA02BCDE23',
    '王德华', '310101198703201234', '刘芳', '13800138002',
    '上海市浦东新区张江高科技园区2号楼8层',
    'https://oss.example.com/license_002.jpg', 'https://oss.example.com/transport_002.jpg',
    1, '企业资质良好，准予通过', '2024-12-02 14:20:00', 1, 1002,
    '2024-11-26 14:30:00', '2024-12-02 14:20:00', 1, 1, 0
);

-- 商户入驻申请3 - 已审核通过
INSERT INTO merchant_application (
    id, application_no, company_name, unified_social_credit_code, 
    legal_person_name, legal_person_id_card, contact_name, contact_phone,
    company_address, business_license_url, transport_license_url,
    application_status, audit_remark, audit_time, auditor_id, tenant_id,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1003, 'APP202412010003', '深圳飞驰租车有限公司', '91440300MA03CDEF34',
    '陈志强', '440301198906155678', '周敏', '13800138003',
    '深圳市南山区科技园南区软件大厦B座12层',
    'https://oss.example.com/license_003.jpg', 'https://oss.example.com/transport_003.jpg',
    1, '符合入驻条件，审核通过', '2024-12-03 11:15:00', 1, 1003,
    '2024-11-27 16:45:00', '2024-12-03 11:15:00', 1, 1, 0
);

-- 商户入驻申请4 - 待审核
INSERT INTO merchant_application (
    id, application_no, company_name, unified_social_credit_code, 
    legal_person_name, legal_person_id_card, contact_name, contact_phone,
    company_address, business_license_url, transport_license_url,
    application_status, audit_remark, audit_time, auditor_id, tenant_id,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1004, 'APP202412010004', '广州快车道汽车租赁有限公司', '91440101MA04DEFG45',
    '黄志明', '440101198808122345', '杨丽', '13800138004',
    '广州市天河区珠江新城花城大道88号写字楼20层',
    'https://oss.example.com/license_004.jpg', 'https://oss.example.com/transport_004.jpg',
    0, NULL, NULL, NULL, NULL,
    '2024-11-30 10:20:00', '2024-11-30 10:20:00', 1, 1, 0
);

-- 商户入驻申请5 - 审核拒绝
INSERT INTO merchant_application (
    id, application_no, company_name, unified_social_credit_code, 
    legal_person_name, legal_person_id_card, contact_name, contact_phone,
    company_address, business_license_url, transport_license_url,
    application_status, audit_remark, audit_time, auditor_id, tenant_id,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1005, 'APP202412010005', '杭州便民租车服务有限公司', '91330108MA05EFGH56',
    '林建华', '330108198710203456', '赵娟', '13800138005',
    '杭州市西湖区文三路259号昌地火炬大厦16层',
    'https://oss.example.com/license_005.jpg', 'https://oss.example.com/transport_005.jpg',
    2, '道路运输经营许可证已过期，请更新后重新申请', '2024-12-04 09:45:00', 1, NULL,
    '2024-11-28 13:15:00', '2024-12-04 09:45:00', 1, 1, 0
);

-- =====================================================
-- 2. 租户数据
-- =====================================================

-- 租户1 - 对应商户申请1001
INSERT INTO tenant (
    id, tenant_code, company_name, contact_name, contact_phone,
    status, expire_time, package_type,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1001, 'T202412010001', '北京云驰汽车租赁有限公司', '李小明', '13800138001',
    1, '2025-12-01 23:59:59', 2,
    '2024-12-01 10:30:00', '2024-12-01 10:30:00', 1, 1, 0
);

-- 租户2 - 对应商户申请1002
INSERT INTO tenant (
    id, tenant_code, company_name, contact_name, contact_phone,
    status, expire_time, package_type,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1002, 'T202412020001', '上海速行汽车服务有限公司', '刘芳', '13800138002',
    1, '2025-12-02 23:59:59', 3,
    '2024-12-02 14:20:00', '2024-12-02 14:20:00', 1, 1, 0
);

-- 租户3 - 对应商户申请1003
INSERT INTO tenant (
    id, tenant_code, company_name, contact_name, contact_phone,
    status, expire_time, package_type,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1003, 'T202412030001', '深圳飞驰租车有限公司', '周敏', '13800138003',
    1, '2025-12-03 23:59:59', 1,
    '2024-12-03 11:15:00', '2024-12-03 11:15:00', 1, 1, 0
);

-- =====================================================
-- 3. 门店数据
-- =====================================================

-- 租户1001的门店
INSERT INTO store (
    id, tenant_id, store_name, city, address, longitude, latitude,
    business_start_time, business_end_time, audit_status, online_status,
    min_advance_hours, max_advance_days, service_fee,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1001, 1001, '北京云驰中关村门店', '北京', '北京市海淀区中关村大街100号',
    116.316833, 39.999374, '08:00:00', '22:00:00', 1, 1, 2, 30, 1000,
    '2024-12-01 11:00:00', '2024-12-01 11:00:00', 1001, 1001, 0
);

INSERT INTO store (
    id, tenant_id, store_name, city, address, longitude, latitude,
    business_start_time, business_end_time, audit_status, online_status,
    min_advance_hours, max_advance_days, service_fee,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1002, 1001, '北京云驰朝阳门店', '北京', '北京市朝阳区朝阳门外大街88号',
    116.434559, 39.930706, '08:30:00', '21:30:00', 1, 1, 2, 30, 1000,
    '2024-12-01 11:30:00', '2024-12-01 11:30:00', 1001, 1001, 0
);

-- 租户1002的门店
INSERT INTO store (
    id, tenant_id, store_name, city, address, longitude, latitude,
    business_start_time, business_end_time, audit_status, online_status,
    min_advance_hours, max_advance_days, service_fee,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1003, 1002, '上海速行浦东门店', '上海', '上海市浦东新区陆家嘴环路1000号',
    121.507831, 31.244359, '07:30:00', '22:30:00', 1, 1, 1, 45, 1200,
    '2024-12-02 15:00:00', '2024-12-02 15:00:00', 1002, 1002, 0
);

INSERT INTO store (
    id, tenant_id, store_name, city, address, longitude, latitude,
    business_start_time, business_end_time, audit_status, online_status,
    min_advance_hours, max_advance_days, service_fee,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1004, 1002, '上海速行虹桥门店', '上海', '上海市长宁区天山路1717号',
    121.397489, 31.224178, '08:00:00', '22:00:00', 1, 1, 1, 45, 1200,
    '2024-12-02 15:30:00', '2024-12-02 15:30:00', 1002, 1002, 0
);

-- 租户1003的门店
INSERT INTO store (
    id, tenant_id, store_name, city, address, longitude, latitude,
    business_start_time, business_end_time, audit_status, online_status,
    min_advance_hours, max_advance_days, service_fee,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1005, 1003, '深圳飞驰南山门店', '深圳', '深圳市南山区深南大道9988号',
    113.926436, 22.532449, '08:00:00', '23:00:00', 1, 1, 2, 60, 800,
    '2024-12-03 12:00:00', '2024-12-03 12:00:00', 1003, 1003, 0
);

-- =====================================================
-- 4. 员工数据
-- =====================================================

-- 租户1001的员工
-- 超级管理员
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1001, 1001, NULL, '李小明', '13800138001', 'admin001', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 1, '2024-12-10 09:30:00', '192.168.1.100',
    '2024-12-01 10:35:00', '2024-12-10 09:30:00', 1001, 1001, 0
);

-- 中关村门店管理员
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1002, 1001, 1001, '张伟强', '13800138011', 'store001_admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 2, '2024-12-10 08:45:00', '192.168.1.101',
    '2024-12-01 11:05:00', '2024-12-10 08:45:00', 1001, 1001, 0
);

-- 中关村门店车辆管理员
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1003, 1001, 1001, '王建华', '13800138012', 'vehicle001_admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 3, '2024-12-09 16:20:00', '192.168.1.102',
    '2024-12-01 11:10:00', '2024-12-09 16:20:00', 1001, 1001, 0
);

-- 朝阳门店管理员
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1004, 1001, 1002, '刘明', '13800138021', 'store002_admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 2, '2024-12-10 10:15:00', '192.168.1.103',
    '2024-12-01 11:35:00', '2024-12-10 10:15:00', 1001, 1001, 0
);

-- 朝阳门店普通员工
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1005, 1001, 1002, '陈小丽', '13800138022', 'employee002_001', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 5, '2024-12-09 18:30:00', '192.168.1.104',
    '2024-12-01 11:40:00', '2024-12-09 18:30:00', 1001, 1001, 0
);

-- 租户1002的员工
-- 超级管理员
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1006, 1002, NULL, '刘芳', '13800138002', 'admin002', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 1, '2024-12-10 11:20:00', '192.168.2.100',
    '2024-12-02 14:25:00', '2024-12-10 11:20:00', 1002, 1002, 0
);

-- 浦东门店管理员
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1007, 1002, 1003, '徐志刚', '13800138031', 'store003_admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 2, '2024-12-10 09:45:00', '192.168.2.101',
    '2024-12-02 15:05:00', '2024-12-10 09:45:00', 1002, 1002, 0
);

-- 浦东门店订单管理员
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1008, 1002, 1003, '吴小红', '13800138032', 'order003_admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 4, '2024-12-09 14:30:00', '192.168.2.102',
    '2024-12-02 15:10:00', '2024-12-09 14:30:00', 1002, 1002, 0
);

-- 虹桥门店管理员
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1009, 1002, 1004, '马强', '13800138041', 'store004_admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 2, '2024-12-10 08:20:00', '192.168.2.103',
    '2024-12-02 15:35:00', '2024-12-10 08:20:00', 1002, 1002, 0
);

-- 虹桥门店普通员工
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1010, 1002, 1004, '李娜', '13800138042', 'employee004_001', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 5, '2024-12-08 17:45:00', '192.168.2.104',
    '2024-12-02 15:40:00', '2024-12-08 17:45:00', 1002, 1002, 0
);

-- 租户1003的员工
-- 超级管理员
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1011, 1003, NULL, '周敏', '13800138003', 'admin003', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 1, '2024-12-10 12:30:00', '192.168.3.100',
    '2024-12-03 11:20:00', '2024-12-10 12:30:00', 1003, 1003, 0
);

-- 南山门店管理员
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1012, 1003, 1005, '黄志华', '13800138051', 'store005_admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 2, '2024-12-10 07:50:00', '192.168.3.101',
    '2024-12-03 12:05:00', '2024-12-10 07:50:00', 1003, 1003, 0
);

-- 南山门店车辆管理员
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1013, 1003, 1005, '林小军', '13800138052', 'vehicle005_admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 3, '2024-12-09 19:15:00', '192.168.3.102',
    '2024-12-03 12:10:00', '2024-12-09 19:15:00', 1003, 1003, 0
);

-- 南山门店订单管理员
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1014, 1003, 1005, '谢小梅', '13800138053', 'order005_admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 4, '2024-12-10 13:40:00', '192.168.3.103',
    '2024-12-03 12:15:00', '2024-12-10 13:40:00', 1003, 1003, 0
);

-- 南山门店普通员工
INSERT INTO employee (
    id, tenant_id, store_id, employee_name, phone, username, password,
    status, role_type, last_login_time, last_login_ip,
    created_time, updated_time, created_by, updated_by, deleted
) VALUES (
    1015, 1003, 1005, '郑文杰', '13800138054', 'employee005_001', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lec.H6NNiIFt4iB7W', -- 密码：123456
    1, 5, '2024-12-09 20:30:00', '192.168.3.104',
    '2024-12-03 12:20:00', '2024-12-09 20:30:00', 1003, 1003, 0
);

-- =====================================================
-- 数据插入完成
-- =====================================================

-- 查询验证数据
SELECT 'merchant_application' as table_name, COUNT(*) as count FROM merchant_application WHERE id >= 1001
UNION ALL
SELECT 'tenant', COUNT(*) FROM tenant WHERE id >= 1001
UNION ALL
SELECT 'store', COUNT(*) FROM store WHERE id >= 1001
UNION ALL
SELECT 'employee', COUNT(*) FROM employee WHERE id >= 1001;

-- 查看数据关联关系
SELECT 
    ma.application_no,
    ma.company_name as application_company,
    ma.application_status,
    t.tenant_code,
    t.company_name as tenant_company,
    COUNT(s.id) as store_count,
    COUNT(e.id) as employee_count
FROM merchant_application ma
LEFT JOIN tenant t ON ma.tenant_id = t.id
LEFT JOIN store s ON t.id = s.tenant_id
LEFT JOIN employee e ON t.id = e.tenant_id
WHERE ma.id >= 1001
GROUP BY ma.id, ma.application_no, ma.company_name, ma.application_status, t.tenant_code, t.company_name
ORDER BY ma.id;

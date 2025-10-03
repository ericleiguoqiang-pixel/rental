# 订单服务 (Rental Order Service)

订单服务是租车SaaS平台的核心服务之一，负责处理订单的创建、状态管理、查询统计等功能。

## 功能特性

- 订单创建与管理
- 订单状态流转控制
- 订单查询与统计
- 订单状态变更记录
- 司机分配功能
- 取车还车确认

## 技术栈

- Spring Boot 2.7.x
- Spring Cloud Alibaba
- MyBatis Plus
- MySQL 8.0
- Redis
- Nacos (服务发现与配置中心)
- RocketMQ (消息队列)

## 服务接口

### 订单管理接口

#### 获取订单列表
```
GET /api/orders
```

#### 获取订单详情
```
GET /api/orders/{id}
```

#### 分配取车司机
```
PUT /api/orders/{id}/pickup-driver?driverName={driverName}
```

#### 分配还车司机
```
PUT /api/orders/{id}/return-driver?driverName={driverName}
```

#### 确认取车
```
PUT /api/orders/{id}/confirm-pickup
```

#### 确认还车
```
PUT /api/orders/{id}/confirm-return
```

## 订单状态说明

| 状态码 | 状态名称 | 说明 |
|-------|---------|------|
| 1 | 待支付 | 订单已创建，等待用户支付 |
| 2 | 待取车 | 用户已支付，等待取车 |
| 3 | 已取车 | 用户已取车，正在使用中 |
| 4 | 已完成 | 用户已还车，订单完成 |
| 5 | 已取消 | 订单已取消 |

## 数据库表结构

### 订单表 (rental_order)

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | BIGINT | 订单ID |
| order_no | VARCHAR(32) | 订单号 |
| tenant_id | BIGINT | 租户ID |
| order_status | TINYINT | 订单状态 |
| create_time | DATETIME | 下单时间 |
| cancel_time | DATETIME | 取消时间 |
| driver_name | VARCHAR(50) | 驾驶人姓名 |
| driver_id_card | VARCHAR(255) | 驾驶人身份证号(加密) |
| driver_phone | VARCHAR(255) | 驾驶人手机号(加密) |
| start_time | DATETIME | 租车开始时间 |
| end_time | DATETIME | 租车结束时间 |
| actual_pickup_time | DATETIME | 实际取车时间 |
| actual_return_time | DATETIME | 实际还车时间 |
| order_location | VARCHAR(200) | 下单位置 |
| car_model_id | BIGINT | 车型ID |
| product_id | BIGINT | 车型商品ID |
| license_plate | VARCHAR(20) | 车牌号 |
| pickup_type | TINYINT | 取车方式 |
| return_type | TINYINT | 还车方式 |
| pickup_store_id | BIGINT | 取车门店ID |
| return_store_id | BIGINT | 还车门店ID |
| pickup_driver | VARCHAR(50) | 取车司机 |
| return_driver | VARCHAR(50) | 还车司机 |
| basic_rental_fee | INT | 基础租车费(分) |
| service_fee | INT | 服务费(分) |
| insurance_fee | INT | 保障费(分) |
| damage_deposit | INT | 车损押金(分) |
| violation_deposit | INT | 违章押金(分) |
| actual_deposit | INT | 实际押金(分) |

### 订单状态变更记录表 (order_status_log)

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | BIGINT | 主键ID |
| order_id | BIGINT | 订单ID |
| old_status | TINYINT | 原状态 |
| new_status | TINYINT | 新状态 |
| change_reason | VARCHAR(200) | 变更原因 |
| operator_id | BIGINT | 操作人ID |
| operator_name | VARCHAR(50) | 操作人姓名 |
| change_time | DATETIME | 变更时间 |

## 配置说明

### 本地开发配置

1. 配置文件位置：
   - 主配置文件：`src/main/resources/application.yml`
   - 开发环境配置：`src/main/resources/application-dev.yml`
   - Nacos引导配置：`src/main/resources/bootstrap.yml`

2. 数据库配置：
   - MySQL 8.0
   - 数据库名：rentalsaas
   - 用户名：rentalsaas
   - 密码：rentalsaas

3. Redis配置：
   - 地址：localhost:6379
   - 密码：rentalsaas
   - 数据库：0

4. Nacos配置：
   - 服务地址：43.143.163.102:8848
   - 命名空间：develop

### Nacos配置中心

服务启动时会从Nacos配置中心加载配置，配置文件名为：`rental-order-service-dev.yml`

## 启动方式

1. 确保MySQL、Redis、Nacos服务已启动
2. 配置application.yml中的数据库连接信息
3. 运行OrderServiceApplication.main()方法

## 注意事项

1. 订单服务依赖于基础数据服务、用户服务、商品服务等
2. 订单状态变更需遵循业务规则，不能随意跳转
3. 所有涉及金额的字段单位为分
4. 敏感信息如身份证号、手机号等需加密存储
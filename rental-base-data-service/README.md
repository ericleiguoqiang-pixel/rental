# Rental Base Data Service

基础数据服务，提供门店管理、车型库、车辆管理等功能。

## 📋 功能特性

### 🏪 门店管理
- **门店CRUD操作**：创建、查询、更新、删除门店
- **门店审核**：支持门店审核流程（待审核→审核通过/拒绝）
- **门店上下架**：支持门店在线状态管理
- **地理位置查询**：基于经纬度的附近门店查询
- **多条件筛选**：按城市、审核状态、上架状态等筛选
- **数据统计**：门店数量统计

### 🚗 车辆管理
- **车辆CRUD操作**：创建、查询、更新、删除车辆
- **车辆审核**：支持车辆审核流程
- **车辆状态管理**：空闲、租出、维修、报废状态流转
- **车辆上下架**：支持车辆在线状态管理
- **多维度查询**：按门店、车型、状态等查询
- **数据校验**：车牌号、车架号唯一性校验
- **里程更新**：车辆里程数据维护

### 🚙 车型管理
- **车型库管理**：统一的车型数据管理
- **多条件筛选**：按品牌、车系、座位数、档位、驱动类型等筛选
- **层级查询**：品牌→车系→车型的层级查询
- **批量导入**：支持车型数据批量导入
- **数据校验**：车型唯一性校验

## 🏗️ 技术架构

### 技术栈
- **Spring Boot 3.x**：应用框架
- **Spring Cloud 2023.x**：微服务框架
- **MyBatis Plus 3.x**：数据访问层
- **MySQL 8.0**：数据存储
- **Redis**：缓存存储
- **Nacos**：服务注册与配置中心
- **RocketMQ**：消息队列

### 架构设计
```
Controller层 → Service层 → Mapper层 → 数据库
     ↓           ↓           ↓
   API接口    业务逻辑    数据访问
```

## 📁 项目结构

```
rental-base-data-service/
├── src/main/java/com/rental/saas/basedata/
│   ├── controller/          # 控制器层
│   │   ├── StoreController.java
│   │   ├── VehicleController.java
│   │   └── CarModelController.java
│   ├── service/             # 服务层
│   │   ├── StoreService.java
│   │   ├── VehicleService.java
│   │   ├── CarModelService.java
│   │   └── impl/            # 服务实现
│   ├── mapper/              # 数据访问层
│   │   ├── StoreMapper.java
│   │   ├── VehicleMapper.java
│   │   └── CarModelMapper.java
│   ├── entity/              # 实体类
│   │   ├── Store.java
│   │   ├── Vehicle.java
│   │   └── CarModel.java
│   ├── dto/                 # 数据传输对象
│   │   ├── request/         # 请求DTO
│   │   └── response/        # 响应DTO
│   └── BaseDataServiceApplication.java
├── src/main/resources/
│   ├── mapper/              # MyBatis XML映射文件
│   ├── application.yml      # 配置文件
│   └── application-dev.yml  # 开发环境配置
└── pom.xml
```

## 🔌 API接口

### 门店管理接口
- `POST /api/stores` - 创建门店
- `PUT /api/stores/{id}` - 更新门店
- `DELETE /api/stores/{id}` - 删除门店
- `GET /api/stores/{id}` - 查询门店详情
- `GET /api/stores` - 分页查询门店
- `GET /api/stores/online` - 查询在线门店
- `POST /api/stores/{id}/online` - 门店上架
- `POST /api/stores/{id}/offline` - 门店下架
- `POST /api/stores/{id}/audit` - 门店审核

### 车辆管理接口
- `POST /api/vehicles` - 创建车辆
- `PUT /api/vehicles/{id}` - 更新车辆
- `DELETE /api/vehicles/{id}` - 删除车辆
- `GET /api/vehicles/{id}` - 查询车辆详情
- `GET /api/vehicles` - 分页查询车辆
- `GET /api/vehicles/available` - 查询可用车辆
- `POST /api/vehicles/{id}/online` - 车辆上架
- `POST /api/vehicles/{id}/offline` - 车辆下架
- `PUT /api/vehicles/{id}/status` - 更新车辆状态

### 车型管理接口
- `POST /api/car-models` - 创建车型
- `PUT /api/car-models/{id}` - 更新车型
- `DELETE /api/car-models/{id}` - 删除车型
- `GET /api/car-models/{id}` - 查询车型详情
- `GET /api/car-models` - 分页查询车型
- `GET /api/car-models/brands` - 查询所有品牌
- `POST /api/car-models/batch-import` - 批量导入车型

## 🚀 部署说明

### 环境要求
- JDK 21+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.3+

### 配置文件
1. 修改 `application-dev.yml` 中的数据库连接配置
2. 修改 Redis 连接配置
3. 修改 Nacos 注册中心地址

### 环境配置说明
- **MySQL**: 43.143.163.102:3306/rentalsaas
- **Redis**: 43.143.163.102:6379 (database: 1)
- **Nacos**: 43.143.163.102:8848 (namespace: develop)

### 启动服务
```bash
# 编译项目
mvn clean compile -pl rental-base-data-service

# 启动服务
mvn spring-boot:run -pl rental-base-data-service
```

### 访问地址
- 服务地址：http://localhost:8082/base-data
- API文档：http://localhost:8082/base-data/swagger-ui.html
- 健康检查：http://localhost:8082/base-data/actuator/health

## 📊 数据库设计

### 核心表结构
- **store** - 门店表
- **vehicle** - 车辆表  
- **car_model** - 车型表

所有表都继承基础字段：
- `id` - 主键
- `created_time` - 创建时间
- `updated_time` - 更新时间
- `created_by` - 创建人
- `updated_by` - 更新人
- `deleted` - 逻辑删除标识

多租户表还包含：
- `tenant_id` - 租户ID

## 🔧 开发规范

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用统一的响应结构 `ApiResponse<T>`
- 使用统一的错误码 `ResponseCode`
- 所有接口都有完整的Swagger注解

### 异常处理
- 业务异常使用 `BusinessException`
- 统一错误码定义在 `ResponseCode` 枚举中
- 禁止使用魔法值

### 日志规范
- 关键操作记录INFO级别日志
- 异常记录ERROR级别日志
- 调试信息记录DEBUG级别日志

## 🎯 后续优化

1. **性能优化**
   - 添加Redis缓存
   - 数据库索引优化
   - 分页查询优化

2. **功能扩展**
   - 门店图片管理
   - 车辆保险信息管理
   - 车型配置参数扩展

3. **监控告警**
   - 接口性能监控
   - 异常告警机制
   - 业务指标监控
# 租车SaaS系统

## 项目概述

租车SaaS系统是一个多租户的云服务平台，为租车行业提供完整的数字化解决方案。

## 进展和计划
- 已完成SaaS端、Mis端和前端h5的基本功能，还有很多功能不够完善，有些数据也是假数据，持续完善中
- 项目中的代码90%都是AI生成的，我使用阿里的Qoder做为编程工具
- 后续计划在项目中添加AI相关功能，包括商户可以通过自然语言完成功能设置，运营可以通过自然语言完成审等
- 也可以提供AI的数据分析、行业动态、竞品比对等

## 在线体验
- SaaS端：http://43.143.163.102:8001/ ，账号：13511112223，密码：123456
- Mis端：http://43.143.163.102:8002/ ，账号：admin，密码：admin
- 用户端：http://43.143.163.102:8003/ ，账号：手机号注册，验证码：1111

## 技术架构

### 微服务架构

```
├── rental-gateway          # API网关服务 (认证鉴权、限流熔断)
├── rental-user-service      # 用户服务 (商户入驻、员工管理、权限控制)
├── rental-base-data-service # 基础数据服务 (门店管理、车型库、车辆管理)
├── rental-product-service   # 商品服务 (车型商品、定价策略、增值服务)
├── rental-order-service     # 订单服务 (订单管理、状态流转、查询统计)
├── rental-payment-service   # 支付服务 (支付接口、退款处理、账单管理)
├── rental-common           # 公共模块 (通用工具类、枚举、异常处理)
├── rental-api              # 公共模块 (服务间API调用接口)
└── rental-merchant-web     # 商户端前端 (React + Ant Design)
```

### 技术栈

| 层级 | 技术选型 | 版本         |
|------|----------|------------|
| 前端框架 | React | 18.2.0     |
| UI组件库 | Ant Design | 5.2.0      |
| 后端框架 | Spring Boot | 3.0.12     |
| 微服务治理 | Spring Cloud Alibaba | 2022.0.4 |
| 注册中心 | Nacos | -          |
| 配置中心 | Nacos | -          |
| 网关 | Spring Cloud Gateway | -          |
| 缓存 | Redis | -          |
| 数据库 | MySQL | 8.0        |
| 消息队列 | RocketMQ | -          |

## 高德地图集成

系统使用高德地图API实现电子围栏绘制功能，请参考 [高德地图集成指南](docs/AMAP_INTEGRATION.md) 进行配置。

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.0+
- 高德地图API密钥（用于电子围栏功能）

### 项目结构

```
rental/
├── docs/                    # 文档目录
│   ├── sql/                # 数据库脚本
│   └── api/               # API文档
├── scripts/               # 部署脚本
├── rental-common/         # 公共模块
├── rental-api/            # API模块
├── rental-gateway/        # API网关
├── rental-user-service/   # 用户服务
├── rental-base-data-service/ # 基础数据服务
├── rental-product-service/   # 商品服务
├── rental-order-service/     # 订单服务
├── rental-payment-service/   # 支付服务
├── rental-merchant-web/      # 商户端前端
└── pom.xml                   # 根POM文件
```

### 本地开发

1. **克隆项目**
```bash
git clone <repository-url>
cd rental
```

2. **启动中间件**
```bash
# 启动MySQL、Redis、Nacos
docker-compose -f docker/docker-compose.yml up -d
```

3. **初始化数据库**
```bash
# 执行数据库脚本
mysql -u root -p < docs/sql/init.sql
```

4. **编译后端项目**
```bash
mvn clean install -DskipTests
```

5. **启动微服务**
```bash
# 按顺序启动各个服务
cd rental-gateway && mvn spring-boot:run
cd rental-user-service && mvn spring-boot:run
cd rental-base-data-service && mvn spring-boot:run
cd rental-product-service && mvn spring-boot:run
cd rental-order-service && mvn spring-boot:run
cd rental-payment-service && mvn spring-boot:run
```

6. **启动前端**
```bash
cd rental-merchant-web
npm install
npm run dev
```

### 访问地址

- 商户端前端: http://localhost:3000
- API网关: http://localhost:8090
- Nacos控制台: http://localhost:8848/nacos

## 核心功能

### 服务范围管理
- 基于高德地图的电子围栏绘制
- 取车/还车区域分类管理
- 服务时间配置
- 送车上门服务设置
- 费用配置

### 商户入驻管理
- 商户信息录入和审核
- 资质证书上传和验证
- 账户初始化和配置

### 门店管理
- 门店信息维护
- 服务范围配置
- 电子围栏设置（基于高德地图）
- 营业时间管理

### 车型库管理
- 标准车型数据维护
- 车型分类和属性配置
- 车型图片和详情管理

### 车辆管理
- 车辆信息录入
- 车辆审核流程
- 车辆状态管理
- 车辆调度分配

### 车型商品管理
- 商品信息配置
- 定价策略管理
- 增值服务配置
- 取消规则设置
- 服务政策配置

### 员工管理
- 员工信息维护
- 角色权限分配
- 登录状态管理

### 订单管理
- 订单查询和筛选
- 订单状态流转
- 取车还车确认
- 订单统计分析

## 开发规范

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用统一的代码格式化配置
- 必须编写单元测试，覆盖率≥80%

### 提交规范
- 使用语义化提交信息
- 每个功能点创建独立分支
- 代码Review后合并主分支

### API设计规范
- RESTful API设计原则
- 统一的响应格式
- 完整的API文档

## 部署方案

### Docker部署
```bash
# 构建镜像
docker build -t rental-gateway rental-gateway/
docker build -t rental-user-service rental-user-service/

# 运行容器
docker-compose up -d
```

### K8s部署
```bash
# 应用配置
kubectl apply -f k8s/
```

## 监控告警

- **应用监控**: Spring Boot Actuator + Micrometer
- **链路追踪**: SkyWalking
- **日志收集**: ELK Stack
- **告警通知**: 钉钉、邮件

## 许可证

本项目采用 MIT 许可证，详情请参阅 [LICENSE](LICENSE) 文件。

## 贡献指南

1. Fork 本仓库
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的修改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

## 联系我们

- 项目负责人: [lgq@vip.qq.com]
- 技术支持: [lgq@vip.qq.com]
- 问题反馈: [GitHub Issues](链接)
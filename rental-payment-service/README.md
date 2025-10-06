# Rental Payment Service

支付服务，提供支付接口、退款处理、账单管理等功能。

## 功能特性

- 支付请求处理
- 支付回调处理
- 支付记录管理
- 退款处理

## 技术栈

- Spring Boot 2.7.x
- Spring Cloud 2021.x
- MyBatis Plus
- MySQL 8.0
- Redis
- Nacos 2.x

## 环境要求

- JDK 11+
- MySQL 8.0
- Redis 6.x
- Nacos 2.x

## 配置说明

### application.yml
```yaml
server:
  port: 8098

spring:
  profiles:
    active: dev
  application:
    name: rental-payment-service
```

### application-dev.yml
```yaml
spring:
  config:
    import:
      - optional:nacos:${spring.application.name}-${spring.profiles.active}.yml

  cloud:
    nacos:
      discovery:
        server-addr: 43.143.163.102:8848
        namespace: develop
      config:
        server-addr: 43.143.163.102:8848
        namespace: develop
  
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/rentalsaas?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: rentalsaas
    password: rentalsaas
    
  data:
    redis:
      host: localhost
      port: 6379
      password: rentalsaas
```

## 启动方式

```bash
# 开发环境启动
./mvnw spring-boot:run

# 或者打包后运行
./mvnw clean package
java -jar target/rental-payment-service-1.0.0-SNAPSHOT.jar
```

## API接口

### 发起支付
```
POST /api/payments
```

### 支付回调
```
POST /api/payments/callback
```

### 模拟支付成功
```
POST /api/payments/simulate-success
```

## 数据库表结构

支付服务使用以下表：

- `payment_record`: 支付记录表

表结构定义请参考 `/docs/sql/tables.sql` 文件。
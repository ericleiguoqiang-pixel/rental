# Rental MCP Gateway

Rental MCP Gateway是租车SaaS平台的MCP（Model Connector Plugin）网关模块，为商户提供AI Agent开发工具。

## 功能特性

### 门店管理工具 (store_management)
- 创建门店 (create_store)
- 更新门店 (update_store)
- 查询门店详情 (get_store_detail)
- 查询全部门店 (get_all_stores)
- 设置门店上架状态 (online_store)
- 设置门店下架状态 (offline_store)
- 统计租户门店数 (count_tenant_stores)

### 车辆管理工具 (vehicle_management)
- 创建车辆 (create_vehicle)
- 更新车辆 (update_vehicle)
- 删除车辆 (delete_vehicle)
- 查询车辆详情 (get_vehicle_detail)
- 分页查询车辆列表 (get_vehicle_list)
- 根据车型查询车辆列表 (get_vehicles_by_model)
- 根据车牌号查询车辆 (get_vehicle_by_license_plate)
- 设置车辆为上架状态 (online_vehicle)
- 设置车辆为下架状态 (offline_vehicle)
- 更新车辆总里程数 (update_vehicle_mileage)
- 统计租户车辆总数 (count_tenant_vehicles)
- 统计指定门店的车辆数量 (count_store_vehicles)
- 统计租户各状态车辆数量 (count_vehicles_by_status)

## 技术架构

- 基于Spring AI框架实现
- 使用SSE（Server-Sent Events）协议提供流式通信
- 通过Feign Client调用基础数据服务
- 支持Nacos服务发现

## 配置说明

### application.yml
```yaml
server:
  port: 8088

spring:
  application:
    name: rental-mcp-gateway
  cloud:
    nacos:
      discovery:
        server-addr: 43.143.163.102:8848
        namespace: develop
  mcp:
    server:
      enabled: true
      transport: sse
      endpoint: /mcp/sse
```

## 启动方式

```bash
# 编译项目
mvn clean compile -pl rental-mcp-gateway

# 启动服务
mvn spring-boot:run -pl rental-mcp-gateway
```

## API访问

MCP网关通过SSE方式提供服务，访问地址：
```
http://localhost:8088/mcp/sse
```

## 使用示例

商户可以通过AI Agent调用MCP工具，例如：

```json
{
  "tool_name": "create_store",
  "parameters": {
    "tenantId": 1,
    "storeName": "测试门店",
    "city": "北京",
    "address": "北京市朝阳区某某街道",
    "longitude": 116.397428,
    "latitude": 39.90923,
    "businessStartTime": "08:00:00",
    "businessEndTime": "22:00:00",
    "minAdvanceHours": 2,
    "maxAdvanceDays": 30,
    "serviceFee": 1000
  }
}
```
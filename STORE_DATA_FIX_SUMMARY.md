# 门店数据显示问题解决方案

## 问题分析

从浏览器开发者工具截图中可以看到：
1. **API正常返回数据**：后端API `/api/stores?current=1&size=10` 返回了门店数据
2. **数据结构不匹配**：后端返回的是分页格式，包含 `records` 数组，前端期望直接是数组
3. **字段名差异**：后端使用 `storeName`，前端期望 `name`

## 修复内容

### 1. API调用修复
- 修改API调用添加分页参数：`/stores?current=1&size=100`
- 处理分页响应格式：`response.data.records`

### 2. 数据解析修复
```typescript
// 检查响应数据结构
let storeData = []
if (response?.data) {
  // 如果是分页格式，取 records 字段
  if (response.data.records && Array.isArray(response.data.records)) {
    storeData = response.data.records
  }
  // 如果直接是数组格式
  else if (Array.isArray(response.data)) {
    storeData = response.data
  }
}
```

### 3. 表格列定义更新
- 使用后端实际字段名：`storeName`、`city`、`address`
- 处理状态显示：基于 `onlineStatus` 字段（1=上架，0=下架）
- 添加备用字段支持：`record.storeName || record.name`

### 4. 统计计算修复
```typescript
const activeStores = storeList.filter(store => 
  store.onlineStatus === 1 || store.status === 'active'
).length
```

## 测试步骤

1. **访问调试页面**：`http://localhost:3003/debug`
   - 查看原始API响应数据
   - 确认数据解析是否正确
   - 查看解析后的门店列表表格

2. **访问门店管理页面**：`http://localhost:3003/stores`
   - 确认门店数据正常显示
   - 检查统计数据是否正确
   - 测试新增、编辑、删除功能

## 数据字段映射

| 后端字段 | 前端字段 | 说明 |
|---------|---------|------|
| `storeName` | `name` | 门店名称 |
| `city` | `city` | 城市 |
| `address` | `address` | 地址 |
| `onlineStatus` | `status` | 状态（1=上架，0=下架）|
| `onlineStatusDesc` | - | 状态描述 |

现在数据应该能正常显示了！
# 高德地图集成指南

## 概述

本系统使用高德地图API实现电子围栏绘制功能，用于门店服务范围管理。

## 申请高德地图API密钥

1. 访问[高德开放平台](https://lbs.amap.com/)
2. 注册并登录账号
3. 进入控制台，创建新应用
4. 为应用添加WebService和JavaScript API服务
5. 获取API密钥

## 配置API密钥

### 开发环境配置

在 `rental-merchant-web/index.html` 文件中找到以下代码：

```html
<script type="text/javascript" src="https://webapi.amap.com/maps?v=1.4.15&key=YOUR_AMAP_KEY"></script>
```

将 `YOUR_AMAP_KEY` 替换为实际的API密钥。

### 生产环境配置

在生产环境中，建议使用环境变量来配置API密钥：

```html
<script type="text/javascript" src="https://webapi.amap.com/maps?v=1.4.15&key=<%= process.env.AMAP_KEY %>"></script>
```

## 功能说明

### 电子围栏绘制

1. 用户可以在地图上点击多个点来绘制多边形区域
2. 系统会自动保存坐标点数据
3. 支持编辑和删除已绘制的区域

### 坐标格式

坐标数据以JSON格式存储，格式如下：

```json
[
  [116.397428, 39.90923],
  [116.398428, 39.91023],
  [116.399428, 39.90923]
]
```

每个坐标点包含经度和纬度两个值。

## 注意事项

1. 请妥善保管API密钥，不要在客户端代码中暴露
2. 高德地图API有调用次数限制，请合理使用
3. 在生产环境中建议配置HTTPS以确保数据安全
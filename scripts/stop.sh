#!/bin/bash

# 租车SaaS系统停止脚本

echo "========================================="
echo "停止租车SaaS系统"
echo "========================================="

# 停止Java服务
echo "🛑 停止微服务..."
pkill -f "spring-boot:run"
pkill -f "rental-.*-service"

# 停止前端服务
echo "🛑 停止前端服务..."
pkill -f "vite"
pkill -f "npm run dev"

# 停止中间件
echo "🛑 停止中间件..."
cd docker
docker-compose down

# 返回项目根目录
cd ..

echo "========================================="
echo "✅ 租车SaaS系统已停止！"
echo "========================================="
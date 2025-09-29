#!/bin/bash

# 租车SaaS系统启动脚本
# 用于启动所有中间件和微服务

echo "========================================="
echo "启动租车SaaS系统"
echo "========================================="

# 检查Docker是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker未运行，请先启动Docker"
    exit 1
fi

# 启动中间件
echo "🚀 启动中间件..."
cd docker
docker-compose up -d

# 等待中间件启动完成
echo "⏳ 等待中间件启动完成..."
sleep 30

# 检查中间件状态
echo "🔍 检查中间件状态..."
docker-compose ps

# 返回项目根目录
cd ..

# 编译项目
echo "🔨 编译项目..."
mvn clean install -DskipTests

# 启动微服务
echo "🚀 启动微服务..."

# 启动API网关
echo "启动API网关..."
cd rental-gateway
nohup mvn spring-boot:run > ../logs/gateway.log 2>&1 &
cd ..

# 等待网关启动
sleep 10

# 启动用户服务
echo "启动用户服务..."
cd rental-user-service
nohup mvn spring-boot:run > ../logs/user-service.log 2>&1 &
cd ..

# 启动基础数据服务
echo "启动基础数据服务..."
cd rental-base-data-service
nohup mvn spring-boot:run > ../logs/base-data-service.log 2>&1 &
cd ..

# 启动商品服务
echo "启动商品服务..."
cd rental-product-service
nohup mvn spring-boot:run > ../logs/product-service.log 2>&1 &
cd ..

# 启动订单服务
echo "启动订单服务..."
cd rental-order-service
nohup mvn spring-boot:run > ../logs/order-service.log 2>&1 &
cd ..

# 启动支付服务
echo "启动支付服务..."
cd rental-payment-service
nohup mvn spring-boot:run > ../logs/payment-service.log 2>&1 &
cd ..

# 启动前端项目
echo "🚀 启动前端项目..."
cd rental-merchant-web
npm install
nohup npm run dev > ../logs/frontend.log 2>&1 &
cd ..

echo "========================================="
echo "✅ 租车SaaS系统启动完成！"
echo "========================================="
echo "🌐 服务访问地址："
echo "   前端应用: http://localhost:3000"
echo "   API网关: http://localhost:8090"
echo "   Nacos控制台: http://localhost:8848/nacos"
echo "   RocketMQ控制台: http://localhost:8080"
echo ""
echo "📋 服务端口分配："
echo "   API网关: 8090"
echo "   用户服务: 8081"
echo "   基础数据服务: 8082"
echo "   商品服务: 8083"
echo "   订单服务: 8084"
echo "   支付服务: 8085"
echo "   前端应用: 3000"
echo ""
echo "📝 日志文件位置: ./logs/"
echo "🛑 停止系统: ./scripts/stop.sh"
echo "========================================="
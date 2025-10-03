#!/bin/bash

# 订单服务启动脚本

# 检查Java环境
if ! command -v java &> /dev/null
then
    echo "未检测到Java环境，请先安装Java"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null
then
    echo "未检测到Maven环境，请先安装Maven"
    exit 1
fi

echo "开始编译订单服务..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "编译成功，开始启动订单服务..."
    nohup java -jar target/rental-order-service-1.0.0-SNAPSHOT.jar > order-service.log 2>&1 &
    echo "订单服务已启动，日志文件: order-service.log"
else
    echo "编译失败，请检查代码"
    exit 1
fi
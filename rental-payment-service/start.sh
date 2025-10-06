#!/bin/bash

# 启动支付服务脚本

# 设置应用名称和端口
APP_NAME="rental-payment-service"
APP_PORT=8098

# 检查是否已经运行
pid=`ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]; then
    echo "$APP_NAME is already running (pid: $pid)"
    exit 1
fi

# 进入脚本所在目录
cd "$(dirname "$0")"

# 启动应用
echo "Starting $APP_NAME..."
nohup ./mvnw spring-boot:run > logs/app.log 2>&1 &

# 等待几秒检查是否启动成功
sleep 5

# 检查启动状态
pid=`ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]; then
    echo "$APP_NAME started successfully (pid: $pid)"
    echo "Application is running on port $APP_PORT"
else
    echo "Failed to start $APP_NAME"
    exit 1
fi
#!/bin/bash

# 停止支付服务脚本

# 设置应用名称
APP_NAME="rental-payment-service"

# 查找进程ID
pid=`ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}'`

if [ -z "$pid" ]; then
    echo "$APP_NAME is not running"
    exit 1
fi

echo "Stopping $APP_NAME (pid: $pid)..."
kill $pid

# 等待几秒确保进程已停止
sleep 3

# 再次检查进程是否已停止
pid=`ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]; then
    echo "Failed to stop $APP_NAME, force killing..."
    kill -9 $pid
fi

echo "$APP_NAME stopped successfully"
#!/bin/bash

# AI Service 管理脚本
# 用法: ./start.sh [start|stop|restart|status]

# 配置
SERVICE_NAME="rental-ai-service"
PID_FILE="/tmp/rental-ai-service.pid"
LOG_FILE="/tmp/rental-ai-service.log"

# 检查参数
if [ $# -eq 0 ]; then
    ACTION="start"
else
    ACTION=$1
fi

case "$ACTION" in
    start)
        # 检查服务是否已在运行
        if [ -f "$PID_FILE" ]; then
            PID=$(cat "$PID_FILE")
            if ps -p "$PID" > /dev/null 2>&1; then
                echo "服务 $SERVICE_NAME 已在运行 (PID: $PID)"
                exit 1
            else
                # PID文件存在但进程不存在，删除PID文件
                rm -f "$PID_FILE"
            fi
        fi
        
        # 安装依赖
        echo "安装依赖..."
        pip3 install -r requirements.txt
        
        # 启动服务
        echo "启动 $SERVICE_NAME 服务..."
        nohup python3 main.py > "$LOG_FILE" 2>&1 &
        PID=$!
        echo "$PID" > "$PID_FILE"
        echo "$SERVICE_NAME 服务已启动 (PID: $PID)"
        ;;
        
    stop)
        # 停止服务
        if [ -f "$PID_FILE" ]; then
            PID=$(cat "$PID_FILE")
            if ps -p "$PID" > /dev/null 2>&1; then
                echo "停止 $SERVICE_NAME 服务 (PID: $PID)..."
                kill "$PID"
                rm -f "$PID_FILE"
                echo "$SERVICE_NAME 服务已停止"
            else
                echo "服务 $SERVICE_NAME 未在运行"
                rm -f "$PID_FILE"
            fi
        else
            echo "服务 $SERVICE_NAME 未在运行"
        fi
        ;;
        
    restart)
        # 重启服务
        echo "重启 $SERVICE_NAME 服务..."
        $0 stop
        sleep 2
        $0 start
        ;;
        
    status)
        # 检查服务状态
        if [ -f "$PID_FILE" ]; then
            PID=$(cat "$PID_FILE")
            if ps -p "$PID" > /dev/null 2>&1; then
                echo "$SERVICE_NAME 服务正在运行 (PID: $PID)"
            else
                echo "$SERVICE_NAME 服务未在运行"
                rm -f "$PID_FILE"
            fi
        else
            echo "$SERVICE_NAME 服务未在运行"
        fi
        ;;
        
    *)
        echo "用法: $0 [start|stop|restart|status]"
        exit 1
        ;;
esac

exit 0
#!/bin/bash

# 启动租车SaaS系统的AI对话模块
# 包括MCP网关和AI服务

echo "Starting Rental SaaS AI System..."

# 启动MCP网关
echo "Starting MCP Gateway..."
cd /Users/leiguoqiang/javaproj/rental/rental-mcp-gateway
mvn spring-boot:run > mcp-gateway.log 2>&1 &
MCP_GATEWAY_PID=$!
echo "MCP Gateway started with PID $MCP_GATEWAY_PID"

# 等待MCP网关启动
sleep 10

# 启动AI服务
echo "Starting AI Service..."
cd /Users/leiguoqiang/javaproj/rental/rental-ai-service
python3 src/main.py > ai-service.log 2>&1 &
AI_SERVICE_PID=$!
echo "AI Service started with PID $AI_SERVICE_PID"

# 等待AI服务启动
sleep 5

echo "Rental SaaS AI System started successfully!"
echo "MCP Gateway running on http://localhost:8088"
echo "AI Service running on http://localhost:8000"
echo "Press Ctrl+C to stop all services"

# 等待用户中断
trap "echo 'Stopping services...'; kill $MCP_GATEWAY_PID $AI_SERVICE_PID; exit" INT
wait
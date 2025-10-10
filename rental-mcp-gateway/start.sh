#!/bin/bash

# Rental MCP Gateway 启动脚本

echo "开始编译 Rental MCP Gateway..."

# 清理并编译项目
mvn clean compile -pl rental-mcp-gateway

if [ $? -eq 0 ]; then
    echo "编译成功，正在启动 Rental MCP Gateway..."
    mvn spring-boot:run -pl rental-mcp-gateway
else
    echo "编译失败，请检查代码！"
    exit 1
fi
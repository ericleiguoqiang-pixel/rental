#!/bin/bash

# 安装依赖
echo "Installing dependencies..."
pip install -r requirements.txt

# 启动服务
echo "Starting AI service..."
python src/main.py
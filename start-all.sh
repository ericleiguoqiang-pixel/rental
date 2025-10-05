#!/bin/bash

echo "Starting all Rental SaaS applications..."

# 启动用户端Web应用
echo "Starting User Web Application (rental-user-web) on port 3002..."
cd /Users/leiguoqiang/javaproj/rental/rental-user-web
npx vite &

# 启动运营MIS系统
echo "Starting Operation MIS (rental-operation-mis) on port 3001..."
cd /Users/leiguoqiang/javaproj/rental/rental-operation-mis
npx vite --port 3001 &

# 启动商户SaaS系统
echo "Starting Merchant SaaS (rental-merchant-web) on port 3000..."
cd /Users/leiguoqiang/javaproj/rental/rental-merchant-web
npx vite --port 3000 &

echo "All applications started!"
echo "User Web App: http://localhost:3002"
echo "Operation MIS: http://localhost:3001"
echo "Merchant SaaS: http://localhost:3000"
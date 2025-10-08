#!/bin/bash

# 编译顺序脚本，确保模块按正确顺序编译

echo "开始按顺序编译项目模块..."

# 1. 首先编译公共模块
echo "1. 编译 rental-common 模块..."
cd rental-common
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "rental-common 模块编译失败"
    exit 1
fi

cd ..

# 2. 编译API模块
echo "2. 编译 rental-api 模块..."
cd rental-api
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "rental-api 模块编译失败"
    exit 1
fi

cd ..

# 3. 编译其他服务模块
echo "3. 编译其他服务模块..."

# 定义需要编译的服务模块数组
modules=("rental-gateway" "rental-user-service" "rental-base-data-service" "rental-product-service" "rental-order-service" "rental-payment-service" "rental-pricing")

for module in "${modules[@]}"; do
    if [ -d "$module" ]; then
        echo "编译 $module 模块..."
        cd "$module"
        mvn clean install -DskipTests
        
        if [ $? -ne 0 ]; then
            echo "$module 模块编译失败"
            exit 1
        fi
        
        cd ..
    else
        echo "警告: 模块目录 $module 不存在"
    fi
done

echo "所有模块编译完成!"
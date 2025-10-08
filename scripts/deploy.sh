#!/bin/bash

# 部署脚本
# 用法: ./deploy.sh <module-name> 或 ./deploy.sh all
# 示例: ./deploy.sh rental-user-service 或 ./deploy.sh rental-user-web 或 ./deploy.sh all

set -e  # 遇到错误时退出

# 配置信息
SERVER_IP="43.143.163.102"
SSH_PORT="22"
SSH_USER="root"
SSH_KEY="/Users/leiguoqiang/.ssh/43.143.163.102.key"
BACKEND_TARGET_DIR="/www/server/java_project"
FRONTEND_TARGET_DIR="/www/wwwroot"

# 检查参数
if [ $# -ne 1 ]; then
    echo "用法: $0 <module-name> 或 $0 all"
    echo "示例: $0 rental-user-service 或 $0 rental-user-web 或 $0 all"
    exit 1
fi

MODULE_NAME=$1
echo "开始部署模块: $MODULE_NAME"

# 处理all参数的情况
if [ "$MODULE_NAME" == "all" ]; then
    echo "开始部署所有模块..."
    
    # 先编译基础模块: rental-common 和 rental-api
    echo "编译基础模块: rental-common 和 rental-api"
    cd rental-common
    mvn clean install -DskipTests
    if [ $? -ne 0 ]; then
        echo "rental-common 模块编译失败"
        exit 1
    fi
    cd ..
    
    cd rental-api
    mvn clean install -DskipTests
    if [ $? -ne 0 ]; then
        echo "rental-api 模块编译失败"
        exit 1
    fi
    cd ..
    
    # 编译所有后端服务模块
    BACKEND_MODULES=("rental-gateway" "rental-user-service" "rental-base-data-service" "rental-product-service" "rental-order-service" "rental-payment-service" "rental-pricing")
    for module in "${BACKEND_MODULES[@]}"; do
        if [ -d "$module" ]; then
            echo "开始构建后端模块: $module"
            cd "$module"
            mvn clean package -Dspring.profiles.active=prod -DskipTests
            
            if [ $? -ne 0 ]; then
                echo "后端模块 $module 构建失败"
                exit 1
            fi
            
            # 获取生成的JAR文件名
            JAR_FILE=$(find target -name "*.jar" | head -n 1)
            if [ -z "$JAR_FILE" ]; then
                echo "未找到模块 $module 生成的JAR文件"
                exit 1
            fi
            
            echo "后端模块 $module 构建完成: $JAR_FILE"
            
            # 传输后端JAR文件
            echo "传输后端模块 $module 到服务器..."
            scp -i "$SSH_KEY" -P "$SSH_PORT" "$JAR_FILE" "$SSH_USER@$SERVER_IP:$BACKEND_TARGET_DIR/"
            
            if [ $? -ne 0 ]; then
                echo "后端模块 $module 文件传输失败"
                exit 1
            fi
            
            echo "后端模块 $module 文件传输完成"
            
            # 重启后端服务
            echo "重启后端模块 $module 服务..."
            MODULE_JAR=$(basename "$JAR_FILE")
            ssh -i "$SSH_KEY" -p "$SSH_PORT" "$SSH_USER@$SERVER_IP" \
                "cd $BACKEND_TARGET_DIR && ./start.sh ${module}"
            
            if [ $? -ne 0 ]; then
                echo "后端模块 $module 服务重启失败"
                exit 1
            fi
            
            echo "后端模块 $module 服务重启完成"
            cd ..
        else
            echo "警告: 后端模块 $module 不存在，跳过部署"
        fi
    done
    
    # 构建所有前端项目
    FRONTEND_MODULES=("rental-user-web" "rental-merchant-web" "rental-operation-mis")
    for module in "${FRONTEND_MODULES[@]}"; do
        if [ -d "$module" ]; then
            echo "开始构建前端项目: $module"
            cd "$module"
            npm run build
            
            if [ $? -ne 0 ]; then
                echo "前端项目 $module 构建失败"
                exit 1
            fi
            
            if [ ! -d "dist" ]; then
                echo "未找到前端项目 $module 的构建输出目录 dist"
                exit 1
            fi
            
            echo "前端项目 $module 构建完成"
            
            # 传输前端构建文件
            echo "传输前端项目 $module 到服务器..."
            ssh -i "$SSH_KEY" -p "$SSH_PORT" "$SSH_USER@$SERVER_IP" "mkdir -p $FRONTEND_TARGET_DIR/$module"
            scp -i "$SSH_KEY" -P "$SSH_PORT" -r "dist/"* "$SSH_USER@$SERVER_IP:$FRONTEND_TARGET_DIR/$module/"
            
            if [ $? -ne 0 ]; then
                echo "前端项目 $module 文件传输失败"
                exit 1
            fi
            
            echo "前端项目 $module 文件传输完成"
            cd ..
        else
            echo "警告: 前端项目 $module 不存在，跳过部署"
        fi
    done
    
    echo "所有模块部署完成!"
    exit 0
fi

# 检查单个模块是否存在
if [ ! -d "$MODULE_NAME" ]; then
    echo "错误: 模块目录 '$MODULE_NAME' 不存在"
    exit 1
fi

# 判断是前端还是后端项目
if [[ "$MODULE_NAME" == *"-web" ]] || [[ "$MODULE_NAME" == *"-mis" ]]; then
    PROJECT_TYPE="frontend"
    echo "识别为前端项目"
else
    PROJECT_TYPE="backend"
    echo "识别为后端项目"
fi

# 构建项目
if [ "$PROJECT_TYPE" == "backend" ]; then
    echo "开始构建后端项目..."
    cd "$MODULE_NAME"
    mvn clean package -Dspring.profiles.active=prod -DskipTests
    
    if [ $? -ne 0 ]; then
        echo "后端项目构建失败"
        exit 1
    fi
    
    # 获取生成的JAR文件名
    JAR_FILE=$(find target -name "*.jar" | head -n 1)
    if [ -z "$JAR_FILE" ]; then
        echo "未找到生成的JAR文件"
        exit 1
    fi
    
    echo "后端项目构建完成: $JAR_FILE"
    cd ..
elif [ "$PROJECT_TYPE" == "frontend" ]; then
    echo "开始构建前端项目..."
    cd "$MODULE_NAME"
    npm run build
    
    if [ $? -ne 0 ]; then
        echo "前端项目构建失败"
        exit 1
    fi
    
    if [ ! -d "dist" ]; then
        echo "未找到构建输出目录 dist"
        exit 1
    fi
    
    echo "前端项目构建完成"
    cd ..
fi

# 传输文件到服务器
echo "开始传输文件到服务器..."

if [ "$PROJECT_TYPE" == "backend" ]; then
    # 传输后端JAR文件
    scp -i "$SSH_KEY" -P "$SSH_PORT" "$MODULE_NAME/$JAR_FILE" "$SSH_USER@$SERVER_IP:$BACKEND_TARGET_DIR/"
    
    if [ $? -ne 0 ]; then
        echo "后端文件传输失败"
        exit 1
    fi
    
    echo "后端文件传输完成"
    
    # 重启后端服务
    echo "重启后端服务..."
    MODULE_JAR=$(basename "$JAR_FILE")
    ssh -i "$SSH_KEY" -p "$SSH_PORT" "$SSH_USER@$SERVER_IP" \
        "cd $BACKEND_TARGET_DIR && ./start.sh ${MODULE_NAME}"
    
    if [ $? -ne 0 ]; then
        echo "后端服务重启失败"
        exit 1
    fi
    
    echo "后端服务重启完成"
elif [ "$PROJECT_TYPE" == "frontend" ]; then
    # 传输前端构建文件
    ssh -i "$SSH_KEY" -p "$SSH_PORT" "$SSH_USER@$SERVER_IP" "mkdir -p $FRONTEND_TARGET_DIR/$MODULE_NAME"
    scp -i "$SSH_KEY" -P "$SSH_PORT" -r "$MODULE_NAME/dist/"* "$SSH_USER@$SERVER_IP:$FRONTEND_TARGET_DIR/$MODULE_NAME/"
    
    if [ $? -ne 0 ]; then
        echo "前端文件传输失败"
        exit 1
    fi
    
    echo "前端文件传输完成"
    
    # 重启前端服务（如果需要）
    echo "前端项目部署完成，可能需要重启Web服务器"
fi

echo "模块 $MODULE_NAME 部署完成!"
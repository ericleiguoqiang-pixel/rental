# Rental AI Service

基于Langchain和DeepSeek的AI对话服务，为租车SaaS系统提供智能助手功能。

## 功能特性

- 集成DeepSeek大语言模型
- 通过MCP协议与后端服务通信
- 支持门店和车辆管理的自然语言操作
- 提供RESTful API接口
- 使用langchain_mcp_adapters库集成MCP功能

## 技术栈

- Python 3.11
- FastAPI
- Langchain 0.3.27
- DeepSeek API

## 环境变量

```bash
DEEPSEEK_API_KEY=your_deepseek_api_key
MCP_GATEWAY_URL=http://rental-mcp-gateway:8088
PORT=8000
TENANT_ID=1
```

## 安装依赖

```bash
pip install -r requirements.txt
```

## 运行服务

```bash
python src/main.py
```

或者使用Docker:

```bash
docker build -t rental-ai-service .
docker run -p 8000:8000 rental-ai-service
```
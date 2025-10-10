# MCP适配器使用指南

## 概述

本指南介绍了如何使用MCP适配器将Langchain Agent与租车SaaS系统的MCP网关集成。

## 架构设计

```
+----------------+     +-----------------+     +------------------+
|   Langchain    |     |  MCP Adapter    |     |   MCP Gateway    |
|     Agent      |<--->|   (Python)      |<--->|    (Java)        |
+----------------+     +-----------------+     +------------------+
                              |
                              v
                       +-----------------+
                       |  MCP Client     |
                       |  (Python)       |
                       +-----------------+
```

## 使用方法

### 1. 初始化适配器

```python
from mcp_adapter import MCPAdapter

# 创建MCP适配器实例
mcp_adapter = MCPAdapter(
    mcp_server_url="http://localhost:8088",
    tenant_id=1
)
```

### 2. 获取工具

```python
# 获取所有MCP工具
tools = mcp_adapter.get_tools()
```

### 3. 创建Agent

```python
from langchain.agents import AgentExecutor, create_tool_calling_agent
from langchain_core.prompts import ChatPromptTemplate
from langchain_openai import ChatOpenAI

# 初始化大语言模型
llm = ChatOpenAI(
    model="deepseek-chat",
    api_key="your-api-key",
    base_url="https://api.deepseek.com/v1"
)

# 创建提示模板
prompt = ChatPromptTemplate.from_messages([
    ("system", "你是一个租车SaaS系统的AI助手..."),
    ("placeholder", "{chat_history}"),
    ("human", "{input}"),
    ("placeholder", "{agent_scratchpad}"),
])

# 创建Agent
agent = create_tool_calling_agent(llm, tools, prompt)

# 创建Agent执行器
agent_executor = AgentExecutor(agent=agent, tools=tools, verbose=True)
```

### 4. 执行Agent

```python
# 执行Agent
result = agent_executor.invoke({
    "input": "帮我创建一个新门店",
    "chat_history": [],
    "tenant_id": 1
})
```

## 支持的工具

### 门店管理工具
1. `create_store` - 创建新门店
2. `update_store` - 更新门店信息
3. `get_store_detail` - 查询门店详情
4. `get_all_stores` - 查询所有门店
5. `online_store` - 门店上架
6. `offline_store` - 门店下架

### 车辆管理工具
1. `create_vehicle` - 创建新车辆
2. `update_vehicle` - 更新车辆信息
3. `delete_vehicle` - 删除车辆
4. `get_vehicle_detail` - 查询车辆详情
5. `get_vehicle_list` - 查询车辆列表

## 自定义适配器

如果需要扩展适配器功能，可以继承MCPAdapter类：

```python
from mcp_adapter import MCPAdapter

class CustomMCPAdapter(MCPAdapter):
    def get_custom_tools(self):
        # 添加自定义工具
        tools = super().get_tools()
        tools.append(self._custom_tool())
        return tools
    
    def _custom_tool(self):
        def custom_function(**kwargs):
            # 自定义逻辑
            pass
        custom_function.__name__ = "custom_tool"
        custom_function.__doc__ = "自定义工具说明"
        return custom_function
```
import json
from langchain.agents import AgentExecutor, create_tool_calling_agent
from langchain_core.prompts import ChatPromptTemplate
from langchain.chat_models import init_chat_model
# from mcp_adapter import MCPAdapter
from langchain_mcp_adapters.client import MultiServerMCPClient
from typing import Dict, Any, List
import os
from dotenv import load_dotenv

load_dotenv(override = True)

# 初始化大语言模型
# 使用DeepSeek模型，API密钥可以从环境变量获取
llm = init_chat_model(
    model="deepseek-chat",
    api_key=os.getenv("DEEPSEEK_API_KEY")
)

# 初始化MCP适配器
# mcp_adapter = MCPAdapter(
#     mcp_server_url=os.getenv("MCP_GATEWAY_URL", "http://localhost:8088/mcp/sse"),
#     tenant_id=int(os.getenv("TENANT_ID", 1))
# )


def load_servers(file_path: str = "/Users/leiguoqiang/javaproj/rental/rental-ai-service/server_config.json") -> Dict[str, Any]:
    with open(file_path, "r", encoding="utf-8") as f:
        return json.load(f).get("mcpServers", {})

async def get_agent() -> AgentExecutor:

    mcp = MultiServerMCPClient(load_servers())

    # 获取MCP工具
    #tools = mcp_adapter.get_tools()
    tools = await mcp.get_tools()

    # 创建提示模板
    prompt = ChatPromptTemplate.from_messages([
        ("system", "你是一个租车SaaS系统的AI助手，可以帮助商户管理门店和车辆。你可以执行查询、创建、更新、删除等操作。对于修改类操作，请提醒用户确认后再执行。"),
        ("placeholder", "{chat_history}"),
        ("human", "{input}，我的租户ID是：{tenant_id}"),
        ("placeholder", "{agent_scratchpad}"),
    ])

    # 创建Agent
    agent = create_tool_calling_agent(llm, tools, prompt)

    # 创建Agent执行器
    return AgentExecutor(agent=agent, tools=tools, verbose=True, handle_parsing_errors=True)

async def process_chat_message(tenant_id: int, messages: List[Dict[str, str]]) -> Dict[str, str]:
    """处理聊天消息"""
    # 构建聊天历史
    chat_history = []
    for msg in messages[:-1]:  # 排除最后一条用户消息
        if msg["role"] == "user":
            chat_history.append(("human", msg["content"]))
        elif msg["role"] == "assistant":
            chat_history.append(("ai", msg["content"]))
    
    # 获取用户最新消息
    user_input = messages[-1]["content"]

    agent_executor = await get_agent()
    # 执行Agent
    result = await agent_executor.ainvoke({
        "input": user_input,
        "chat_history": chat_history,
        "tenant_id": tenant_id
    })
    
    return {
        "role": "assistant",
        "content": result["output"]
    }
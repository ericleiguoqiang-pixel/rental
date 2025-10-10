import json
import re
from langchain.agents import AgentExecutor, create_tool_calling_agent
from langchain_core.prompts import ChatPromptTemplate
from langchain.chat_models import init_chat_model
from langchain_mcp_adapters.client import MultiServerMCPClient
from typing import Dict, Any, List
import os
from dotenv import load_dotenv

load_dotenv(override = True)

# 初始化大语言模型
llm = init_chat_model(
    model="deepseek-chat",
    api_key=os.getenv("DEEPSEEK_API_KEY")
)

def load_servers(file_path: str = "./server_config.json") -> Dict[str, Any]:
    with open(file_path, "r", encoding="utf-8") as f:
        return json.load(f).get("mcpServers", {})

async def get_agent() -> AgentExecutor:
    mcp = MultiServerMCPClient(load_servers())

    # 获取MCP工具
    tools = await mcp.get_tools()

    # 创建提示模板
    prompt = ChatPromptTemplate.from_messages([
        ("system", """你是一个租车SaaS系统的AI助手，可以帮助商户管理门店和车辆。
你可以执行查询、创建、更新、删除等操作。
对于修改类操作（创建、更新、删除），请使用以下格式提醒用户确认：
[CONFIRMATION_REQUIRED]
操作类型: action_type
操作描述: action_description
确认信息: 请确认是否执行此操作
[/CONFIRMATION_REQUIRED]

例如：
[CONFIRMATION_REQUIRED]
操作类型: 创建门店
操作描述: 创建名为"测试门店"的新门店
确认信息: 此操作将创建一个新的门店，请确认是否继续
[/CONFIRMATION_REQUIRED]"""),
        ("placeholder", "{chat_history}"),
        ("human", "{input}，我的租户ID是：{tenant_id}"),
        ("placeholder", "{agent_scratchpad}"),
    ])

    # 创建Agent
    agent = create_tool_calling_agent(llm, tools, prompt)

    # 创建Agent执行器
    return AgentExecutor(agent=agent, tools=tools, verbose=True, handle_parsing_errors=True)

async def process_chat_message(tenant_id: int, messages: List[Dict[str, str]], confirmed: bool = False) -> Dict[str, str]:
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

    # 如果是确认请求，直接执行操作
    if confirmed:
        agent_executor = await get_agent()
        # 执行Agent
        result = await agent_executor.ainvoke({
            "input": user_input + " (用户已确认此操作)",
            "chat_history": chat_history,
            "tenant_id": tenant_id
        })
        
        return {
            "role": "assistant",
            "content": "✅ 操作已成功执行！\n\n" + result["output"]
        }
    
    agent_executor = await get_agent()
    # 执行Agent
    result = await agent_executor.ainvoke({
        "input": user_input,
        "chat_history": chat_history,
        "tenant_id": tenant_id
    })
    
    # 检查是否需要确认
    output = result["output"]
    confirmation_pattern = r'\[CONFIRMATION_REQUIRED\](.*?)\[/CONFIRMATION_REQUIRED\]'
    confirmation_match = re.search(confirmation_pattern, output, re.DOTALL)
    
    if confirmation_match:
        confirmation_content = confirmation_match.group(1).strip()
        # 提取操作类型和描述
        action_type = ""
        action_description = ""
        confirm_message = ""
        
        for line in confirmation_content.split('\n'):
            if line.startswith('操作类型:'):
                action_type = line.replace('操作类型:', '').strip()
            elif line.startswith('操作描述:'):
                action_description = line.replace('操作描述:', '').strip()
            elif line.startswith('确认信息:'):
                confirm_message = line.replace('确认信息:', '').strip()
        
        # 返回确认请求
        return {
            "role": "assistant",
            "content": f"⚠️ **操作确认**\n\n{confirm_message}\n\n操作详情：{action_description}",
            "requires_confirmation": True,
            "action_type": action_type,
            "action_description": action_description
        }
    else:
        return {
            "role": "assistant",
            "content": output
        }
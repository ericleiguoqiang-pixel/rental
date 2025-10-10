#!/usr/bin/env python3
"""
AI服务测试脚本
用于验证AI服务是否能正常启动和响应请求
"""

import requests
import time
import os

def test_health_check():
    """测试健康检查接口"""
    try:
        response = requests.get("http://localhost:8000/health")
        if response.status_code == 200:
            print("✓ 健康检查接口正常")
            return True
        else:
            print(f"✗ 健康检查接口异常: {response.status_code}")
            return False
    except Exception as e:
        print(f"✗ 无法连接到AI服务: {e}")
        return False

def test_chat():
    """测试聊天接口"""
    try:
        # 测试消息
        test_messages = [
            {
                "role": "user",
                "content": "你好，帮我创建一个门店"
            }
        ]
        
        payload = {
            "messages": test_messages,
            "tenant_id": 1
        }
        
        response = requests.post(
            "http://localhost:8000/chat",
            json=payload,
            headers={"Content-Type": "application/json"}
        )
        
        if response.status_code == 200:
            result = response.json()
            print("✓ 聊天接口正常")
            print(f"  AI回复: {result['content']}")
            return True
        else:
            print(f"✗ 聊天接口异常: {response.status_code}")
            print(f"  错误信息: {response.text}")
            return False
    except Exception as e:
        print(f"✗ 聊天接口测试失败: {e}")
        return False

def test_mcp_adapter():
    """测试MCP适配器"""
    try:
        # 导入MCP适配器
        from src.mcp_adapter import MCPAdapter
        
        # 创建适配器实例
        adapter = MCPAdapter(
            mcp_server_url=os.getenv("MCP_GATEWAY_URL", "http://localhost:8088"),
            tenant_id=int(os.getenv("TENANT_ID", 1))
        )
        
        # 获取工具
        tools = adapter.get_tools()
        
        if len(tools) > 0:
            print("✓ MCP适配器正常")
            print(f"  已加载 {len(tools)} 个工具")
            return True
        else:
            print("✗ MCP适配器未加载任何工具")
            return False
    except Exception as e:
        print(f"✗ MCP适配器测试失败: {e}")
        return False

def main():
    """主函数"""
    print("开始测试AI服务...")
    print("=" * 30)
    
    # 等待服务启动
    print("等待服务启动...")
    time.sleep(5)
    
    # 测试健康检查
    health_ok = test_health_check()
    
    # 测试MCP适配器
    adapter_ok = False
    if health_ok:
        adapter_ok = test_mcp_adapter()
    
    # 测试聊天接口
    chat_ok = False
    if health_ok and adapter_ok:
        chat_ok = test_chat()
    
    print("=" * 30)
    if health_ok and adapter_ok and chat_ok:
        print("✓ 所有测试通过，AI服务正常运行")
        return 0
    else:
        print("✗ 部分测试失败，请检查服务配置")
        return 1

if __name__ == "__main__":
    exit(main())
# 交互式确认功能说明

## 功能概述

AI助手现在支持交互式确认功能，对于敏感操作（创建、更新、删除），系统会显示确认按钮而不是纯文本确认。

## 实现原理

### 1. 后端处理
- AI Agent识别敏感操作
- 返回结构化的确认请求，包含操作类型和描述
- 前端解析响应并显示确认按钮
- 用户确认后，前端发送确认请求给后端执行实际操作

### 2. 前端展示
- 检测到确认请求时显示"确认"和"取消"按钮
- 用户点击确认后执行相应操作
- 提供Popconfirm弹窗进行二次确认

### 3. 确认流程
- 用户请求执行敏感操作
- AI助手识别操作并返回确认请求
- 前端显示操作详情和确认按钮
- 用户点击"确认执行"按钮
- 系统显示Popconfirm弹窗进行二次确认
- 用户确认后，前端发送确认请求给后端
- 后端执行实际操作并返回结果

## 敏感操作类型

### 门店管理
- 创建门店 (create_store)
- 更新门店 (update_store)
- 删除门店 (delete_store)
- 门店上架 (online_store)
- 门店下架 (offline_store)

### 车辆管理
- 创建车辆 (create_vehicle)
- 更新车辆 (update_vehicle)
- 删除车辆 (delete_vehicle)
- 车辆上架 (online_vehicle)
- 车辆下架 (offline_vehicle)

## 用户体验

### 确认流程
1. 用户请求执行敏感操作
2. AI助手识别操作并返回确认请求
3. 前端显示操作详情和确认按钮
4. 用户点击"确认执行"按钮
5. 系统显示Popconfirm弹窗进行二次确认
6. 用户确认后，前端发送确认请求给后端执行实际操作
7. 后端执行操作并返回结果

### 界面元素
- ⚠️ 警告图标：标识需要确认的操作
- ✅ 确认按钮：执行操作
- ❌ 取消按钮：取消操作
- 弹窗确认：防止误操作

## 安全规范

根据[敏感操作安全规范](#)，所有新增、修改、删除类的数据变更操作都必须经过商户二次确认后才能执行。

## 代码示例

### 后端响应格式
```json
{
  "role": "assistant",
  "content": "⚠️ **操作确认**\n\n此操作将创建一个新的门店，请确认是否继续\n\n操作详情：创建名为\"测试门店\"的新门店",
  "requires_confirmation": true,
  "action_type": "创建门店",
  "action_description": "创建名为\"测试门店\"的新门店"
}
```

### 前端处理逻辑
```typescript
// 检查是否需要确认
if (response.requires_confirmation) {
  const confirmMessage: ConfirmationMessage = {
    role: 'assistant',
    type: 'confirmation',
    content: response.content,
    actionType: response.action_type || '',
    actionDescription: response.action_description || '',
    originalResponse: response, // 保存原始响应用于确认时重新发送
    timestamp: new Date()
  };
  setMessages(prev => [...prev, confirmMessage]);
}

// 确认操作处理
const handleConfirmAction = async (originalResponse: any) => {
  // 发送确认请求给后端执行实际操作
  const confirmRequest = {
    messages: [...messages, confirmMessage].map(msg => ({
      role: msg.role,
      content: msg.content
    })),
    tenant_id: getTenantId(),
    confirmed: true, // 添加确认标志
    original_response: originalResponse
  };

  const response = await aiAPI.chat(confirmRequest);
  // 处理响应...
}
```

### 后端确认处理
```python
async def process_chat_message(tenant_id: int, messages: List[Dict[str, str]], confirmed: bool = False) -> Dict[str, str]:
    """处理聊天消息"""
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
    
    # 正常处理流程...
```
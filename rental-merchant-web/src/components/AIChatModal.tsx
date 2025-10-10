import React, { useState, useRef, useEffect } from 'react';
import { Modal, Button, Input, List, Avatar, Typography, Space, message, Popconfirm } from 'antd';
import { SendOutlined, RobotOutlined, UserOutlined, CheckOutlined, CloseOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { aiAPI } from '../services/api';

const { TextArea } = Input;
const { Text } = Typography;

interface BaseMessage {
  role: 'user' | 'assistant';
  timestamp: Date;
}

interface TextMessage extends BaseMessage {
  type: 'text';
  content: string;
}

interface ConfirmationMessage extends BaseMessage {
  type: 'confirmation';
  content: string;
  actionType: string;
  actionDescription: string;
  // 存储原始的AI响应，用于后续确认时重新发送请求
  originalResponse: any;
}

type Message = TextMessage | ConfirmationMessage;

interface AIChatModalProps {
  visible: boolean;
  onClose: () => void;
}

const AIChatModal: React.FC<AIChatModalProps> = ({ visible, onClose }) => {
  const [messages, setMessages] = useState<Message[]>([
    {
      role: 'assistant',
      type: 'text',
      content: '您好！我是您的AI助手，可以帮助您管理门店和车辆。请问有什么我可以帮您的吗？',
      timestamp: new Date()
    }
  ]);
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // 滚动到最新消息
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // 获取租户ID
  const getTenantId = () => {
    try {
      const userInfoStr = localStorage.getItem('userInfo');
      if (userInfoStr) {
        const userInfo = JSON.parse(userInfoStr);
        return userInfo.tenantId || 1;
      }
    } catch (e) {
      console.warn('解析用户信息失败:', e);
    }
    return 1; // 默认租户ID
  };

  const handleSend = async () => {
    if (!inputValue.trim() || loading) return;

    // 添加用户消息
    const userMessage: TextMessage = {
      role: 'user',
      type: 'text',
      content: inputValue,
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setInputValue('');
    setLoading(true);

    try {
      // 调用AI服务API
      const response = await aiAPI.chat({
        messages: [...messages, userMessage].map(msg => ({
          role: msg.role,
          content: msg.content
        })),
        tenant_id: getTenantId()
      });

      // 检查是否需要确认
      if (response.requires_confirmation) {
        const confirmMessage: ConfirmationMessage = {
          role: 'assistant',
          type: 'confirmation',
          content: response.content,
          actionType: response.action_type || '',
          actionDescription: response.action_description || '',
          originalResponse: response, // 保存原始响应
          timestamp: new Date()
        };
        setMessages(prev => [...prev, confirmMessage]);
      } else {
        // 添加AI回复
        const aiMessage: TextMessage = {
          role: 'assistant',
          type: 'text',
          content: response.content,
          timestamp: new Date()
        };
        setMessages(prev => [...prev, aiMessage]);
      }
    } catch (error: any) {
      console.error('发送消息失败:', error);
      message.error('发送消息失败: ' + (error.message || '未知错误'));
      const errorMessage: TextMessage = {
        role: 'assistant',
        type: 'text',
        content: '抱歉，我遇到了一些问题。请稍后再试。',
        timestamp: new Date()
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmAction = async (originalResponse: any) => {
    // 添加用户确认消息
    const confirmMessage: TextMessage = {
      role: 'user',
      type: 'text',
      content: '确认执行操作',
      timestamp: new Date()
    };

    setMessages(prev => [...prev, confirmMessage]);
    setLoading(true);

    try {
      // 发送确认请求给后端，执行实际操作
      // 这里需要调用后端API来执行确认的操作
      // 我们可以通过在原始消息中添加一个确认标志来实现
      const confirmRequest = {
        messages: [...messages, confirmMessage].map(msg => ({
          role: msg.role,
          content: msg.content
        })),
        tenant_id: getTenantId(),
        confirmed: true, // 添加确认标志
        original_response: originalResponse // 传递原始响应
      };

      const response = await aiAPI.chat(confirmRequest);

      // 添加AI回复
      const aiMessage: TextMessage = {
        role: 'assistant',
        type: 'text',
        content: response.content,
        timestamp: new Date()
      };

      setMessages(prev => [...prev, aiMessage]);
    } catch (error: any) {
      console.error('执行操作失败:', error);
      message.error('执行操作失败: ' + (error.message || '未知错误'));
      const errorMessage: TextMessage = {
        role: 'assistant',
        type: 'text',
        content: '❌ 抱歉，执行操作时遇到了问题。请稍后再试。',
        timestamp: new Date()
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const renderMessageContent = (message: Message) => {
    if (message.type === 'confirmation') {
      return (
        <div>
          <div style={{ display: 'flex', alignItems: 'flex-start', gap: '8px' }}>
            <ExclamationCircleOutlined style={{ color: '#faad14', fontSize: '16px', marginTop: '2px' }} />
            <Text>{message.content}</Text>
          </div>
          <div style={{ marginTop: '12px', display: 'flex', gap: '8px' }}>
            <Popconfirm
              title="确认执行此操作？"
              description="此操作将修改您的数据，请确认是否继续"
              onConfirm={() => handleConfirmAction(message.originalResponse)}
              okText="确认"
              cancelText="取消"
            >
              <Button type="primary" size="small" icon={<CheckOutlined />}>
                确认执行
              </Button>
            </Popconfirm>
            <Button size="small" icon={<CloseOutlined />}>
              取消
            </Button>
          </div>
        </div>
      );
    }
    return <Text>{message.content}</Text>;
  };

  return (
    <Modal
      title={
        <Space>
          <RobotOutlined />
          <span>AI助手</span>
        </Space>
      }
      open={visible}
      onCancel={onClose}
      footer={null}
      width={600}
      styles={{
        body: { padding: 0 }
      }}
    >
      <div style={{ display: 'flex', flexDirection: 'column', height: '500px' }}>
        {/* 消息区域 */}
        <div style={{ 
          flex: 1, 
          overflowY: 'auto', 
          padding: '16px',
          backgroundColor: '#f5f5f5'
        }}>
          <List
            dataSource={messages}
            renderItem={(message) => (
              <List.Item style={{ 
                border: 'none', 
                padding: '8px 0',
                justifyContent: message.role === 'user' ? 'flex-end' : 'flex-start'
              }}>
                <div style={{ 
                  display: 'flex', 
                  alignItems: 'flex-start',
                  maxWidth: '80%',
                  flexDirection: message.role === 'user' ? 'row-reverse' : 'row'
                }}>
                  <Avatar 
                    icon={message.role === 'user' ? <UserOutlined /> : <RobotOutlined />} 
                    style={{ 
                      backgroundColor: message.role === 'user' ? '#1890ff' : '#52c41a',
                      margin: message.role === 'user' ? '0 0 0 12px' : '0 12px 0 0'
                    }} 
                  />
                  <div style={{ 
                    backgroundColor: message.role === 'user' ? '#e6f7ff' : '#ffffff',
                    padding: '12px',
                    borderRadius: '8px',
                    boxShadow: '0 1px 2px rgba(0,0,0,0.1)'
                  }}>
                    {renderMessageContent(message)}
                  </div>
                </div>
              </List.Item>
            )}
          />
          <div ref={messagesEndRef} />
        </div>
        
        {/* 输入区域 */}
        <div style={{ 
          padding: '16px', 
          borderTop: '1px solid #f0f0f0',
          backgroundColor: '#ffffff'
        }}>
          <div style={{ display: 'flex', alignItems: 'flex-end', gap: '8px' }}>
            <TextArea
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              onPressEnter={handleKeyPress}
              placeholder="输入您的问题..."
              autoSize={{ minRows: 2, maxRows: 4 }}
              disabled={loading}
            />
            <Button 
              type="primary" 
              icon={<SendOutlined />} 
              onClick={handleSend}
              loading={loading}
              disabled={!inputValue.trim()}
            >
              发送
            </Button>
          </div>
          <Text type="secondary" style={{ fontSize: '12px', marginTop: '8px', display: 'block' }}>
            AI助手可以帮助您管理门店和车辆，对于修改操作会要求您确认后执行
          </Text>
        </div>
      </div>
    </Modal>
  );
};

export default AIChatModal;
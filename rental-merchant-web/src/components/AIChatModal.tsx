import React, { useState, useRef, useEffect } from 'react';
import { Modal, Button, Input, List, Avatar, Typography, Space, message } from 'antd';
import { SendOutlined, RobotOutlined, UserOutlined } from '@ant-design/icons';
import { aiAPI } from '../services/api';

const { TextArea } = Input;
const { Text } = Typography;

interface Message {
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
}

interface AIChatModalProps {
  visible: boolean;
  onClose: () => void;
}

const AIChatModal: React.FC<AIChatModalProps> = ({ visible, onClose }) => {
  const [messages, setMessages] = useState<Message[]>([
    {
      role: 'assistant',
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
    const userMessage: Message = {
      role: 'user',
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

      // 添加AI回复
      const aiMessage: Message = {
        role: 'assistant',
        content: response.content,  // 直接使用response.content
        timestamp: new Date()
      };

      setMessages(prev => [...prev, aiMessage]);
    } catch (error: any) {
      console.error('发送消息失败:', error);
      message.error('发送消息失败: ' + (error.message || '未知错误'));
      const errorMessage: Message = {
        role: 'assistant',
        content: '抱歉，我遇到了一些问题。请稍后再试。',
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
                    <Text>{message.content}</Text>
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
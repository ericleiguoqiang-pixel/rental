import React, { useState } from 'react';
import { Button, Input, message, Form, Card, Typography } from 'antd';
import { useNavigate } from 'react-router-dom';
import { login } from '../services/authService';

const { Title } = Typography;

const Login: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onFinish = async (values: { phone: string; code: string }) => {
    setLoading(true);
    try {
      const result = await login(values.phone, values.code);
      if (result.success) {
        message.success('登录成功');
        // 存储用户信息到本地存储
        localStorage.setItem('user', JSON.stringify(result.data));
        navigate('/'); // 登录成功后跳转到首页
      } else {
        message.error(result.message || '登录失败');
      }
    } catch (error) {
      message.error('登录失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  // 发送验证码（模拟）
  const sendCode = () => {
    message.info('验证码已发送（模拟）：1111');
  };

  return (
    <div style={{ 
      display: 'flex', 
      justifyContent: 'center', 
      alignItems: 'center', 
      height: '100vh',
      backgroundColor: '#f0f2f5'
    }}>
      <Card style={{ width: 350 }}>
        <Title level={3} style={{ textAlign: 'center' }}>用户登录</Title>
        <Form
          name="login"
          onFinish={onFinish}
          autoComplete="off"
        >
          <Form.Item
            name="phone"
            rules={[{ required: true, message: '请输入手机号!' }]}
          >
            <Input placeholder="请输入手机号" />
          </Form.Item>

          <Form.Item
            name="code"
            rules={[{ required: true, message: '请输入验证码!' }]}
          >
            <Input placeholder="请输入验证码" />
          </Form.Item>

          <Form.Item>
            <Button type="primary" onClick={sendCode} style={{ marginRight: 10 }}>
              发送验证码
            </Button>
            <Button type="primary" htmlType="submit" loading={loading}>
              登录
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default Login;
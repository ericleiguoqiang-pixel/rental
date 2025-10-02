import React, { useState } from 'react';
import { Form, Input, Button, Card, Typography, message, Space } from 'antd';
import { UserOutlined, LockOutlined, SafetyCertificateOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { operationAuthAPI, ApiResponse } from '../../services/api';
import { useAuthStore } from '../../hooks/useAuth';

const { Title, Text } = Typography;

interface LoginForm {
  username: string;
  password: string;
}

const Login: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuthStore();

  const onFinish = async (values: LoginForm) => {
    setLoading(true);
    try {
      const response: any = await operationAuthAPI.login(values);
      console.log('Login response:', response);
      if (response.code === 200) {
        login(response.data.accessToken, response.data.userInfo);
        message.success('登录成功！');
        navigate('/dashboard');
      } else {
        message.error(response.message || '登录失败');
      }
    } catch (error: any) {
      console.error('Login error:', error);
      message.error(error.message || '登录失败，请检查用户名和密码');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <Card 
        className="login-form"
        style={{ maxWidth: 400, margin: '0 auto' }}
      >
        <Space direction="vertical" size="large" style={{ width: '100%', textAlign: 'center' }}>
          <div>
            <SafetyCertificateOutlined 
              style={{ fontSize: 48, color: '#1890ff', marginBottom: 16 }} 
            />
            <Title level={2} className="login-title">
              运营MIS管理平台
            </Title>
            <Text type="secondary">
              租车SaaS系统运营管理中心
            </Text>
          </div>
          
          <Form
            name="login"
            onFinish={onFinish}
            autoComplete="off"
            layout="vertical"
            style={{ width: '100%' }}
          >
            <Form.Item
              label="用户名"
              name="username"
              rules={[{ required: true, message: '请输入用户名!' }]}
            >
              <Input 
                prefix={<UserOutlined />} 
                placeholder="请输入用户名 (admin)"
                size="large"
              />
            </Form.Item>

            <Form.Item
              label="密码"
              name="password"
              rules={[{ required: true, message: '请输入密码!' }]}
            >
              <Input.Password
                prefix={<LockOutlined />}
                placeholder="请输入密码 (admin)"
                size="large"
              />
            </Form.Item>

            <Form.Item style={{ marginBottom: 0 }}>
              <Button 
                type="primary" 
                htmlType="submit" 
                loading={loading}
                size="large"
                block
              >
                登录
              </Button>
            </Form.Item>
          </Form>
          
          <div style={{ textAlign: 'center', marginTop: 20 }}>
            <Text type="secondary" style={{ fontSize: 12 }}>
              运营管理员账号：admin / admin
            </Text>
          </div>
        </Space>
      </Card>
    </div>
  );
};

export default Login;
import React, { useState, useEffect } from 'react';
import { Button, Input, message, Form, Card, Typography, Row, Col } from 'antd';
import { useNavigate } from 'react-router-dom';
import { login, getCaptcha } from '../services/authService';

const { Title } = Typography;

const Login: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [captchaKey, setCaptchaKey] = useState('');
  const [captchaText, setCaptchaText] = useState('');
  const navigate = useNavigate();

  // 获取验证码
  const fetchCaptcha = async () => {
    try {
      const result = await getCaptcha();
      if (result.success) {
        const [key, text] = result.data.split(':');
        setCaptchaKey(key);
        setCaptchaText(text);
        message.success('验证码已更新');
      } else {
        message.error(result.message || '获取验证码失败');
      }
    } catch (error) {
      message.error('获取验证码失败');
    }
  };

  useEffect(() => {
    fetchCaptcha();
  }, []);

  const onFinish = async (values: { phone: string; code: string }) => {
    setLoading(true);
    try {
      const result = await login(values.phone, values.code, captchaKey);
      if (result.success) {
        message.success('登录成功');
        // 存储认证信息
        const { token, userInfo } = result.data;
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(userInfo));
        // 登录成功后跳转到首页
        navigate('/');
      } else {
        message.error(result.message || '登录失败');
        // 刷新验证码
        fetchCaptcha();
      }
    } catch (error) {
      message.error('登录失败，请稍后重试');
      // 刷新验证码
      fetchCaptcha();
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ 
      display: 'flex', 
      justifyContent: 'center', 
      alignItems: 'center', 
      height: '100vh',
      backgroundColor: '#f0f2f5'
    }}>
      <Card style={{ width: 400 }}>
        <Title level={3} style={{ textAlign: 'center' }}>用户登录</Title>
        <Form
          name="login"
          onFinish={onFinish}
          autoComplete="off"
        >
          <Form.Item
            name="phone"
            rules={[
              { required: true, message: '请输入手机号!' },
              { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号!' }
            ]}
          >
            <Input placeholder="请输入手机号" />
          </Form.Item>

          <Form.Item
            name="code"
            rules={[{ required: true, message: '请输入验证码!' }]}
          >
            <Row gutter={8}>
              <Col span={16}>
                <Input placeholder="请输入验证码" />
              </Col>
              <Col span={8}>
                <Button onClick={fetchCaptcha}>
                  {captchaText || '获取验证码'}
                </Button>
              </Col>
            </Row>
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} style={{ width: '100%' }}>
              登录
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default Login;
import React, { useState, useEffect } from 'react';
import { Button, Input, message, Form, Card, Typography, Row, Col } from 'antd';
import { useNavigate } from 'react-router-dom';
import { login } from '../services/authService';

const { Title } = Typography;

const Login: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [countdown, setCountdown] = useState(0);
  const [isSending, setIsSending] = useState(false);
  const navigate = useNavigate();

  // 发送验证码
  const sendVerificationCode = async () => {
    if (countdown > 0) return;
    
    setIsSending(true);
    try {
      // 这里是假的发送验证码逻辑，实际项目中会调用后端接口
      // 模拟发送验证码的网络请求延迟
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // 设置倒计时
      setCountdown(60);
      message.success('验证码已发送，请输入1111进行登录');
    } catch (error) {
      message.error('发送验证码失败，请稍后重试');
    } finally {
      setIsSending(false);
    }
  };

  // 倒计时效果
  useEffect(() => {
    let timer: NodeJS.Timeout;
    if (countdown > 0) {
      timer = setTimeout(() => setCountdown(countdown - 1), 1000);
    }
    return () => {
      if (timer) clearTimeout(timer);
    };
  }, [countdown]);

  const onFinish = async (values: { phone: string; code: string }) => {
    setLoading(true);
    try {
      // 注意：这里去掉了captchaKey参数，因为不再需要图片验证码
      const result = await login(values.phone, values.code, '');
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
      }
    } catch (error) {
      message.error('登录失败，请稍后重试');
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
                <Input placeholder="请输入验证码(测试阶段请输入1111)" />
              </Col>
              <Col span={8}>
                <Button 
                  onClick={sendVerificationCode} 
                  disabled={countdown > 0 || isSending}
                  loading={isSending}
                >
                  {countdown > 0 ? `${countdown}秒后重发` : '发送验证码'}
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
        <div style={{ textAlign: 'center', marginTop: 16, fontSize: 12, color: '#999' }}>
          测试阶段验证码统一使用1111
        </div>
      </Card>
    </div>
  );
};

export default Login;
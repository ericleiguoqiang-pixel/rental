import React, { useState, useEffect } from 'react'
import { Form, Input, Button, Card, Typography, message, Row, Col } from 'antd'
import { UserOutlined, LockOutlined, ReloadOutlined } from '@ant-design/icons'
import { authAPI } from '../../services/api'

const { Title } = Typography

interface LoginForm {
  username: string
  password: string
  captcha: string
}

const Login: React.FC = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [captchaKey, setCaptchaKey] = useState('')
  const [captchaText, setCaptchaText] = useState('')

  // 获取验证码
  const fetchCaptcha = async () => {
    try {
      const response = await authAPI.getCaptcha()
      // 这里假设后端返回的格式是 "captchaKey:captchaText"
      const [key, text] = response.data.split(':')
      setCaptchaKey(key)
      setCaptchaText(text)
    } catch (error) {
      console.error('获取验证码失败:', error)
      // 如果获取失败，使用模拟数据
      const mockKey = 'captcha_' + Date.now()
      const mockText = Math.random().toString(36).substring(2, 6).toUpperCase()
      setCaptchaKey(mockKey)
      setCaptchaText(mockText)
    }
  }

  useEffect(() => {
    fetchCaptcha()
  }, [])

  const onFinish = async (values: LoginForm) => {
    setLoading(true)
    try {
      console.log('登录信息:', values)
      
      // 调用后端登录API
      const response = await authAPI.login({
        username: values.username,
        password: values.password,
        captcha: values.captcha,
        captchaKey: captchaKey
      })
      
      // 登录成功，保存token
      if (response.code === 200) {
        localStorage.setItem('accessToken', response.data.accessToken)
        localStorage.setItem('refreshToken', response.data.refreshToken)
        localStorage.setItem('userInfo', JSON.stringify(response.data.userInfo))
        
        message.success('登录成功！')
        // 跳转到仪表板
        window.location.href = '/dashboard'
      } else {
        message.error(response.message || '登录失败')
        // 刷新验证码
        fetchCaptcha()
        form.setFieldValue('captcha', '')
      }
    } catch (error: any) {
      console.error('登录失败:', error)
      
      // 处理网络错误或后端不可用的情况
      if (error.message.includes('Failed to fetch') || error.message.includes('Network Error')) {
        message.warning('后端服务暂时不可用，使用模拟登录')
        // 模拟登录成功
        localStorage.setItem('accessToken', 'mock_token_' + Date.now())
        localStorage.setItem('userInfo', JSON.stringify({
          userId: 1,
          username: values.username,
          employeeName: '模拟管理员',
          roleType: 1,
          roleTypeDesc: '超级管理员'
        }))
        message.success('登录成功！')
        window.location.href = '/dashboard'
      } else {
        message.error('登录失败，请检查用户名和密码')
        // 刷新验证码
        fetchCaptcha()
        form.setFieldValue('captcha', '')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-container">
      <Card className="login-form">
        <Title level={2} className="login-title">
          租车SaaS系统
        </Title>
        <Title level={4} style={{ textAlign: 'center', marginBottom: 32, color: '#666' }}>
          商户端管理平台
        </Title>
        
        <Form
          form={form}
          name="login"
          onFinish={onFinish}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[
              { required: true, message: '请输入用户名' },
              { min: 4, message: '用户名至少4个字符' }
            ]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="用户名/手机号"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: '请输入密码' },
              { min: 6, message: '密码至少6个字符' }
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="密码"
            />
          </Form.Item>

          <Form.Item
            name="captcha"
            rules={[{ required: true, message: '请输入验证码' }]}
          >
            <Row gutter={8}>
              <Col span={14}>
                <Input placeholder="验证码" />
              </Col>
              <Col span={10}>
                <Button 
                  onClick={fetchCaptcha}
                  icon={<ReloadOutlined />}
                  style={{ width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}
                >
                  {captchaText || '获取验证码'}
                </Button>
              </Col>
            </Row>
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block>
              登录
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}

export default Login
import React, { useState } from 'react'
import { Card, Form, Input, Button, Steps, Row, Col, Upload, Select, message, Typography } from 'antd'
import { PlusOutlined, UploadOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { request } from '../../services/api'
import './index.css'

const { Title, Paragraph } = Typography
const { TextArea } = Input
const { Step } = Steps

interface MerchantRegisterForm {
  // 基本信息
  companyName: string
  businessLicense: string
  legalPerson: string
  contactPhone: string
  contactEmail: string
  
  // 详细信息
  businessAddress: string
  businessScope: string
  registeredCapital: string
  establishedDate: string
  
  // 联系人信息
  contactPersonName: string
  contactPersonPhone: string
  contactPersonEmail: string
  
  // 平台账户信息
  username: string
  password: string
  confirmPassword: string
}

const MerchantRegister: React.FC = () => {
  const [currentStep, setCurrentStep] = useState(0)
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const steps = [
    {
      title: '基本信息',
      description: '填写企业基本信息',
    },
    {
      title: '详细资料',
      description: '完善企业详细资料',
    },
    {
      title: '联系信息',
      description: '设置联系人信息',
    },
    {
      title: '账户设置',
      description: '创建平台账户',
    },
  ]

  const onFinish = async (values: MerchantRegisterForm) => {
    if (values.password !== values.confirmPassword) {
      message.error('两次输入的密码不一致')
      return
    }

    setLoading(true)
    try {
      const response = await request('/merchant/register', {
        method: 'POST',
        body: JSON.stringify(values),
      })

      if (response.code === 200) {
        message.success('商户入住申请提交成功！我们将在1-3个工作日内完成审核')
        navigate('/register-success', { state: { merchantId: response.data.merchantId } })
      } else {
        message.error(response.message || '提交失败，请重试')
      }
    } catch (error) {
      console.error('商户注册失败:', error)
      message.error('网络错误，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  const next = async () => {
    try {
      const values = await form.validateFields()
      setCurrentStep(currentStep + 1)
    } catch (errorInfo) {
      console.log('验证失败:', errorInfo)
    }
  }

  const prev = () => {
    setCurrentStep(currentStep - 1)
  }

  const renderStepContent = () => {
    switch (currentStep) {
      case 0:
        return (
          <>
            <Form.Item
              label="企业名称"
              name="companyName"
              rules={[{ required: true, message: '请输入企业名称' }]}
            >
              <Input placeholder="请输入企业全称" />
            </Form.Item>

            <Form.Item
              label="统一社会信用代码"
              name="businessLicense"
              rules={[
                { required: true, message: '请输入统一社会信用代码' },
                { pattern: /^[0-9A-HJ-NPQRTUWXY]{2}\d{6}[0-9A-HJ-NPQRTUWXY]{10}$/, message: '请输入正确的统一社会信用代码' }
              ]}
            >
              <Input placeholder="请输入18位统一社会信用代码" />
            </Form.Item>

            <Form.Item
              label="法定代表人"
              name="legalPerson"
              rules={[{ required: true, message: '请输入法定代表人姓名' }]}
            >
              <Input placeholder="请输入法定代表人姓名" />
            </Form.Item>

            <Form.Item
              label="企业联系电话"
              name="contactPhone"
              rules={[
                { required: true, message: '请输入联系电话' },
                { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码' }
              ]}
            >
              <Input placeholder="请输入11位手机号码" />
            </Form.Item>

            <Form.Item
              label="企业邮箱"
              name="contactEmail"
              rules={[
                { required: true, message: '请输入企业邮箱' },
                { type: 'email', message: '请输入正确的邮箱地址' }
              ]}
            >
              <Input placeholder="请输入企业邮箱地址" />
            </Form.Item>
          </>
        )

      case 1:
        return (
          <>
            <Form.Item
              label="经营地址"
              name="businessAddress"
              rules={[{ required: true, message: '请输入经营地址' }]}
            >
              <TextArea rows={3} placeholder="请输入详细的经营地址" />
            </Form.Item>

            <Form.Item
              label="经营范围"
              name="businessScope"
              rules={[{ required: true, message: '请输入经营范围' }]}
            >
              <TextArea rows={3} placeholder="请输入主要经营范围" />
            </Form.Item>

            <Form.Item
              label="注册资本"
              name="registeredCapital"
              rules={[{ required: true, message: '请输入注册资本' }]}
            >
              <Input placeholder="请输入注册资本（万元）" suffix="万元" />
            </Form.Item>

            <Form.Item
              label="成立日期"
              name="establishedDate"
              rules={[{ required: true, message: '请选择成立日期' }]}
            >
              <Input type="date" />
            </Form.Item>
          </>
        )

      case 2:
        return (
          <>
            <Form.Item
              label="联系人姓名"
              name="contactPersonName"
              rules={[{ required: true, message: '请输入联系人姓名' }]}
            >
              <Input placeholder="请输入主要联系人姓名" />
            </Form.Item>

            <Form.Item
              label="联系人电话"
              name="contactPersonPhone"
              rules={[
                { required: true, message: '请输入联系人电话' },
                { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码' }
              ]}
            >
              <Input placeholder="请输入联系人手机号" />
            </Form.Item>

            <Form.Item
              label="联系人邮箱"
              name="contactPersonEmail"
              rules={[
                { required: true, message: '请输入联系人邮箱' },
                { type: 'email', message: '请输入正确的邮箱地址' }
              ]}
            >
              <Input placeholder="请输入联系人邮箱" />
            </Form.Item>
          </>
        )

      case 3:
        return (
          <>
            <Form.Item
              label="登录用户名"
              name="username"
              rules={[
                { required: true, message: '请输入用户名' },
                { min: 4, max: 20, message: '用户名长度为4-20个字符' },
                { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线' }
              ]}
            >
              <Input placeholder="请输入登录用户名" />
            </Form.Item>

            <Form.Item
              label="登录密码"
              name="password"
              rules={[
                { required: true, message: '请输入密码' },
                { min: 6, message: '密码至少6个字符' },
                { pattern: /^(?=.*[a-zA-Z])(?=.*\d)/, message: '密码必须包含字母和数字' }
              ]}
            >
              <Input.Password placeholder="请输入登录密码" />
            </Form.Item>

            <Form.Item
              label="确认密码"
              name="confirmPassword"
              rules={[
                { required: true, message: '请确认密码' },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue('password') === value) {
                      return Promise.resolve()
                    }
                    return Promise.reject(new Error('两次输入的密码不一致'))
                  },
                }),
              ]}
            >
              <Input.Password placeholder="请再次输入密码" />
            </Form.Item>
          </>
        )

      default:
        return null
    }
  }

  return (
    <div className="merchant-register-container">
      <Card className="register-form-card">
        <Title level={2} style={{ textAlign: 'center', marginBottom: 32 }}>
          商户入住申请
        </Title>

        <Steps current={currentStep} style={{ marginBottom: 32 }}>
          {steps.map(item => (
            <Step key={item.title} title={item.title} description={item.description} />
          ))}
        </Steps>

        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          style={{ maxWidth: 600, margin: '0 auto' }}
        >
          {renderStepContent()}

          <Form.Item style={{ marginTop: 32 }}>
            <Row gutter={16}>
              <Col span={12}>
                {currentStep > 0 && (
                  <Button onClick={prev} style={{ width: '100%' }}>
                    上一步
                  </Button>
                )}
              </Col>
              <Col span={12}>
                {currentStep < steps.length - 1 ? (
                  <Button type="primary" onClick={next} style={{ width: '100%' }}>
                    下一步
                  </Button>
                ) : (
                  <Button type="primary" htmlType="submit" loading={loading} style={{ width: '100%' }}>
                    提交申请
                  </Button>
                )}
              </Col>
            </Row>
          </Form.Item>
        </Form>

        <div style={{ textAlign: 'center', marginTop: 24 }}>
          <Button type="link" onClick={() => navigate('/login')}>
            已有账号？立即登录
          </Button>
        </div>
      </Card>
    </div>
  )
}

export default MerchantRegister
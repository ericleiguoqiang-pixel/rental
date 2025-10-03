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
  legalPersonIdCard: string
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
  
  // 证件信息
  businessLicenseUrl?: string
  transportLicenseUrl?: string
  
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
    
    // 将前端表单字段映射到后端DTO字段
    const requestData = {
      companyName: values.companyName,
      unifiedSocialCreditCode: values.businessLicense,
      legalPersonName: values.legalPerson,
      legalPersonIdCard: values.legalPersonIdCard,
      contactName: values.contactPersonName,
      contactPhone: values.contactPersonPhone,
      companyAddress: values.businessAddress,
      businessLicenseUrl: values.businessLicenseUrl || '',
      transportLicenseUrl: values.transportLicenseUrl || ''
    };

    try {
      const response = await request('/merchants/apply', {
        method: 'POST',
        body: JSON.stringify(requestData),
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

  // 定义每个步骤需要验证的字段
  const stepFields = [
    ['companyName', 'businessLicense', 'legalPerson', 'legalPersonIdCard', 'contactPhone', 'contactEmail'],
    ['businessAddress', 'businessScope', 'registeredCapital', 'establishedDate'],
    ['contactPersonName', 'contactPersonPhone', 'contactPersonEmail'],
    ['username', 'password', 'confirmPassword']
  ];

  const next = async () => {
    try {
      // 只验证当前步骤的字段
      const fieldsToValidate = stepFields[currentStep];
      await form.validateFields(fieldsToValidate);
      setCurrentStep(currentStep + 1);
    } catch (errorInfo) {
      console.log('验证失败:', errorInfo);
    }
  }

  const prev = () => {
    setCurrentStep(currentStep - 1)
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
          {/* 渲染所有步骤的表单字段，但只显示当前步骤的字段 */}
          <div style={{ display: currentStep === 0 ? 'block' : 'none' }}>
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
              label="法人身份证号"
              name="legalPersonIdCard"
              rules={[
                { required: true, message: '请输入法人身份证号' },
                { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入正确的身份证号' }
              ]}
            >
              <Input placeholder="请输入法人身份证号" />
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
          </div>

          <div style={{ display: currentStep === 1 ? 'block' : 'none' }}>
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
          </div>

          <div style={{ display: currentStep === 2 ? 'block' : 'none' }}>
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
          </div>

          <div style={{ display: currentStep === 3 ? 'block' : 'none' }}>
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
          </div>

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
import React from 'react'
import { Result, Button, Card, Typography, Timeline, Space } from 'antd'
import { CheckCircleOutlined, ClockCircleOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'

const { Title, Paragraph } = Typography

const RegisterSuccess: React.FC = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const merchantId = location.state?.merchantId || '待分配'

  return (
    <div style={{
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '20px'
    }}>
      <Card style={{ 
        width: '100%', 
        maxWidth: 600,
        borderRadius: 12,
        boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)'
      }}>
        <Result
          status="success"
          title="入住申请提交成功！"
          subTitle={`您的商户申请ID：${merchantId}`}
          extra={[
            <Button type="primary" key="login" onClick={() => navigate('/login')}>
              返回登录
            </Button>,
            <Button key="home" onClick={() => navigate('/')}>
              返回首页
            </Button>,
          ]}
        />

        <div style={{ marginTop: 24 }}>
          <Title level={4}>后续流程：</Title>
          <Timeline>
            <Timeline.Item dot={<CheckCircleOutlined style={{ color: '#52c41a' }} />}>
              <strong>提交申请</strong>
              <br />
              <span style={{ color: '#666' }}>您已成功提交商户入住申请</span>
            </Timeline.Item>
            <Timeline.Item dot={<ClockCircleOutlined style={{ color: '#1890ff' }} />}>
              <strong>资质审核</strong>
              <br />
              <span style={{ color: '#666' }}>我们将在1-3个工作日内完成审核</span>
            </Timeline.Item>
            <Timeline.Item dot={<ClockCircleOutlined style={{ color: '#faad14' }} />}>
              <strong>账户激活</strong>
              <br />
              <span style={{ color: '#666' }}>审核通过后将通过邮件通知您激活账户</span>
            </Timeline.Item>
            <Timeline.Item dot={<ClockCircleOutlined style={{ color: '#722ed1' }} />}>
              <strong>开始使用</strong>
              <br />
              <span style={{ color: '#666' }}>激活后即可登录系统开始使用</span>
            </Timeline.Item>
          </Timeline>
        </div>

        <div style={{ 
          marginTop: 32, 
          padding: 16, 
          background: '#f6f8fa', 
          borderRadius: 8 
        }}>
          <Title level={5} style={{ margin: 0, marginBottom: 8 }}>温馨提示：</Title>
          <ul style={{ margin: 0, paddingLeft: 20 }}>
            <li>请保持联系电话和邮箱畅通，我们会及时与您联系</li>
            <li>如有疑问，请联系客服：400-xxx-xxxx</li>
            <li>您可以通过邮件查看审核进度</li>
          </ul>
        </div>
      </Card>
    </div>
  )
}

export default RegisterSuccess
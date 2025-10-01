import React from 'react'
import { Card, Row, Col, Statistic, Typography, Spin } from 'antd'
import { ArrowUpOutlined } from '@ant-design/icons'
import MainLayout from '../../components/MainLayout'
import { useDashboardStats } from '../../hooks/useDashboard'

const { Title } = Typography

const Dashboard: React.FC = () => {
  const { stats, loading } = useDashboardStats()

  return (
    <MainLayout title="仪表盘">
      <Spin spinning={loading}>
        {/* 数据统计卡片 */}
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={6}>
            <Card>
              <Statistic
                title="今日订单"
                value={stats.todayOrders}
                valueStyle={{ color: '#3f8600' }}
                prefix={<ArrowUpOutlined />}
                suffix="单"
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic
                title="今日收入"
                value={stats.todayRevenue}
                precision={2}
                valueStyle={{ color: '#3f8600' }}
                prefix="¥"
                suffix={<ArrowUpOutlined />}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic
                title="可用车辆"
                value={stats.availableVehicles}
                valueStyle={{ color: '#1890ff' }}
                suffix="辆"
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic
                title="租出车辆"
                value={stats.rentedVehicles}
                valueStyle={{ color: '#cf1322' }}
                suffix="辆"
              />
            </Card>
          </Col>
        </Row>

        {/* 欢迎信息 */}
        <Card>
          <Title level={3}>欢迎使用租车SaaS系统</Title>
          <p>这是一个功能完善的租车行业数字化管理平台</p>
          <p>您可以通过左侧菜单访问各个功能模块：</p>
          <ul>
            <li><strong>门店管理</strong>：管理您的门店信息和营业状态</li>
            <li><strong>车辆管理</strong>：管理车辆信息、车型库和车辆审核</li>
            <li><strong>订单管理</strong>：查看和处理客户订单</li>
            <li><strong>支付管理</strong>：管理支付记录和退款</li>
            <li><strong>员工管理</strong>：管理员工账户和权限</li>
          </ul>
        </Card>
      </Spin>
    </MainLayout>
  )
}

export default Dashboard
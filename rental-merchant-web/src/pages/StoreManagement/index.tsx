import React from 'react'
import { Card, Table, Button, Space, Tag, Statistic, Row, Col } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import MainLayout from '../../components/MainLayout'

const StoreManagement: React.FC = () => {
  const columns = [
    {
      title: '门店名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '门店地址',
      dataIndex: 'address',
      key: 'address',
    },
    {
      title: '联系电话',
      dataIndex: 'phone',
      key: 'phone',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => (
        <Tag color={status === 'active' ? 'green' : 'red'}>
          {status === 'active' ? '营业中' : '暂停营业'}
        </Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: () => (
        <Space size="middle">
          <Button type="link" icon={<EditOutlined />}>编辑</Button>
          <Button type="link" danger icon={<DeleteOutlined />}>删除</Button>
        </Space>
      ),
    },
  ]

  const mockData = [
    {
      key: '1',
      name: '总店',
      address: '上海市浦东新区陆家嘴金融中心',
      phone: '021-12345678',
      status: 'active',
    },
    {
      key: '2',
      name: '虹桥分店',
      address: '上海市长宁区虹桥路1号',
      phone: '021-87654321',
      status: 'active',
    },
  ]

  return (
    <MainLayout title="门店管理">
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic title="门店总数" value={2} />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="营业中门店" value={2} />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="暂停营业" value={0} />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="月营业额" value={128000} prefix="¥" />
          </Card>
        </Col>
      </Row>

      <Card 
        title="门店列表" 
        extra={<Button type="primary" icon={<PlusOutlined />}>新增门店</Button>}
      >
        <Table columns={columns} dataSource={mockData} />
      </Card>
    </MainLayout>
  )
}

export default StoreManagement
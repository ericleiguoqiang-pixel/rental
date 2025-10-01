import React from 'react'
import { Card, Table, Button, Space, Tag, Statistic, Row, Col, Spin, Popconfirm } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import MainLayout from '../../components/MainLayout'
import { useStores, Store } from '../../hooks/useStores'

const StoreManagement: React.FC = () => {
  const { stores, loading, deleteStore } = useStores()
  
  const handleDelete = async (id: string) => {
    try {
      await deleteStore(id)
    } catch (error) {
      // 错误信息已在 hook 中处理
    }
  }
  
  const activeStores = stores.filter(store => store.status === 'active').length
  const inactiveStores = stores.filter(store => store.status === 'inactive').length
  const totalRevenue = 128000 // 这个需要从另外的API获取
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
      render: (_, record: Store) => (
        <Space size="middle">
          <Button type="link" icon={<EditOutlined />}>编辑</Button>
          <Popconfirm
            title="删除门店"
            description="确定要删除这个门店吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  const mockData = stores

  return (
    <MainLayout title="门店管理">
      <Spin spinning={loading}>
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={6}>
            <Card>
              <Statistic title="门店总数" value={stores.length} />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic title="营业中门店" value={activeStores} />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic title="暂停营业" value={inactiveStores} />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic title="月营业额" value={totalRevenue} prefix="¥" />
            </Card>
          </Col>
        </Row>

        <Card 
          title="门店列表" 
          extra={<Button type="primary" icon={<PlusOutlined />}>新增门店</Button>}
        >
          <Table 
            columns={columns} 
            dataSource={mockData.map(item => ({ ...item, key: item.id }))}
            loading={loading}
          />
        </Card>
      </Spin>
    </MainLayout>
  )
}

export default StoreManagement
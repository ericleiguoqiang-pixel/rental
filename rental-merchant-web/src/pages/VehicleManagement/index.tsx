import React from 'react'
import { Card, Table, Button, Space, Tag, Select, Input } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons'

const { Search } = Input

const VehicleManagement: React.FC = () => {
  const columns = [
    {
      title: '车牌号',
      dataIndex: 'plateNumber',
      key: 'plateNumber',
    },
    {
      title: '车型',
      dataIndex: 'model',
      key: 'model',
    },
    {
      title: '品牌',
      dataIndex: 'brand',
      key: 'brand',
    },
    {
      title: '颜色',
      dataIndex: 'color',
      key: 'color',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        const statusMap = {
          available: { color: 'green', text: '可租用' },
          rented: { color: 'blue', text: '已租出' },
          maintenance: { color: 'orange', text: '维护中' },
          inactive: { color: 'red', text: '停用' }
        }
        const statusInfo = statusMap[status as keyof typeof statusMap]
        return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>
      },
    },
    {
      title: '日租金',
      dataIndex: 'dailyRate',
      key: 'dailyRate',
      render: (rate: number) => `¥${rate}`,
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
      plateNumber: '沪A12345',
      model: '朗逸',
      brand: '大众',
      color: '白色',
      status: 'available',
      dailyRate: 150,
    },
    {
      key: '2',
      plateNumber: '沪B67890',
      model: '轩逸',
      brand: '日产',
      color: '银色',
      status: 'rented',
      dailyRate: 140,
    },
    {
      key: '3',
      plateNumber: '沪C11111',
      model: 'Model 3',
      brand: '特斯拉',
      color: '黑色',
      status: 'maintenance',
      dailyRate: 300,
    },
  ]

  return (
    <Card 
      title="车辆管理"
      extra={
        <Space>
          <Search placeholder="搜索车牌号" style={{ width: 200 }} />
          <Select defaultValue="all" style={{ width: 120 }}>
            <Select.Option value="all">全部状态</Select.Option>
            <Select.Option value="available">可租用</Select.Option>
            <Select.Option value="rented">已租出</Select.Option>
            <Select.Option value="maintenance">维护中</Select.Option>
          </Select>
          <Button type="primary" icon={<PlusOutlined />}>新增车辆</Button>
        </Space>
      }
    >
      <Table columns={columns} dataSource={mockData} />
    </Card>
  )
}

export default VehicleManagement
import React from 'react'
import { Card, Table, Button, Space, Tag, Select, DatePicker, Input } from 'antd'
import { EyeOutlined, EditOutlined } from '@ant-design/icons'

const { RangePicker } = DatePicker
const { Search } = Input

const OrderManagement: React.FC = () => {
  const columns = [
    {
      title: '订单号',
      dataIndex: 'orderNo',
      key: 'orderNo',
    },
    {
      title: '客户姓名',
      dataIndex: 'customerName',
      key: 'customerName',
    },
    {
      title: '车辆信息',
      dataIndex: 'vehicleInfo',
      key: 'vehicleInfo',
    },
    {
      title: '租期',
      dataIndex: 'duration',
      key: 'duration',
    },
    {
      title: '总金额',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      render: (amount: number) => `¥${amount}`,
    },
    {
      title: '订单状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        const statusMap = {
          pending: { color: 'orange', text: '待确认' },
          confirmed: { color: 'blue', text: '已确认' },
          ongoing: { color: 'green', text: '进行中' },
          completed: { color: 'gray', text: '已完成' },
          cancelled: { color: 'red', text: '已取消' }
        }
        const statusInfo = statusMap[status as keyof typeof statusMap]
        return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>
      },
    },
    {
      title: '操作',
      key: 'action',
      render: () => (
        <Space size="middle">
          <Button type="link" icon={<EyeOutlined />}>查看</Button>
          <Button type="link" icon={<EditOutlined />}>处理</Button>
        </Space>
      ),
    },
  ]

  const mockData = [
    {
      key: '1',
      orderNo: 'R20231201001',
      customerName: '张三',
      vehicleInfo: '沪A12345 大众朗逸',
      duration: '2023-12-01 至 2023-12-03',
      totalAmount: 450,
      status: 'ongoing',
    },
    {
      key: '2',
      orderNo: 'R20231201002',
      customerName: '李四',
      vehicleInfo: '沪B67890 日产轩逸',
      duration: '2023-12-02 至 2023-12-05',
      totalAmount: 560,
      status: 'confirmed',
    },
    {
      key: '3',
      orderNo: 'R20231130001',
      customerName: '王五',
      vehicleInfo: '沪C11111 特斯拉Model 3',
      duration: '2023-11-30 至 2023-12-01',
      totalAmount: 600,
      status: 'completed',
    },
  ]

  return (
    <Card 
      title="订单管理"
      extra={
        <Space>
          <Search placeholder="搜索订单号/客户" style={{ width: 200 }} />
          <Select defaultValue="all" style={{ width: 120 }}>
            <Select.Option value="all">全部状态</Select.Option>
            <Select.Option value="pending">待确认</Select.Option>
            <Select.Option value="confirmed">已确认</Select.Option>
            <Select.Option value="ongoing">进行中</Select.Option>
            <Select.Option value="completed">已完成</Select.Option>
          </Select>
          <RangePicker placeholder={['开始日期', '结束日期']} />
        </Space>
      }
    >
      <Table columns={columns} dataSource={mockData} />
    </Card>
  )
}

export default OrderManagement
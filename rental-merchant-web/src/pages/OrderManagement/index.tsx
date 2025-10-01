import React from 'react'
import { Card, Table, Button, Space, Tag, Select, DatePicker, Input, Spin } from 'antd'
import { EyeOutlined, EditOutlined } from '@ant-design/icons'
import MainLayout from '../../components/MainLayout'
import { useOrders, Order } from '../../hooks/useOrders'

const { RangePicker } = DatePicker
const { Search } = Input

const OrderManagement: React.FC = () => {
  const { orders, loading, updateOrderStatus } = useOrders()
  
  const handleStatusUpdate = async (id: string, status: string) => {
    try {
      await updateOrderStatus(id, status)
    } catch (error) {
      // 错误信息已在 hook 中处理
    }
  }
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
      render: (_, record: Order) => (
        <Space size="middle">
          <Button type="link" icon={<EyeOutlined />}>查看</Button>
          <Button 
            type="link" 
            icon={<EditOutlined />}
            onClick={() => {
              // 这里可以打开状态更新对话框
              console.log('处理订单:', record.id)
            }}
          >
            处理
          </Button>
        </Space>
      ),
    },
  ]

  const mockData = orders

  return (
    <MainLayout title="订单管理">
      <Spin spinning={loading}>
        <Card 
          title="订单列表"
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

export default OrderManagement
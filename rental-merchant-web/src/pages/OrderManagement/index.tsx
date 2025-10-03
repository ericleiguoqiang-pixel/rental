import React, { useState, useEffect } from 'react'
import { 
  Card, 
  Table, 
  Button, 
  Space, 
  Tag, 
  Select, 
  Input, 
  Spin, 
  Modal, 
  Form, 
  Input as AntdInput,
  message,
  Descriptions,
  Divider
} from 'antd'
import { 
  EyeOutlined, 
  CarOutlined, 
  UserOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined
} from '@ant-design/icons'
import MainLayout from '../../components/MainLayout'
import { useOrders, Order } from '../../hooks/useOrders'

const { Search } = Input

const OrderManagement: React.FC = () => {
  const { 
    orders, 
    loading, 
    fetchOrders,
    getOrderDetail,
    assignPickupDriver,
    assignReturnDriver,
    confirmPickup,
    confirmReturn
  } = useOrders()
  
  const [form] = Form.useForm()
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null)
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [isDetailModalVisible, setIsDetailModalVisible] = useState(false)
  const [searchParams, setSearchParams] = useState({
    current: 1,
    size: 10,
    orderNo: '',
    status: undefined as number | undefined
  })

  // 确保 orders 是数组
  const orderList = Array.isArray(orders) ? orders : []
  
  useEffect(() => {
    fetchOrders(searchParams)
  }, [searchParams])

  const handleSearch = (value: string) => {
    setSearchParams(prev => ({
      ...prev,
      orderNo: value,
      current: 1
    }))
  }

  const handleStatusChange = (value: number | string) => {
    setSearchParams(prev => ({
      ...prev,
      status: value === 'all' ? undefined : Number(value),
      current: 1
    }))
  }

  const handleTableChange = (pagination: any) => {
    setSearchParams(prev => ({
      ...prev,
      current: pagination.current,
      size: pagination.pageSize
    }))
  }

  const showDriverModal = (order: Order, type: 'pickup' | 'return') => {
    setSelectedOrder(order)
    form.setFieldsValue({
      driverName: type === 'pickup' ? order.pickupDriver : order.returnDriver
    })
    setIsModalVisible(true)
  }

  const showDetailModal = async (order: Order) => {
    try {
      const detail = await getOrderDetail(order.id)
      setSelectedOrder(detail)
      setIsDetailModalVisible(true)
    } catch (error) {
      console.error('获取订单详情失败:', error)
    }
  }

  const handleDriverSubmit = async (values: { driverName: string }) => {
    if (!selectedOrder) return
    
    try {
      if (selectedOrder.orderStatus === 2) { // 待取车
        await assignPickupDriver(selectedOrder.id, values.driverName)
      } else if (selectedOrder.orderStatus === 3) { // 已取车
        await assignReturnDriver(selectedOrder.id, values.driverName)
      }
      setIsModalVisible(false)
      form.resetFields()
    } catch (error) {
      console.error('分配司机失败:', error)
    }
  }

  const handleConfirmPickup = async (order: Order) => {
    try {
      await confirmPickup(order.id)
      message.success('确认取车成功')
    } catch (error) {
      console.error('确认取车失败:', error)
      message.error('确认取车失败')
    }
  }

  const handleConfirmReturn = async (order: Order) => {
    try {
      await confirmReturn(order.id)
      message.success('确认还车成功')
    } catch (error) {
      console.error('确认还车失败:', error)
      message.error('确认还车失败')
    }
  }

  const getStatusText = (status: number) => {
    const statusMap: Record<number, { text: string; color: string }> = {
      1: { text: '待支付', color: 'orange' },
      2: { text: '待取车', color: 'blue' },
      3: { text: '已取车', color: 'green' },
      4: { text: '已完成', color: 'gray' },
      5: { text: '已取消', color: 'red' }
    }
    return statusMap[status] || { text: '未知', color: 'default' }
  }

  const columns = [
    {
      title: '订单号',
      dataIndex: 'orderNo',
      key: 'orderNo',
    },
    {
      title: '客户姓名',
      dataIndex: 'driverName',
      key: 'driverName',
    },
    {
      title: '车辆信息',
      dataIndex: 'licensePlate',
      key: 'licensePlate',
      render: (licensePlate: string, record: Order) => (
        <Space>
          <CarOutlined />
          <span>{licensePlate || '未分配'}</span>
        </Space>
      ),
    },
    {
      title: '租期',
      key: 'duration',
      render: (_: any, record: Order) => (
        <div>
          <div>{new Date(record.startTime).toLocaleDateString()}</div>
          <div>至</div>
          <div>{new Date(record.endTime).toLocaleDateString()}</div>
        </div>
      ),
    },
    {
      title: '取车司机',
      dataIndex: 'pickupDriver',
      key: 'pickupDriver',
      render: (pickupDriver: string) => pickupDriver || '未分配',
    },
    {
      title: '还车司机',
      dataIndex: 'returnDriver',
      key: 'returnDriver',
      render: (returnDriver: string) => returnDriver || '未分配',
    },
    {
      title: '订单状态',
      dataIndex: 'orderStatus',
      key: 'orderStatus',
      render: (status: number) => {
        const statusInfo = getStatusText(status)
        return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>
      },
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: Order) => (
        <Space size="middle">
          <Button 
            type="link" 
            icon={<EyeOutlined />} 
            onClick={() => showDetailModal(record)}
          >
            查看
          </Button>
          
          {record.orderStatus === 2 && ( // 待取车
            <>
              <Button 
                type="link" 
                icon={<UserOutlined />}
                onClick={() => showDriverModal(record, 'pickup')}
              >
                分配司机
              </Button>
              <Button 
                type="link" 
                icon={<CheckCircleOutlined />}
                onClick={() => handleConfirmPickup(record)}
              >
                确认取车
              </Button>
            </>
          )}
          
          {record.orderStatus === 3 && ( // 已取车
            <>
              <Button 
                type="link" 
                icon={<UserOutlined />}
                onClick={() => showDriverModal(record, 'return')}
              >
                分配司机
              </Button>
              <Button 
                type="link" 
                icon={<CheckCircleOutlined />}
                onClick={() => handleConfirmReturn(record)}
              >
                确认还车
              </Button>
            </>
          )}
        </Space>
      ),
    },
  ]

  return (
    <MainLayout title="订单管理">
      <Spin spinning={loading}>
        <Card 
          title="订单列表"
          extra={
            <Space>
              <Search 
                placeholder="搜索订单号" 
                onSearch={handleSearch}
                style={{ width: 200 }} 
              />
              <Select 
                defaultValue="all" 
                style={{ width: 120 }}
                onChange={handleStatusChange}
              >
                <Select.Option value="all">全部状态</Select.Option>
                <Select.Option value={1}>待支付</Select.Option>
                <Select.Option value={2}>待取车</Select.Option>
                <Select.Option value={3}>已取车</Select.Option>
                <Select.Option value={4}>已完成</Select.Option>
                <Select.Option value={5}>已取消</Select.Option>
              </Select>
            </Space>
          }
        >
          <Table 
            columns={columns} 
            dataSource={orderList.map(item => ({ ...item, key: item.id }))}
            loading={loading}
            pagination={{
              current: searchParams.current,
              pageSize: searchParams.size,
              total: orderList.length,
              showSizeChanger: true,
              showQuickJumper: true,
            }}
            onChange={handleTableChange}
          />
        </Card>
      </Spin>

      {/* 分配司机弹窗 */}
      <Modal
        title={selectedOrder?.orderStatus === 2 ? "分配取车司机" : "分配还车司机"}
        visible={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false)
          form.resetFields()
        }}
        onOk={() => form.submit()}
      >
        <Form
          form={form}
          onFinish={handleDriverSubmit}
        >
          <Form.Item
            label="司机姓名"
            name="driverName"
            rules={[{ required: true, message: '请输入司机姓名' }]}
          >
            <AntdInput placeholder="请输入司机姓名" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 订单详情弹窗 */}
      <Modal
        title="订单详情"
        visible={isDetailModalVisible}
        onCancel={() => setIsDetailModalVisible(false)}
        footer={null}
        width={800}
      >
        {selectedOrder && (
          <div>
            <Descriptions title="基本信息" column={2} bordered>
              <Descriptions.Item label="订单号">{selectedOrder.orderNo}</Descriptions.Item>
              <Descriptions.Item label="订单状态">
                <Tag color={getStatusText(selectedOrder.orderStatus).color}>
                  {getStatusText(selectedOrder.orderStatus).text}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="客户姓名">{selectedOrder.driverName}</Descriptions.Item>
              <Descriptions.Item label="联系电话">{selectedOrder.driverPhone}</Descriptions.Item>
              <Descriptions.Item label="车牌号">{selectedOrder.licensePlate || '未分配'}</Descriptions.Item>
              <Descriptions.Item label="下单时间">
                {selectedOrder.createdAt ? new Date(selectedOrder.createdAt).toLocaleString() : ''}
              </Descriptions.Item>
            </Descriptions>
            
            <Divider />
            
            <Descriptions title="租期信息" column={2} bordered>
              <Descriptions.Item label="计划取车时间">
                {new Date(selectedOrder.startTime).toLocaleString()}
              </Descriptions.Item>
              <Descriptions.Item label="计划还车时间">
                {new Date(selectedOrder.endTime).toLocaleString()}
              </Descriptions.Item>
              <Descriptions.Item label="实际取车时间">
                {selectedOrder.actualPickupTime ? new Date(selectedOrder.actualPickupTime).toLocaleString() : '未取车'}
              </Descriptions.Item>
              <Descriptions.Item label="实际还车时间">
                {selectedOrder.actualReturnTime ? new Date(selectedOrder.actualReturnTime).toLocaleString() : '未还车'}
              </Descriptions.Item>
            </Descriptions>
            
            <Divider />
            
            <Descriptions title="司机信息" column={2} bordered>
              <Descriptions.Item label="取车司机">
                {selectedOrder.pickupDriver || '未分配'}
              </Descriptions.Item>
              <Descriptions.Item label="还车司机">
                {selectedOrder.returnDriver || '未分配'}
              </Descriptions.Item>
            </Descriptions>
          </div>
        )}
      </Modal>
    </MainLayout>
  )
}

export default OrderManagement
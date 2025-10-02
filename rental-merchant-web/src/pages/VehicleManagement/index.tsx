import React, { useState } from 'react'
import { Card, Table, Button, Space, Tag, Select, Input, Spin, Popconfirm, message } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons'
import MainLayout from '../../components/MainLayout'
import VehicleFormModal from '../../components/VehicleFormModal'
import { useVehicles, Vehicle } from '../../hooks/useVehicles'

const { Search } = Input

const VehicleManagement: React.FC = () => {
  const { vehicles, loading, deleteVehicle, createVehicle, updateVehicle } = useVehicles()
  const [modalVisible, setModalVisible] = useState(false)
  const [editingVehicle, setEditingVehicle] = useState<Vehicle | undefined>(undefined)
  
  // 确保 vehicles 是数组
  const vehicleList = Array.isArray(vehicles) ? vehicles : []
  
  const handleDelete = async (id: string) => {
    try {
      await deleteVehicle(id)
    } catch (error) {
      // 错误信息已在 hook 中处理
    }
  }

  const handleAdd = () => {
    setEditingVehicle(undefined)
    setModalVisible(true)
  }

  const handleEdit = (vehicle: Vehicle) => {
    setEditingVehicle(vehicle)
    setModalVisible(true)
  }

  const handleModalSubmit = async (values: any) => {
    try {
      if (editingVehicle) {
        await updateVehicle(editingVehicle.id, values)
        message.success('车辆更新成功')
      } else {
        await createVehicle(values)
        message.success('车辆创建成功')
      }
      setModalVisible(false)
      setEditingVehicle(undefined)
    } catch (error) {
      // 错误信息已在 hook 中处理
    }
  }

  const handleModalCancel = () => {
    setModalVisible(false)
    setEditingVehicle(undefined)
  }
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
      render: (_, record: Vehicle) => (
        <Space size="middle">
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          <Popconfirm
            title="删除车辆"
            description="确定要删除这辆车吗？"
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

  const mockData = vehicleList

  return (
    <MainLayout title="车辆管理">
      <Spin spinning={loading}>
        <Card 
          title="车辆列表"
          extra={
            <Space>
              <Search placeholder="搜索车牌号" style={{ width: 200 }} />
              <Select defaultValue="all" style={{ width: 120 }}>
                <Select.Option value="all">全部状态</Select.Option>
                <Select.Option value="available">可租用</Select.Option>
                <Select.Option value="rented">已租出</Select.Option>
                <Select.Option value="maintenance">维护中</Select.Option>
              </Select>
              <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>新增车辆</Button>
            </Space>
          }
        >
          <Table 
            columns={columns} 
            dataSource={mockData.map(item => ({ ...item, key: item.id }))}
            loading={loading}
          />
        </Card>

        <VehicleFormModal
          visible={modalVisible}
          onCancel={handleModalCancel}
          onSubmit={handleModalSubmit}
          initialValues={editingVehicle}
          title={editingVehicle ? '编辑车辆' : '新增车辆'}
        />
      </Spin>
    </MainLayout>
  )
}

export default VehicleManagement
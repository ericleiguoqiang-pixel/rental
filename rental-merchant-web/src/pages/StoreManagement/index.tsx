import React, { useState } from 'react'
import { Card, Table, Button, Space, Tag, Statistic, Row, Col, Spin, Popconfirm, message } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, UpOutlined, DownOutlined } from '@ant-design/icons'
import MainLayout from '../../components/MainLayout'
import StoreFormModal from '../../components/StoreFormModal'
import { useStores, Store } from '../../hooks/useStores'
import StoreServiceAreaManagement from '../../components/StoreServiceAreaManagement'

const StoreManagement: React.FC = () => {
  const { stores, loading, deleteStore, createStore, updateStore, onlineStore, offlineStore } = useStores()
  const [modalVisible, setModalVisible] = useState(false)
  const [editingStore, setEditingStore] = useState<Store | undefined>(undefined)
  const [serviceAreaStore, setServiceAreaStore] = useState<Store | undefined>(undefined)
  
  // 确保 stores 是数组
  const storeList = Array.isArray(stores) ? stores : []
  
  const handleDelete = async (id: string) => {
    try {
      await deleteStore(id)
    } catch (error) {
      // 错误信息已在 hook 中处理
    }
  }

  const handleAdd = () => {
    setEditingStore(undefined)
    setModalVisible(true)
  }

  const handleEdit = (store: Store) => {
    setEditingStore(store)
    setModalVisible(true)
  }

  const handleManageServiceArea = (store: Store) => {
    setServiceAreaStore(store)
  }

  // 处理门店上架
  const handleOnline = async (id: string) => {
    try {
      await onlineStore(id)
    } catch (error) {
      // 错误信息已在 hook 中处理
    }
  }

  // 处理门店下架
  const handleOffline = async (id: string) => {
    try {
      await offlineStore(id)
    } catch (error) {
      // 错误信息已在 hook 中处理
    }
  }

  const handleBackToStoreList = () => {
    setServiceAreaStore(undefined)
  }

  const handleModalSubmit = async (values: any) => {
    try {
      if (editingStore) {
        await updateStore(editingStore.id, values)
        message.success('门店更新成功')
      } else {
        await createStore(values)
        message.success('门店创建成功')
      }
      setModalVisible(false)
      setEditingStore(undefined)
    } catch (error) {
      // 错误信息已在 hook 中处理
    }
  }

  const handleModalCancel = () => {
    setModalVisible(false)
    setEditingStore(undefined)
  }
  
  const activeStores = storeList.filter(store => store.onlineStatus === 1 || store.status === 'active').length
  const inactiveStores = storeList.filter(store => store.onlineStatus === 0 || store.status === 'inactive').length
  const totalRevenue = 128000 // 这个需要从另外的API获取
  const columns = [
    {
      title: '门店名称',
      dataIndex: 'storeName',
      key: 'storeName',
      render: (text: string, record: Store) => record.storeName || record.name || text
    },
    {
      title: '城市',
      dataIndex: 'city',
      key: 'city',
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
      render: (phone: string) => phone || '暂无'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string, record: Store) => {
        // 优先使用 onlineStatusDesc，其次使用 status
        const isActive = record.onlineStatus === 1 || status === 'active'
        const statusText = record.onlineStatusDesc || (isActive ? '上架' : '下架')
        return (
          <Tag color={isActive ? 'green' : 'red'}>
            {statusText}
          </Tag>
        )
      },
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record: Store) => (
        <Space size="middle">
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          {record.onlineStatus === 1 ? (
            <Popconfirm
              title="下架门店"
              description="确定要下架这个门店吗？"
              onConfirm={() => handleOffline(record.id)}
              okText="确定"
              cancelText="取消"
            >
              <Button type="link" icon={<DownOutlined />}>下架</Button>
            </Popconfirm>
          ) : (
            <Popconfirm
              title="上架门店"
              description="确定要上架这个门店吗？"
              onConfirm={() => handleOnline(record.id)}
              okText="确定"
              cancelText="取消"
            >
              <Button type="link" icon={<UpOutlined />}>上架</Button>
            </Popconfirm>
          )}
          <Button type="link" onClick={() => handleManageServiceArea(record)}>服务范围管理</Button>
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

  const mockData = storeList

  // 如果选择了门店进行服务范围管理，则显示服务范围管理界面
  if (serviceAreaStore) {
    return (
      <MainLayout title="门店管理">
        <Button onClick={handleBackToStoreList} style={{ marginBottom: 16 }}>
          ← 返回门店列表
        </Button>
        <StoreServiceAreaManagement 
          storeId={serviceAreaStore.id} 
          storeName={serviceAreaStore.storeName || serviceAreaStore.name || ''} 
        />
      </MainLayout>
    )
  }

  return (
    <MainLayout title="门店管理">
      <Spin spinning={loading}>
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={6}>
            <Card>
              <Statistic title="门店总数" value={storeList.length} />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic title="上架门店" value={activeStores} />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic title="下架门店" value={inactiveStores} />
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
          extra={<Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>新增门店</Button>}
        >
          <Table 
            columns={columns} 
            dataSource={mockData.map(item => ({ ...item, key: item.id }))}
            loading={loading}
          />
        </Card>

        <StoreFormModal
          visible={modalVisible}
          onCancel={handleModalCancel}
          onSubmit={handleModalSubmit}
          initialValues={editingStore}
          title={editingStore ? '编辑门店' : '新增门店'}
        />
      </Spin>
    </MainLayout>
  )
}

export default StoreManagement
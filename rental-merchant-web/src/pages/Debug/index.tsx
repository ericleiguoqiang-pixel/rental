import React, { useState, useEffect } from 'react'
import { Card, Button, Typography, Space, Table } from 'antd'
import { storeAPI, vehicleAPI } from '../../services/api'

const { Title, Paragraph, Text } = Typography

const DebugPage: React.FC = () => {
  const [storeData, setStoreData] = useState<any>(null)
  const [vehicleData, setVehicleData] = useState<any>(null)
  const [storeError, setStoreError] = useState<string>('')
  const [vehicleError, setVehicleError] = useState<string>('')
  const [parsedStores, setParsedStores] = useState<any[]>([])

  const testStoreAPI = async () => {
    try {
      console.log('正在调用门店API...')
      const response = await storeAPI.getStores()
      console.log('门店API响应:', response)
      setStoreData(response)
      setStoreError('')
      
      // 解析数据
      let stores = []
      if (response?.data?.records && Array.isArray(response.data.records)) {
        stores = response.data.records
        console.log('解析的门店数据:', stores)
        setParsedStores(stores)
      }
    } catch (error) {
      console.error('门店API错误:', error)
      setStoreError(error instanceof Error ? error.message : '未知错误')
    }
  }

  const testVehicleAPI = async () => {
    try {
      console.log('正在调用车辆API...')
      const response = await vehicleAPI.getVehicles()
      console.log('车辆API响应:', response)
      setVehicleData(response)
      setVehicleError('')
    } catch (error) {
      console.error('车辆API错误:', error)
      setVehicleError(error instanceof Error ? error.message : '未知错误')
    }
  }

  useEffect(() => {
    testStoreAPI()
    testVehicleAPI()
  }, [])

  const storeColumns = [
    { title: 'ID', dataIndex: 'id', key: 'id' },
    { title: '门店名称', dataIndex: 'storeName', key: 'storeName' },
    { title: '城市', dataIndex: 'city', key: 'city' },
    { title: '地址', dataIndex: 'address', key: 'address' },
    { title: '上架状态', dataIndex: 'onlineStatus', key: 'onlineStatus' },
  ]

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>API调试页面</Title>
      
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Card title="门店API测试">
          <Space direction="vertical" style={{ width: '100%' }}>
            <Button onClick={testStoreAPI}>重新测试门店API</Button>
            {storeError && (
              <Text type="danger">错误: {storeError}</Text>
            )}
            
            <Title level={4}>原始响应数据:</Title>
            <Paragraph>
              <pre style={{ background: '#f5f5f5', padding: '12px', borderRadius: '4px', overflow: 'auto', maxHeight: '300px' }}>
                {storeData ? JSON.stringify(storeData, null, 2) : '无数据'}
              </pre>
            </Paragraph>
            
            {parsedStores.length > 0 && (
              <>
                <Title level={4}>解析后的门店列表:</Title>
                <Table 
                  columns={storeColumns} 
                  dataSource={parsedStores.map(item => ({...item, key: item.id}))} 
                  size="small"
                />
              </>
            )}
          </Space>
        </Card>

        <Card title="车辆API测试">
          <Space direction="vertical" style={{ width: '100%' }}>
            <Button onClick={testVehicleAPI}>重新测试车辆API</Button>
            {vehicleError && (
              <Text type="danger">错误: {vehicleError}</Text>
            )}
            <Paragraph>
              <Text strong>响应数据:</Text>
              <pre style={{ background: '#f5f5f5', padding: '12px', borderRadius: '4px', overflow: 'auto', maxHeight: '300px' }}>
                {vehicleData ? JSON.stringify(vehicleData, null, 2) : '无数据'}
              </pre>
            </Paragraph>
          </Space>
        </Card>
      </Space>
    </div>
  )
}

export default DebugPage
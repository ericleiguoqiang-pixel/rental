import { useState, useEffect } from 'react'
import { storeAPI } from '../services/api'
import { message } from 'antd'

export interface Store {
  id: string
  storeName: string  // 对应后端的 storeName
  city: string
  address: string
  longitude?: number
  latitude?: number
  businessStartTime?: string
  businessEndTime?: string
  auditStatus?: number
  auditStatusDesc?: string
  onlineStatus?: number
  onlineStatusDesc?: string
  minAdvanceHours?: number
  maxAdvanceDays?: number
  serviceFee?: number
  vehicleCount?: number
  createdTime?: string
  updatedTime?: string
  // 前端展示用的字段（基于后端数据计算）
  name?: string      // 由 storeName 赋值
  phone?: string     // 目前后端暂无此字段，可能需要附加信息
  status?: 'active' | 'inactive'  // 由 onlineStatus 转换
}

export const useStores = () => {
  const [stores, setStores] = useState<Store[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // 数据转换函数：将后端数据转换为前端需要的格式
  const transformStoreData = (backendData: any[]): Store[] => {
    return backendData.map((item: any) => ({
      id: String(item.id || ''),
      storeName: item.storeName || '',
      name: item.storeName || '', // 前端展示用
      city: item.city || '',
      address: item.address || '',
      longitude: item.longitude,
      latitude: item.latitude,
      businessStartTime: item.businessStartTime,
      businessEndTime: item.businessEndTime,
      auditStatus: item.auditStatus,
      auditStatusDesc: item.auditStatusDesc,
      onlineStatus: item.onlineStatus,
      onlineStatusDesc: item.onlineStatusDesc,
      minAdvanceHours: item.minAdvanceHours,
      maxAdvanceDays: item.maxAdvanceDays,
      serviceFee: item.serviceFee,
      vehicleCount: item.vehicleCount,
      createdTime: item.createdTime,
      updatedTime: item.updatedTime,
      // 前端展示字段
      phone: '暂无', // 后端暂无此字段
      status: item.onlineStatus === 1 ? 'active' : 'inactive' as 'active' | 'inactive'
    }))
  }

  const fetchStores = async () => {
    try {
      setLoading(true)
      setError(null)
      const response = await storeAPI.getStores()
      console.log('门店API返回数据:', response)
      
      // 检查响应数据结构
      let storeData = []
      if (response?.data) {
        // 如果是分页格式，取 records 字段
        if (response.data.records && Array.isArray(response.data.records)) {
          storeData = response.data.records
        }
        // 如果直接是数组格式
        else if (Array.isArray(response.data)) {
          storeData = response.data
        }
      }
      
      console.log('解析后的门店数据:', storeData)
      
      if (storeData.length > 0) {
        const transformedData = transformStoreData(storeData)
        console.log('转换后的门店数据:', transformedData)
        setStores(transformedData)
      } else {
        console.warn('门店数据为空，使用Mock数据')
        // 使用mock数据
        setStores([
          {
            id: '1',
            storeName: '总店',
            name: '总店',
            city: '上海',
            address: '上海市浦东新区陆家嘴金融中心',
            phone: '021-12345678',
            status: 'active',
            onlineStatus: 1,
          },
          {
            id: '2',
            storeName: '虹桥分店',
            name: '虹桥分店',
            city: '上海',
            address: '上海市长宁区虹桥路1号',
            phone: '021-87654321',
            status: 'active',
            onlineStatus: 1,
          },
        ])
      }
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '获取门店列表失败'
      setError(errorMsg)
      message.error(errorMsg)
      // 如果API调用失败，使用mock数据
      setStores([
        {
          id: '1',
          storeName: '总店',
          name: '总店',
          city: '上海',
          address: '上海市浦东新区陆家嘴金融中心',
          phone: '021-12345678',
          status: 'active',
          onlineStatus: 1,
        },
        {
          id: '2',
          storeName: '虹桥分店',
          name: '虹桥分店',
          city: '上海',
          address: '上海市长宁区虹桥路1号',
          phone: '021-87654321',
          status: 'active',
          onlineStatus: 1,
        },
      ])
    } finally {
      setLoading(false)
    }
  }

  const createStore = async (storeData: Omit<Store, 'id'>) => {
    try {
      setLoading(true)
      await storeAPI.createStore(storeData)
      message.success('门店创建成功')
      await fetchStores() // 重新获取列表
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '创建门店失败'
      message.error(errorMsg)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const updateStore = async (id: string, storeData: Partial<Store>) => {
    try {
      setLoading(true)
      await storeAPI.updateStore(id, storeData)
      message.success('门店更新成功')
      await fetchStores() // 重新获取列表
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '更新门店失败'
      message.error(errorMsg)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const deleteStore = async (id: string) => {
    try {
      setLoading(true)
      await storeAPI.deleteStore(id)
      message.success('门店删除成功')
      await fetchStores() // 重新获取列表
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '删除门店失败'
      message.error(errorMsg)
      throw err
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchStores()
  }, [])

  return {
    stores,
    loading,
    error,
    fetchStores,
    createStore,
    updateStore,
    deleteStore,
  }
}
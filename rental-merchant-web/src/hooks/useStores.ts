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

  const fetchStores = async () => {
    try {
      setLoading(true)
      setError(null)
      
      // 调用API获取门店列表
      const response: any = await storeAPI.getStores()
      
      // 检查响应数据结构
      let storeData: Store[] = []
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
      
      // 数据转换：确保字段名匹配
      const convertedStores = storeData.map(store => ({
        ...store,
        // 确保有 name 字段（用于前端显示）
        name: store.storeName || store.name || '',
        // 状态转换
        status: store.onlineStatus === 1 ? 'active' as const : 'inactive' as const
      }))
      
      setStores(convertedStores)
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

  // 添加门店上架功能
  const onlineStore = async (id: string) => {
    try {
      setLoading(true)
      await storeAPI.onlineStore(id)
      message.success('门店上架成功')
      await fetchStores() // 重新获取列表
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '门店上架失败'
      message.error(errorMsg)
      throw err
    } finally {
      setLoading(false)
    }
  }

  // 添加门店下架功能
  const offlineStore = async (id: string) => {
    try {
      setLoading(true)
      await storeAPI.offlineStore(id)
      message.success('门店下架成功')
      await fetchStores() // 重新获取列表
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '门店下架失败'
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
    onlineStore,    // 导出上架功能
    offlineStore,   // 导出下架功能
  }
}
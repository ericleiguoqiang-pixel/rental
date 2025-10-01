import { useState, useEffect } from 'react'
import { storeAPI } from '../services/api'
import { message } from 'antd'

export interface Store {
  id: string
  name: string
  address: string
  phone: string
  status: 'active' | 'inactive'
  createdAt?: string
  updatedAt?: string
}

export const useStores = () => {
  const [stores, setStores] = useState<Store[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchStores = async () => {
    try {
      setLoading(true)
      setError(null)
      const response = await storeAPI.getStores()
      setStores(response.data || [])
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '获取门店列表失败'
      setError(errorMsg)
      message.error(errorMsg)
      // 如果API调用失败，使用mock数据
      setStores([
        {
          id: '1',
          name: '总店',
          address: '上海市浦东新区陆家嘴金融中心',
          phone: '021-12345678',
          status: 'active',
        },
        {
          id: '2',
          name: '虹桥分店',
          address: '上海市长宁区虹桥路1号',
          phone: '021-87654321',
          status: 'active',
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
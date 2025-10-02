import { useState, useEffect } from 'react'
import { orderAPI } from '../services/api'
import { message } from 'antd'

export interface Order {
  id: string
  orderNo: string
  customerName: string
  vehicleInfo: string
  duration: string
  totalAmount: number
  status: 'pending' | 'confirmed' | 'ongoing' | 'completed' | 'cancelled'
  createdAt?: string
  updatedAt?: string
}

export const useOrders = () => {
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchOrders = async () => {
    try {
      setLoading(true)
      setError(null)
      const response = await orderAPI.getOrders()
      // 确保返回的数据是数组格式
      const orderList = Array.isArray(response?.data) ? response.data : []
      setOrders(orderList)
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '获取订单列表失败'
      setError(errorMsg)
      message.error(errorMsg)
      // 如果API调用失败，使用mock数据
      setOrders([
        {
          id: '1',
          orderNo: 'R20231201001',
          customerName: '张三',
          vehicleInfo: '沪A12345 大众朗逸',
          duration: '2023-12-01 至 2023-12-03',
          totalAmount: 450,
          status: 'ongoing',
        },
        {
          id: '2',
          orderNo: 'R20231201002',
          customerName: '李四',
          vehicleInfo: '沪B67890 日产轩逸',
          duration: '2023-12-02 至 2023-12-05',
          totalAmount: 560,
          status: 'confirmed',
        },
        {
          id: '3',
          orderNo: 'R20231130001',
          customerName: '王五',
          vehicleInfo: '沪C11111 特斯拉Model 3',
          duration: '2023-11-30 至 2023-12-01',
          totalAmount: 600,
          status: 'completed',
        },
      ])
    } finally {
      setLoading(false)
    }
  }

  const getOrderDetail = async (id: string) => {
    try {
      setLoading(true)
      const response = await orderAPI.getOrderDetail(id)
      return response.data
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '获取订单详情失败'
      message.error(errorMsg)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const updateOrderStatus = async (id: string, status: string) => {
    try {
      setLoading(true)
      await orderAPI.updateOrderStatus(id, status)
      message.success('订单状态更新成功')
      await fetchOrders() // 重新获取列表
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '更新订单状态失败'
      message.error(errorMsg)
      throw err
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchOrders()
  }, [])

  return {
    orders,
    loading,
    error,
    fetchOrders,
    getOrderDetail,
    updateOrderStatus,
  }
}
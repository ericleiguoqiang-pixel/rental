import { useState, useEffect } from 'react'
import { orderAPI } from '../services/api'
import { message } from 'antd'

export interface Order {
  id: string
  orderNo: string
  driverName: string
  driverPhone: string
  licensePlate: string
  startTime: string
  endTime: string
  actualPickupTime?: string
  actualReturnTime?: string
  orderStatus: number
  pickupDriver?: string
  returnDriver?: string
  totalAmount: number
  createdAt?: string
  updatedAt?: string
}

export const useOrders = () => {
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchOrders = async (params?: { current?: number; size?: number; orderNo?: string; status?: number }) => {
    try {
      setLoading(true)
      setError(null)
      const response = await orderAPI.getOrders(params)
      // 确保返回的数据是数组格式
      const orderList = Array.isArray(response?.data?.records) ? response.data.records : []
      setOrders(orderList)
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '获取订单列表失败'
      setError(errorMsg)
      message.error(errorMsg)
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

  const assignPickupDriver = async (id: string, driverName: string) => {
    try {
      setLoading(true)
      await orderAPI.assignPickupDriver(id, driverName)
      message.success('分配取车司机成功')
      await fetchOrders() // 重新获取列表
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '分配取车司机失败'
      message.error(errorMsg)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const assignReturnDriver = async (id: string, driverName: string) => {
    try {
      setLoading(true)
      await orderAPI.assignReturnDriver(id, driverName)
      message.success('分配还车司机成功')
      await fetchOrders() // 重新获取列表
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '分配还车司机失败'
      message.error(errorMsg)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const confirmPickup = async (id: string) => {
    try {
      setLoading(true)
      await orderAPI.confirmPickup(id)
      message.success('确认取车成功')
      await fetchOrders() // 重新获取列表
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '确认取车失败'
      message.error(errorMsg)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const confirmReturn = async (id: string) => {
    try {
      setLoading(true)
      await orderAPI.confirmReturn(id)
      message.success('确认还车成功')
      await fetchOrders() // 重新获取列表
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '确认还车失败'
      message.error(errorMsg)
      throw err
    } finally {
      setLoading(false)
    }
  }

  return {
    orders,
    loading,
    error,
    fetchOrders,
    getOrderDetail,
    assignPickupDriver,
    assignReturnDriver,
    confirmPickup,
    confirmReturn,
  }
}
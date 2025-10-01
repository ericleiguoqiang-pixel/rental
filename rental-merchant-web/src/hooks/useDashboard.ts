import { useState, useEffect } from 'react'
import { request } from '../services/api'
import { message } from 'antd'

export interface DashboardStats {
  todayOrders: number
  todayRevenue: number
  availableVehicles: number
  rentedVehicles: number
}

export const useDashboardStats = () => {
  const [stats, setStats] = useState<DashboardStats>({
    todayOrders: 0,
    todayRevenue: 0,
    availableVehicles: 0,
    rentedVehicles: 0,
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchStats = async () => {
    try {
      setLoading(true)
      setError(null)
      const response = await request('/dashboard/stats')
      setStats(response.data || {
        todayOrders: 0,
        todayRevenue: 0,
        availableVehicles: 0,
        rentedVehicles: 0,
      })
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '获取仪表盘数据失败'
      setError(errorMsg)
      // 如果API调用失败，使用mock数据
      setStats({
        todayOrders: 12,
        todayRevenue: 8560,
        availableVehicles: 28,
        rentedVehicles: 15,
      })
      console.warn('仪表盘API调用失败，使用模拟数据:', errorMsg)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchStats()
  }, [])

  return {
    stats,
    loading,
    error,
    fetchStats,
  }
}
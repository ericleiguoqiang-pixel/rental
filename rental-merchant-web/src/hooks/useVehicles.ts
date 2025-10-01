import { useState, useEffect } from 'react'
import { vehicleAPI } from '../services/api'
import { message } from 'antd'

export interface Vehicle {
  id: string
  plateNumber: string
  model: string
  brand: string
  color: string
  status: 'available' | 'rented' | 'maintenance' | 'inactive'
  dailyRate: number
  createdAt?: string
  updatedAt?: string
}

export const useVehicles = () => {
  const [vehicles, setVehicles] = useState<Vehicle[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchVehicles = async () => {
    try {
      setLoading(true)
      setError(null)
      const response = await vehicleAPI.getVehicles()
      setVehicles(response.data || [])
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '获取车辆列表失败'
      setError(errorMsg)
      message.error(errorMsg)
      // 如果API调用失败，使用mock数据
      setVehicles([
        {
          id: '1',
          plateNumber: '沪A12345',
          model: '朗逸',
          brand: '大众',
          color: '白色',
          status: 'available',
          dailyRate: 150,
        },
        {
          id: '2',
          plateNumber: '沪B67890',
          model: '轩逸',
          brand: '日产',
          color: '银色',
          status: 'rented',
          dailyRate: 140,
        },
        {
          id: '3',
          plateNumber: '沪C11111',
          model: 'Model 3',
          brand: '特斯拉',
          color: '黑色',
          status: 'maintenance',
          dailyRate: 300,
        },
      ])
    } finally {
      setLoading(false)
    }
  }

  const createVehicle = async (vehicleData: Omit<Vehicle, 'id'>) => {
    try {
      setLoading(true)
      await vehicleAPI.createVehicle(vehicleData)
      message.success('车辆创建成功')
      await fetchVehicles() // 重新获取列表
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '创建车辆失败'
      message.error(errorMsg)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const updateVehicle = async (id: string, vehicleData: Partial<Vehicle>) => {
    try {
      setLoading(true)
      await vehicleAPI.updateVehicle(id, vehicleData)
      message.success('车辆更新成功')
      await fetchVehicles() // 重新获取列表
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '更新车辆失败'
      message.error(errorMsg)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const deleteVehicle = async (id: string) => {
    try {
      setLoading(true)
      await vehicleAPI.deleteVehicle(id)
      message.success('车辆删除成功')
      await fetchVehicles() // 重新获取列表
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '删除车辆失败'
      message.error(errorMsg)
      throw err
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchVehicles()
  }, [])

  return {
    vehicles,
    loading,
    error,
    fetchVehicles,
    createVehicle,
    updateVehicle,
    deleteVehicle,
  }
}
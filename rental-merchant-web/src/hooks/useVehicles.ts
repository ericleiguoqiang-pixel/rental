import { useState, useEffect } from 'react'
import { vehicleAPI } from '../services/api'
import { message } from 'antd'

export interface Vehicle {
  id: string
  storeId?: number
  storeName?: string
  licensePlate: string  // 对应后端的 licensePlate
  carModelId?: number
  carModel?: any  // 车型信息
  licenseType?: number
  licenseTypeDesc?: string
  registerDate?: string
  vin?: string
  engineNo?: string
  usageNature?: number
  usageNatureDesc?: string
  auditStatus?: number
  auditStatusDesc?: string
  onlineStatus?: number
  onlineStatusDesc?: string
  vehicleStatus?: number
  vehicleStatusDesc?: string
  mileage?: number
  createdTime?: string
  updatedTime?: string
  // 前端展示用的字段（基于后端数据计算）
  plateNumber?: string   // 由 licensePlate 赋值
  model?: string        // 由 carModel.model 赋值
  brand?: string        // 由 carModel.brand 赋值
  color?: string        // 后端暂无此字段
  status?: 'available' | 'rented' | 'maintenance' | 'inactive'  // 由 vehicleStatus 转换
  dailyRate?: number    // 后端暂无此字段，可能在价格管理中
}

export const useVehicles = () => {
  const [vehicles, setVehicles] = useState<Vehicle[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // 数据转换函数：将后端数据转换为前端需要的格式
  const transformVehicleData = (backendData: any[]): Vehicle[] => {
    return backendData.map((item: any) => ({
      id: String(item.id || ''),
      storeId: item.storeId,
      storeName: item.storeName,
      licensePlate: item.licensePlate || '',
      carModelId: item.carModelId,
      carModel: item.carModel,
      licenseType: item.licenseType,
      licenseTypeDesc: item.licenseTypeDesc,
      registerDate: item.registerDate,
      vin: item.vin,
      engineNo: item.engineNo,
      usageNature: item.usageNature,
      usageNatureDesc: item.usageNatureDesc,
      auditStatus: item.auditStatus,
      auditStatusDesc: item.auditStatusDesc,
      onlineStatus: item.onlineStatus,
      onlineStatusDesc: item.onlineStatusDesc,
      vehicleStatus: item.vehicleStatus,
      vehicleStatusDesc: item.vehicleStatusDesc,
      mileage: item.mileage,
      createdTime: item.createdTime,
      updatedTime: item.updatedTime,
      // 前端展示字段
      plateNumber: item.licensePlate || '',
      model: item.carModel?.model || '未知车型',
      brand: item.carModel?.brand || '未知品牌',
      color: '白色', // 后端暂无此字段
      status: (() => {
        switch (item.vehicleStatus) {
          case 1: return 'available'
          case 2: return 'rented'
          case 3: return 'maintenance'
          case 4: return 'inactive'
          default: return 'available'
        }
      })() as 'available' | 'rented' | 'maintenance' | 'inactive',
      dailyRate: 150 // 后端暂无此字段，使用默认值
    }))
  }

  const fetchVehicles = async () => {
    try {
      setLoading(true)
      setError(null)
      const response = await vehicleAPI.getVehicles()
      console.log('车辆API返回数据:', response)
      
      // 检查响应数据结构
      let vehicleData = []
      if (response?.data) {
        // 如果是分页格式，取 records 字段
        if (response.data.records && Array.isArray(response.data.records)) {
          vehicleData = response.data.records
        }
        // 如果直接是数组格式
        else if (Array.isArray(response.data)) {
          vehicleData = response.data
        }
      }
      
      console.log('解析后的车辆数据:', vehicleData)
      
      if (vehicleData.length > 0) {
        const transformedData = transformVehicleData(vehicleData)
        console.log('转换后的车辆数据:', transformedData)
        setVehicles(transformedData)
      } else {
        console.warn('车辆数据为空，使用Mock数据')
        // 使用mock数据
        setVehicles([
          {
            id: '1',
            licensePlate: '沪A12345',
            plateNumber: '沪A12345',
            model: '朗逸',
            brand: '大众',
            color: '白色',
            status: 'available',
            dailyRate: 150,
          },
          {
            id: '2',
            licensePlate: '沪B67890',
            plateNumber: '沪B67890',
            model: '轩逸',
            brand: '日产',
            color: '银色',
            status: 'rented',
            dailyRate: 140,
          },
          {
            id: '3',
            licensePlate: '沪C11111',
            plateNumber: '沪C11111',
            model: 'Model 3',
            brand: '特斯拉',
            color: '黑色',
            status: 'maintenance',
            dailyRate: 300,
          },
        ])
      }
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '获取车辆列表失败'
      setError(errorMsg)
      message.error(errorMsg)
      // 如果API调用失败，使用mock数据
      setVehicles([
        {
          id: '1',
          licensePlate: '沪A12345',
          plateNumber: '沪A12345',
          model: '朗逸',
          brand: '大众',
          color: '白色',
          status: 'available',
          dailyRate: 150,
        },
        {
          id: '2',
          licensePlate: '沪B67890',
          plateNumber: '沪B67890',
          model: '轩逸',
          brand: '日产',
          color: '银色',
          status: 'rented',
          dailyRate: 140,
        },
        {
          id: '3',
          licensePlate: '沪C11111',
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
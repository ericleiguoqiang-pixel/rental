import { useState, useEffect } from 'react'
import { carModelAPI } from '../services/api'
import { message } from 'antd'

export interface CarModel {
  id: number
  brand: string
  series: string
  model: string
  year: number
  transmission: number
  transmissionDesc: string
  driveType: number
  driveTypeDesc: string
  seatCount: number
  doorCount: number
  category?: string
  description?: string
}

export const useCarModels = () => {
  const [carModels, setCarModels] = useState<CarModel[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchCarModels = async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await carModelAPI.getAllCarModels()
      if (response.success) {
        setCarModels(response.data || [])
      } else {
        throw new Error(response.message || '获取车型数据失败')
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '获取车型数据失败'
      setError(errorMessage)
      message.error(errorMessage)
      setCarModels([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchCarModels()
  }, [])

  return {
    carModels,
    loading,
    error,
    refetch: fetchCarModels
  }
}

export const useCarModelsByBrand = (brand?: string) => {
  const [carModels, setCarModels] = useState<CarModel[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchCarModelsByBrand = async (brandName: string) => {
    setLoading(true)
    setError(null)
    try {
      const response = await carModelAPI.getCarModelsByBrand(brandName)
      if (response.success) {
        setCarModels(response.data || [])
      } else {
        throw new Error(response.message || '获取车型数据失败')
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '获取车型数据失败'
      setError(errorMessage)
      message.error(errorMessage)
      setCarModels([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (brand) {
      fetchCarModelsByBrand(brand)
    } else {
      setCarModels([])
    }
  }, [brand])

  return {
    carModels,
    loading,
    error,
    refetch: brand ? () => fetchCarModelsByBrand(brand) : () => {}
  }
}

export const useBrands = () => {
  const [brands, setBrands] = useState<string[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchBrands = async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await carModelAPI.getAllBrands()
      if (response.success) {
        setBrands(response.data || [])
      } else {
        throw new Error(response.message || '获取品牌数据失败')
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '获取品牌数据失败'
      setError(errorMessage)
      message.error(errorMessage)
      setBrands([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchBrands()
  }, [])

  return {
    brands,
    loading,
    error,
    refetch: fetchBrands
  }
}
import { useState, useEffect } from 'react'
import { request } from '../services/api'
import { message } from 'antd'

export interface MerchantApplication {
  id: string
  companyName: string
  businessLicense: string
  legalPerson: string
  contactPhone: string
  contactEmail: string
  businessAddress: string
  businessScope: string
  registeredCapital: string
  establishedDate: string
  contactPersonName: string
  contactPersonPhone: string
  contactPersonEmail: string
  username: string
  status: 'pending' | 'approved' | 'rejected' | 'reviewing'
  auditReason?: string
  createdAt: string
  updatedAt?: string
}

export const useMerchantAudit = () => {
  const [merchants, setMerchants] = useState<MerchantApplication[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchMerchants = async () => {
    try {
      setLoading(true)
      setError(null)
      const response = await request('/merchant/applications')
      setMerchants(response.data || [])
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '获取商户申请列表失败'
      setError(errorMsg)
      message.error(errorMsg)
      // 如果API调用失败，使用mock数据
      setMerchants([
        {
          id: '1',
          companyName: '上海某某汽车租赁有限公司',
          businessLicense: '91310000MA1234567X',
          legalPerson: '张三',
          contactPhone: '13800138001',
          contactEmail: 'contact@example.com',
          businessAddress: '上海市浦东新区张江高科技园区',
          businessScope: '汽车租赁服务，汽车销售',
          registeredCapital: '500',
          establishedDate: '2020-01-15',
          contactPersonName: '李四',
          contactPersonPhone: '13800138002',
          contactPersonEmail: 'lisi@example.com',
          username: 'merchant001',
          status: 'pending',
          createdAt: '2023-12-01T10:00:00Z',
        },
        {
          id: '2',
          companyName: '北京汽车服务有限公司',
          businessLicense: '91110000MA9876543Y',
          legalPerson: '王五',
          contactPhone: '13800138003',
          contactEmail: 'info@beijing-auto.com',
          businessAddress: '北京市朝阳区CBD核心区',
          businessScope: '汽车租赁，汽车维修保养',
          registeredCapital: '1000',
          establishedDate: '2019-06-20',
          contactPersonName: '赵六',
          contactPersonPhone: '13800138004',
          contactPersonEmail: 'zhaoliu@beijing-auto.com',
          username: 'merchant002',
          status: 'approved',
          auditReason: '资质完整，审核通过',
          createdAt: '2023-11-28T14:30:00Z',
        },
        {
          id: '3',
          companyName: '深圳快车租赁公司',
          businessLicense: '91440300MA5555666Z',
          legalPerson: '孙七',
          contactPhone: '13800138005',
          contactEmail: 'service@shenzhen-rental.com',
          businessAddress: '深圳市南山区科技园',
          businessScope: '汽车租赁服务',
          registeredCapital: '300',
          establishedDate: '2021-03-10',
          contactPersonName: '周八',
          contactPersonPhone: '13800138006',
          contactPersonEmail: 'zhouba@shenzhen-rental.com',
          username: 'merchant003',
          status: 'rejected',
          auditReason: '注册资本不足，营业执照信息不完整',
          createdAt: '2023-11-25T09:15:00Z',
        },
      ])
    } finally {
      setLoading(false)
    }
  }

  const updateMerchantStatus = async (id: string, status: string, reason: string) => {
    try {
      setLoading(true)
      await request(`/merchant/applications/${id}/audit`, {
        method: 'PUT',
        body: JSON.stringify({ status, reason }),
      })
      message.success('审核操作完成')
      await fetchMerchants() // 重新获取列表
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : '审核操作失败'
      message.error(errorMsg)
      throw err
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchMerchants()
  }, [])

  return {
    merchants,
    loading,
    error,
    fetchMerchants,
    updateMerchantStatus,
  }
}
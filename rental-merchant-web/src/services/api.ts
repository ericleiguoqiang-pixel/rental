// API服务基础配置
const API_BASE_URL = '/api'

// 通用请求函数
export const request = async (url: string, options: RequestInit = {}) => {
  const token = localStorage.getItem('token')
  // 获取租户ID（这里假设从某个地方获取，比如localStorage或全局状态）
  const tenantId = localStorage.getItem('tenantId') || '1'; // 默认值为1，实际应该从认证信息中获取
  
  const config: RequestInit = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
      'X-Tenant-Id': tenantId,
      ...options.headers,
    },
    ...options,
  }

  try {
    const response = await fetch(`${API_BASE_URL}${url}`, config)
    
    if (!response.ok) {
      if (response.status === 401) {
        // Token 过期或无效，跳转到登录页
        localStorage.removeItem('token')
        window.location.href = '/login'
        throw new Error('身份验证失效，请重新登录')
      }
      throw new Error(`请求失败: ${response.status}`)
    }
    
    const data = await response.json()
    return data
  } catch (error) {
    console.error('API request failed:', error)
    throw error
  }
}

// 门店管理API
export const storeAPI = {
  // 获取门店列表
  getStores: () => request('/stores?current=1&size=100'), // 获取前100条记录
  
  // 创建门店
  createStore: (data: any) => request('/stores', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  
  // 更新门店
  updateStore: (id: string, data: any) => request(`/stores/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  }),
  
  // 删除门店
  deleteStore: (id: string) => request(`/stores/${id}`, {
    method: 'DELETE',
  }),
}

// 车辆管理API
export const vehicleAPI = {
  // 获取车辆列表
  getVehicles: () => request('/vehicles?current=1&size=100'), // 获取前100条记录
  
  // 创建车辆
  createVehicle: (data: any) => request('/vehicles', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  
  // 更新车辆
  updateVehicle: (id: string, data: any) => request(`/vehicles/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  }),
  
  // 删除车辆
  deleteVehicle: (id: string) => request(`/vehicles/${id}`, {
    method: 'DELETE',
  }),
}

// 订单管理API
export const orderAPI = {
  // 获取订单列表
  getOrders: () => request('/orders'),
  
  // 获取订单详情
  getOrderDetail: (id: string) => request(`/orders/${id}`),
  
  // 更新订单状态
  updateOrderStatus: (id: string, status: string) => request(`/orders/${id}/status`, {
    method: 'PUT',
    body: JSON.stringify({ status }),
  }),
}

// 用户认证API
export const authAPI = {
  // 用户登录
  login: (credentials: { username: string; password: string; captcha: string; captchaKey: string }) => 
    request('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
    }),
  
  // 刷新令牌
  refreshToken: (refreshToken: string) => 
    request('/auth/refresh', {
      method: 'POST',
      body: JSON.stringify({ refreshToken }),
    }),
  
  // 获取验证码
  getCaptcha: () => request('/auth/captcha'),
  
  // 用户登出
  logout: (token: string) => 
    request('/auth/logout', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    }),
}

// 仪表盘API
export const dashboardAPI = {
  // 获取仪表盘统计数据
  getStats: () => request('/dashboard/stats'),
}

// 车型管理API
export const carModelAPI = {
  // 获取所有车型
  getAllCarModels: () => request('/car-models/all'),
  
  // 分页查询车型
  getCarModels: (params?: any) => {
    const queryString = params ? new URLSearchParams(params).toString() : ''
    return request(`/car-models${queryString ? '?' + queryString : ''}`)
  },
  
  // 根据品牌查询车型
  getCarModelsByBrand: (brand: string) => request(`/car-models/brand/${brand}`),
  
  // 获取所有品牌
  getAllBrands: () => request('/car-models/brands'),
  
  // 根据品牌获取车系
  getSeriesByBrand: (brand: string) => request(`/car-models/series?brand=${brand}`),
}

// 商户API
export const merchantAPI = {
  // 商户注册
  register: (data: any) => request('/merchant/register', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  
  // 获取商户申请列表
  getApplications: () => request('/merchant/applications'),
  
  // 审核商户申请
  auditApplication: (id: string, data: { status: string; reason: string }) => 
    request(`/merchant/applications/${id}/audit`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
  
  // 获取商户申请详情
  getApplicationDetail: (id: string) => request(`/merchant/applications/${id}`),
}

// 服务范围API
export const serviceAreaApi = {
  // 根据门店ID查询服务范围列表
  listByStoreId: (storeId: number) => request(`/service-area/list?storeId=${storeId}`),
  
  // 分页查询服务范围
  pageServiceAreas: (params: { pageNo?: number; pageSize?: number; storeId?: number; areaType?: number }) => {
    const queryString = new URLSearchParams(params as any).toString();
    return request(`/service-area/page${queryString ? '?' + queryString : ''}`);
  },
  
  // 创建服务范围
  create: (data: any) => request('/service-area/save', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  
  // 更新服务范围
  update: (data: any) => request('/service-area/update', {
    method: 'PUT',
    body: JSON.stringify(data),
  }),
  
  // 删除服务范围
  delete: (id: number) => request(`/service-area/delete/${id}`, {
    method: 'DELETE',
  }),
}
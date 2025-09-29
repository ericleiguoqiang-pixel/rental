// API服务基础配置
const API_BASE_URL = '/api'

// 通用请求函数
export const request = async (url: string, options: RequestInit = {}) => {
  const config: RequestInit = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  }

  try {
    const response = await fetch(`${API_BASE_URL}${url}`, config)
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
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
  getStores: () => request('/stores'),
  
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
  getVehicles: () => request('/vehicles'),
  
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
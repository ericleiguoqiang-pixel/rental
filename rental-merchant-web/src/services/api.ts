// API服务基础配置
const API_BASE_URL = '/api'
const AI_BASE_URL = '/ai'

// 通用请求函数
export const request = async (url: string, options: RequestInit = {}) => {
  const token = localStorage.getItem('token')
  // 从用户信息中获取租户ID
  let tenantId = '1'; // 默认值
  try {
    const userInfoStr = localStorage.getItem('userInfo');
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr);
      if (userInfo.tenantId) {
        tenantId = userInfo.tenantId.toString();
      }
    }
  } catch (e) {
    console.warn('解析用户信息失败:', e);
  }
  
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
      
      // 尝试解析错误响应
      let errorMsg = `请求失败: ${response.status}`;
      try {
        const errorData = await response.json();
        if (errorData.message) {
          errorMsg = errorData.message;
        }
      } catch (e) {
        // 解析失败，使用默认错误信息
      }
      
      throw new Error(errorMsg);
    }
    
    const data = await response.json();
    
    // 检查业务响应是否成功
    if (data && data.code !== 200) {
      throw new Error(data.message || '业务处理失败');
    }
    
    return data;
  } catch (error) {
    console.error('API request failed:', error);
    throw error;
  }
}

// AI服务专用请求函数
export const aiRequest = async (url: string, options: RequestInit = {}) => {
  const token = localStorage.getItem('token')
  // 从用户信息中获取租户ID
  let tenantId = '1'; // 默认值
  try {
    const userInfoStr = localStorage.getItem('userInfo');
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr);
      if (userInfo.tenantId) {
        tenantId = userInfo.tenantId.toString();
      }
    }
  } catch (e) {
    console.warn('解析用户信息失败:', e);
  }
  
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
    const response = await fetch(`${AI_BASE_URL}${url}`, config)
    
    if (!response.ok) {
      if (response.status === 401) {
        // Token 过期或无效，跳转到登录页
        localStorage.removeItem('token')
        window.location.href = '/login'
        throw new Error('身份验证失效，请重新登录')
      }
      
      // 尝试解析错误响应
      let errorMsg = `请求失败: ${response.status}`;
      try {
        const errorData = await response.json();
        if (errorData.message) {
          errorMsg = errorData.message;
        }
      } catch (e) {
        // 解析失败，使用默认错误信息
      }
      
      throw new Error(errorMsg);
    }
    
    // AI服务直接返回响应数据，不需要检查code字段
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('AI API request failed:', error);
    throw error;
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
  
  // 门店上架
  onlineStore: (id: string) => request(`/stores/${id}/online`, {
    method: 'POST',
  }),
  
  // 门店下架
  offlineStore: (id: string) => request(`/stores/${id}/offline`, {
    method: 'POST',
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
  getOrders: (params?: { current?: number; size?: number; orderNo?: string; status?: number }) => {
    // 过滤掉值为undefined的参数
    const filteredParams: Record<string, any> = {};
    if (params) {
      Object.keys(params).forEach(key => {
        if (params[key as keyof typeof params] !== undefined) {
          filteredParams[key] = params[key as keyof typeof params];
        }
      });
    }
    
    const queryString = Object.keys(filteredParams).length > 0 ? new URLSearchParams(filteredParams).toString() : '';
    return request(`/orders${queryString ? '?' + queryString : ''}`);
  },
  
  // 获取订单详情
  getOrderDetail: (id: string) => request(`/orders/${id}`),
  
  // 分配取车司机
  assignPickupDriver: (id: string, driverName: string) => request(`/orders/${id}/pickup-driver?driverName=${encodeURIComponent(driverName)}`, {
    method: 'PUT',
  }),
  
  // 分配还车司机
  assignReturnDriver: (id: string, driverName: string) => request(`/orders/${id}/return-driver?driverName=${encodeURIComponent(driverName)}`, {
    method: 'PUT',
  }),
  
  // 确认取车
  confirmPickup: (id: string) => request(`/orders/${id}/confirm-pickup`, {
    method: 'PUT',
  }),
  
  // 确认还车
  confirmReturn: (id: string) => request(`/orders/${id}/confirm-return`, {
    method: 'PUT',
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
    // 过滤掉值为undefined的参数
    const filteredParams: Record<string, any> = {};
    if (params) {
      Object.keys(params).forEach(key => {
        if (params[key] !== undefined) {
          filteredParams[key] = params[key];
        }
      });
    }
    
    const queryString = Object.keys(filteredParams).length > 0 ? new URLSearchParams(filteredParams).toString() : '';
    return request(`/car-models${queryString ? '?' + queryString : ''}`);
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
  register: (data: any) => request('/merchants/apply', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  
  // 获取商户申请列表
  getApplications: () => request('/merchants/applications'),
  
  // 审核商户申请
  auditApplication: (id: string, data: { status: string; reason: string }) => 
    request(`/merchants/applications/${id}/audit`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
  
  // 获取商户申请详情
  getApplicationDetail: (id: string) => request(`/merchants/applications/${id}`),
}

// 服务范围API
export const serviceAreaApi = {
  // 根据门店ID查询服务范围列表
  listByStoreId: (storeId: number) => request(`/service-area/list?storeId=${storeId}`),
  
  // 分页查询服务范围
  pageServiceAreas: (params: { pageNo?: number; pageSize?: number; storeId?: number; areaType?: number }) => {
    // 过滤掉值为undefined的参数
    const filteredParams: Record<string, any> = {};
    if (params) {
      Object.keys(params).forEach(key => {
        if (params[key as keyof typeof params] !== undefined) {
          filteredParams[key] = params[key as keyof typeof params];
        }
      });
    }
    
    const queryString = Object.keys(filteredParams).length > 0 ? new URLSearchParams(filteredParams).toString() : '';
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

// 商品管理API
export const productAPI = {
  // 创建车型商品
  createProduct: (data: any) => request('/car-model-products', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  
  // 更新车型商品
  updateProduct: (id: number, data: any) => request(`/car-model-products/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  }),
  
  // 删除车型商品
  deleteProduct: (id: number) => request(`/car-model-products/${id}`, {
    method: 'DELETE',
  }),
  
  // 获取车型商品详情
  getProduct: (id: number) => request(`/car-model-products/${id}`),
  
  // 分页查询车型商品
  getProductList: (params?: any) => {
    // 过滤掉值为undefined的参数
    const filteredParams: Record<string, any> = {};
    if (params) {
      Object.keys(params).forEach(key => {
        if (params[key] !== undefined) {
          filteredParams[key] = params[key];
        }
      });
    }
    
    const queryString = Object.keys(filteredParams).length > 0 ? new URLSearchParams(filteredParams).toString() : '';
    return request(`/car-model-products${queryString ? '?' + queryString : ''}`);
  },
  
  // 上架车型商品
  onlineProduct: (id: number) => request(`/car-model-products/${id}/online`, {
    method: 'PUT',
  }),
  
  // 下架车型商品
  offlineProduct: (id: number) => request(`/car-model-products/${id}/offline`, {
    method: 'PUT',
  }),
}

// 模板管理API
export const templateAPI = {
  // 增值服务模板相关接口
  getValueAddedServiceTemplateList: (params?: any) => {
    // 过滤掉值为undefined的参数
    const filteredParams: Record<string, any> = {};
    if (params) {
      Object.keys(params).forEach(key => {
        if (params[key] !== undefined) {
          filteredParams[key] = params[key];
        }
      });
    }
    
    const queryString = Object.keys(filteredParams).length > 0 ? new URLSearchParams(filteredParams).toString() : '';
    return request(`/value-added-service-templates${queryString ? '?' + queryString : ''}`);
  },
  
  createValueAddedServiceTemplate: (data: any) => request('/value-added-service-templates', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  
  updateValueAddedServiceTemplate: (id: number, data: any) => request(`/value-added-service-templates/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  }),
  
  deleteValueAddedServiceTemplate: (id: number) => request(`/value-added-service-templates/${id}`, {
    method: 'DELETE',
  }),
  
  // 取消规则模板相关接口
  getCancellationRuleTemplateList: (params?: any) => {
    // 过滤掉值为undefined的参数
    const filteredParams: Record<string, any> = {};
    if (params) {
      Object.keys(params).forEach(key => {
        if (params[key] !== undefined) {
          filteredParams[key] = params[key];
        }
      });
    }
    
    const queryString = Object.keys(filteredParams).length > 0 ? new URLSearchParams(filteredParams).toString() : '';
    return request(`/cancellation-rule-templates${queryString ? '?' + queryString : ''}`);
  },
  
  createCancellationRuleTemplate: (data: any) => request('/cancellation-rule-templates', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  
  updateCancellationRuleTemplate: (id: number, data: any) => request(`/cancellation-rule-templates/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  }),
  
  deleteCancellationRuleTemplate: (id: number) => request(`/cancellation-rule-templates/${id}`, {
    method: 'DELETE',
  }),
  
  // 服务政策模板相关接口
  getServicePolicyTemplateList: (params?: any) => {
    // 过滤掉值为undefined的参数
    const filteredParams: Record<string, any> = {};
    if (params) {
      Object.keys(params).forEach(key => {
        if (params[key] !== undefined) {
          filteredParams[key] = params[key];
        }
      });
    }
    
    const queryString = Object.keys(filteredParams).length > 0 ? new URLSearchParams(filteredParams).toString() : '';
    return request(`/service-policy-templates${queryString ? '?' + queryString : ''}`);
  },
  
  createServicePolicyTemplate: (data: any) => request('/service-policy-templates', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  
  updateServicePolicyTemplate: (id: number, data: any) => request(`/service-policy-templates/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  }),
  
  deleteServicePolicyTemplate: (id: number) => request(`/service-policy-templates/${id}`, {
    method: 'DELETE',
  }),
}

// 特殊定价API
export const pricingAPI = {
  // 创建特殊定价
  createPricing: (data: any) => request('/special-pricings', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  
  // 更新特殊定价
  updatePricing: (id: number, data: any) => request(`/special-pricings/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  }),
  
  // 删除特殊定价
  deletePricing: (id: number) => request(`/special-pricings/${id}`, {
    method: 'DELETE',
  }),
  
  // 获取特殊定价详情
  getPricing: (id: number) => request(`/special-pricings/${id}`),
  
  // 分页查询特殊定价
  getPricingList: (params?: any) => {
    // 过滤掉值为undefined的参数
    const filteredParams: Record<string, any> = {};
    if (params) {
      Object.keys(params).forEach(key => {
        if (params[key] !== undefined) {
          filteredParams[key] = params[key];
        }
      });
    }
    
    const queryString = Object.keys(filteredParams).length > 0 ? new URLSearchParams(filteredParams).toString() : '';
    return request(`/special-pricings${queryString ? '?' + queryString : ''}`);
  },
  
  // 获取商品的所有特殊定价
  getPricingsByProduct: (productId: number) => request(`/special-pricings/product/${productId}`),
  
  // 获取商品特殊定价日历视图
  getPricingsByProductForCalendar: (productId: number) => request(`/special-pricings/product/${productId}/calendar`),
}

// 商品车辆关联API
export const vehicleRelationAPI = {
  // 获取商品关联的车辆
  getRelatedVehicles: (productId: number) => request(`/product-vehicle-relations/product/${productId}`),
  
  // 关联车辆到商品
  relateVehicles: (data: { productId: number; vehicleIds: number[] }) => request('/product-vehicle-relations/batch', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  
  // 取消车辆与商品的关联
  unrelateVehicles: (data: { productId: number; vehicleIds: number[] }) => request('/product-vehicle-relations/batch', {
    method: 'DELETE',
    body: JSON.stringify(data),
  }),
  
  // 获取门店下指定车型的可关联车辆
  getAvailableVehiclesForProduct: (productId: number) => request(`/product-vehicle-relations/product/${productId}/vehicles`),
}

// 员工管理API
export const employeeAPI = {
  // 创建员工
  createEmployee: (data: any) => request('/employees', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  
  // 更新员工
  updateEmployee: (data: any) => request('/employees', {
    method: 'PUT',
    body: JSON.stringify(data),
  }),
  
  // 删除员工
  deleteEmployee: (id: number) => request(`/employees/${id}`, {
    method: 'DELETE',
  }),
  
  // 获取员工详情
  getEmployee: (id: number) => request(`/employees/${id}`),
  
  // 分页查询员工列表
  getEmployees: (params?: { current?: number; size?: number; employeeName?: string; phone?: string; status?: number }) => {
    // 过滤掉值为undefined的参数
    const filteredParams: Record<string, any> = {};
    if (params) {
      Object.keys(params).forEach(key => {
        if (params[key as keyof typeof params] !== undefined) {
          filteredParams[key] = params[key as keyof typeof params];
        }
      });
    }
    
    const queryString = Object.keys(filteredParams).length > 0 ? new URLSearchParams(filteredParams).toString() : '';
    return request(`/employees${queryString ? '?' + queryString : ''}`);
  },
  
  // 重置员工密码
  resetPassword: (id: number) => request(`/employees/${id}/reset-password`, {
    method: 'PUT',
  }),
}

// AI服务API
export const aiAPI = {
  // 发送聊天消息
  chat: (data: { messages: Array<{ role: string; content: string }>; tenant_id: number }) => 
    aiRequest('/chat', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
}
// API服务基础配置
const API_BASE_URL = '/api'

// 通用请求函数
export const request = async (url: string, options: RequestInit = {}) => {
  const token = localStorage.getItem('token')
  
  const config: RequestInit = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
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

// 运营认证API
export const operationAuthAPI = {
  // 用户登录
  login: (credentials: { username: string; password: string }) => 
    request('/operation/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
    }),
}

// 商户审核API
export const merchantAuditAPI = {
  // 获取待审核商户列表
  getPendingMerchants: async (params?: any) => {
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
    return request(`/operation/merchants/pending${queryString ? '?' + queryString : ''}`);
  },
  
  // 获取所有商户列表
  getAllMerchants: async (params?: any) => {
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
    return request(`/operation/merchants${queryString ? '?' + queryString : ''}`);
  },
  
  // 审核商户申请
  auditMerchant: async (id: string, data: { status: string; reason?: string }) => {
    // 将参数作为查询参数发送而不是请求体
    const params = new URLSearchParams();
    params.append('status', data.status);
    if (data.reason) {
      params.append('reason', data.reason);
    }
    return request(`/operation/merchants/${id}/audit?${params.toString()}`, {
      method: 'PUT'
    });
  },
  
  // 获取商户申请详情
  getMerchantDetail: async (id: string) => {
    return request(`/operation/merchants/${id}`);
  }
};

// 车辆审核API
export const vehicleAuditAPI = {
  // 获取待审核车辆列表
  getPendingVehicles: async (params?: any) => {
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
    return request(`/operation/vehicles/pending${queryString ? '?' + queryString : ''}`);
  },
  
  // 获取所有车辆列表
  getAllVehicles: async (params?: any) => {
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
    return request(`/operation/vehicles${queryString ? '?' + queryString : ''}`);
  },
  
  // 审核车辆
  auditVehicle: async (id: string, data: { status: string; reason?: string }) => {
    // 将参数作为查询参数发送而不是请求体
    const params = new URLSearchParams();
    params.append('status', data.status);
    if (data.reason) {
      params.append('reason', data.reason);
    }
    return request(`/operation/vehicles/${id}/audit?${params.toString()}`, {
      method: 'PUT'
    });
  },
  
  // 获取车辆详情
  getVehicleDetail: async (id: string) => {
    return request(`/operation/vehicles/${id}`);
  }
};

// 门店审核API
export const storeAuditAPI = {
  // 获取待审核门店列表
  getPendingStores: async (params?: any) => {
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
    return request(`/operation/stores/pending${queryString ? '?' + queryString : ''}`);
  },
  
  // 获取所有门店列表
  getAllStores: async (params?: any) => {
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
    return request(`/operation/stores${queryString ? '?' + queryString : ''}`);
  },
  
  // 审核门店
  auditStore: async (id: string, data: { status: string; reason?: string }) => {
    // 将参数作为查询参数发送而不是请求体
    const params = new URLSearchParams();
    params.append('status', data.status);
    if (data.reason) {
      params.append('reason', data.reason);
    }
    return request(`/operation/stores/${id}/audit?${params.toString()}`, {
      method: 'PUT'
    });
  },
  
  // 获取门店详情
  getStoreDetail: async (id: string) => {
    return request(`/operation/stores/${id}`);
  }
};

// 运营数据Dashboard API
export const operationDashboardAPI = {
  // 获取运营概览数据
  getOverviewStats: async () => {
    return request('/operation/dashboard/overview');
  },
  
  // 获取审核统计数据
  getAuditStats: async () => {
    return request('/operation/dashboard/audit-stats');
  },
  
  // 获取业务趋势数据
  getTrendData: async (type: string, period: string) => {
    return request(`/operation/dashboard/trends?type=${type}&period=${period}`);
  }
};

// 车型管理API
export const carModelAPI = {
  // 创建车型
  createCarModel: async (data: any) => {
    return request('/operation/car-models', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  },
  
  // 更新车型
  updateCarModel: async (id: number, data: any) => {
    return request(`/operation/car-models/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  },
  
  // 删除车型
  deleteCarModel: async (id: number) => {
    return request(`/operation/car-models/${id}`, {
      method: 'DELETE',
    });
  },
  
  // 获取车型详情
  getCarModel: async (id: number) => {
    return request(`/operation/car-models/${id}`);
  },
  
  // 分页查询车型列表
  getCarModelList: async (params?: any) => {
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
    return request(`/operation/car-models${queryString ? '?' + queryString : ''}`);
  },
  
  // 查询全部车型
  getAllCarModels: async () => {
    return request('/operation/car-models/all');
  },
  
  // 按品牌查询车型
  getCarModelsByBrand: async (brand: string) => {
    return request(`/operation/car-models/brand/${brand}`);
  }
};

// 用户管理API
export const userManagementAPI = {
  // 分页获取用户列表
  getUserList: async (params?: any) => {
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
    return request(`/operation/users${queryString ? '?' + queryString : ''}`);
  },
  
  // 获取用户详情
  getUserDetail: async (id: number) => {
    return request(`/operation/users/${id}`);
  },
  
  // 更新用户状态
  updateUserStatus: async (id: number, status: number) => {
    const params = new URLSearchParams();
    params.append('status', status.toString());
    return request(`/operation/users/${id}/status?${params.toString()}`, {
      method: 'PUT'
    });
  }
};
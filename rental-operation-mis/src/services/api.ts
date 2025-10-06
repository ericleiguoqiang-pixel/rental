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
    const response = await apiClient.get<ApiResponse<PageResponse<any>>>(`/operation/merchants/pending${queryString ? '?' + queryString : ''}`);
    return response;
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
    const response = await apiClient.get<ApiResponse<PageResponse<any>>>(`/operation/merchants${queryString ? '?' + queryString : ''}`);
    return response;
  },
  
  // 审核商户申请
  auditMerchant: async (id: string, data: { status: string; reason?: string }) => {
    // 将参数作为查询参数发送而不是请求体
    const params = new URLSearchParams();
    params.append('status', data.status);
    if (data.reason) {
      params.append('reason', data.reason);
    }
    const response = await apiClient.put<ApiResponse<any>>(`/operation/merchants/${id}/audit?${params.toString()}`);
    return response;
  },
  
  // 获取商户申请详情
  getMerchantDetail: async (id: string) => {
    const response = await apiClient.get<ApiResponse<any>>(`/operation/merchants/${id}`);
    return response;
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
    const response = await apiClient.get<ApiResponse<PageResponse<any>>>(`/operation/vehicles/pending${queryString ? '?' + queryString : ''}`);
    return response;
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
    const response = await apiClient.get<ApiResponse<PageResponse<any>>>(`/operation/vehicles${queryString ? '?' + queryString : ''}`);
    return response;
  },
  
  // 审核车辆
  auditVehicle: async (id: string, data: { status: string; reason?: string }) => {
    // 将参数作为查询参数发送而不是请求体
    const params = new URLSearchParams();
    params.append('status', data.status);
    if (data.reason) {
      params.append('reason', data.reason);
    }
    const response = await apiClient.put<ApiResponse<any>>(`/operation/vehicles/${id}/audit?${params.toString()}`);
    return response;
  },
  
  // 获取车辆详情
  getVehicleDetail: async (id: string) => {
    const response = await apiClient.get<ApiResponse<any>>(`/operation/vehicles/${id}`);
    return response;
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
    const response = await apiClient.get<ApiResponse<PageResponse<any>>>(`/operation/stores/pending${queryString ? '?' + queryString : ''}`);
    return response;
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
    const response = await apiClient.get<ApiResponse<PageResponse<any>>>(`/operation/stores${queryString ? '?' + queryString : ''}`);
    return response;
  },
  
  // 审核门店
  auditStore: async (id: string, data: { status: string; reason?: string }) => {
    // 将参数作为查询参数发送而不是请求体
    const params = new URLSearchParams();
    params.append('status', data.status);
    if (data.reason) {
      params.append('reason', data.reason);
    }
    const response = await apiClient.put<ApiResponse<any>>(`/operation/stores/${id}/audit?${params.toString()}`);
    return response;
  },
  
  // 获取门店详情
  getStoreDetail: async (id: string) => {
    const response = await apiClient.get<ApiResponse<any>>(`/operation/stores/${id}`);
    return response;
  }
};

// 运营数据Dashboard API
export const operationDashboardAPI = {
  // 获取运营概览数据
  getOverviewStats: async () => {
    const response = await apiClient.get<ApiResponse<any>>('/operation/dashboard/overview');
    return response;
  },
  
  // 获取审核统计数据
  getAuditStats: async () => {
    const response = await apiClient.get<ApiResponse<any>>('/operation/dashboard/audit-stats');
    return response;
  },
  
  // 获取业务趋势数据
  getTrendData: async (type: string, period: string) => {
    const response = await apiClient.get<ApiResponse<any>>(`/operation/dashboard/trends?type=${type}&period=${period}`);
    return response;
  }
};

// 车型管理API
export const carModelAPI = {
  // 创建车型
  createCarModel: async (data: any) => {
    const response = await apiClient.post<ApiResponse<number>>('/operation/car-models', data);
    return response;
  },
  
  // 更新车型
  updateCarModel: async (id: number, data: any) => {
    const response = await apiClient.put<ApiResponse<void>>(`/operation/car-models/${id}`, data);
    return response;
  },
  
  // 删除车型
  deleteCarModel: async (id: number) => {
    const response = await apiClient.delete<ApiResponse<void>>(`/operation/car-models/${id}`);
    return response;
  },
  
  // 获取车型详情
  getCarModel: async (id: number) => {
    const response = await apiClient.get<ApiResponse<any>>(`/operation/car-models/${id}`);
    return response;
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
    const response = await apiClient.get<ApiResponse<PageResponse<any>>>(`/operation/car-models${queryString ? '?' + queryString : ''}`);
    return response;
  },
  
  // 查询全部车型
  getAllCarModels: async () => {
    const response = await apiClient.get<ApiResponse<any[]>>('/operation/car-models/all');
    return response;
  },
  
  // 按品牌查询车型
  getCarModelsByBrand: async (brand: string) => {
    const response = await apiClient.get<ApiResponse<any[]>>(`/operation/car-models/brand/${brand}`);
    return response;
  }
};
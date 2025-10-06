import request from './request';

// 创建订单请求参数接口
export interface CreateOrderRequest {
  quoteId: string;
  driverName: string;
  driverIdCard: string;
  driverPhone: string;
  startTime: string;
  endTime: string;
  orderLocation: string;
  selectedVasIds: number[];
  totalAmount: number;
  damageDeposit: number;
  violationDeposit: number;
  userId?: number; // 添加用户ID字段
}

// 订单响应接口
export interface OrderResponse {
  id: number;
  orderNo: string;
  orderStatus: number;
  createTime: string;
  driverName: string;
  startTime: string;
  endTime: string;
  productId: number;
  totalAmount: number;
  damageDeposit: number;
  violationDeposit: number;
  isDepositPaid: boolean;
  userId?: number; // 添加用户ID字段
}

// 订单列表响应接口
export interface OrderListResponse {
  records: OrderResponse[];
  total: number;
  current: number;
  size: number;
}

// 创建订单
export const createOrder = async (params: CreateOrderRequest) => {
  try {
    const response = await request.post('/user-orders', params);
    console.log('createOrder response:', response.data);
    return response.data;
  } catch (error) {
    console.error('Create order error:', error);
    throw error;
  }
};

// 获取订单详情
export const getOrderDetail = async (orderId: number) => {
  try {
    const response = await request.get(`/user-orders/${orderId}`);
    console.log('getOrderDetail response:', response.data);
    return response.data;
  } catch (error) {
    console.error('Get order detail error:', error);
    throw error;
  }
};

// 获取用户订单列表
export const getUserOrders = async (params?: { 
  current?: number; 
  size?: number; 
  orderNo?: string; 
  status?: number 
}) => {
  try {
    // 获取用户ID
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userId = user?.id;
    
    if (!userId) {
      throw new Error('用户未登录或用户信息不存在');
    }
    
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
    // 修复API路径，使用正确的网关路由
    const response = await request.get(`/user-orders/user/${userId}${queryString ? '?' + queryString : ''}`);
    console.log('getUserOrders response:', response.data);
    return response.data;
  } catch (error) {
    console.error('Get user orders error:', error);
    throw error;
  }
};

// 取消订单
export const cancelOrder = async (orderId: number) => {
  try {
    const response = await request.put(`/user-orders/${orderId}/cancel`);
    console.log('cancelOrder response:', response.data);
    return response.data;
  } catch (error) {
    console.error('Cancel order error:', error);
    throw error;
  }
};
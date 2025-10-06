import request from './request';

// 支付请求参数接口
export interface PaymentRequest {
  orderId: number;
  orderNo: string;
  paymentType: number; // 1-租车费, 2-押金
  amount: number;
  paymentMethod: number; // 1-微信, 2-支付宝
}

// 支付响应结果接口
export interface PaymentResponse {
  id: number;
  paymentNo: string;
  orderId: number;
  orderNo: string;
  paymentType: number;
  amount: number;
  paymentMethod: number;
  paymentStatus: number;
  paymentTime: string;
}

// 发起支付
export const initiatePayment = async (params: PaymentRequest) => {
  try {
    const response = await request.post('/payments', params);
    console.log('initiatePayment response:', response.data);
    return response.data;
  } catch (error) {
    console.error('Initiate payment error:', error);
    throw error;
  }
};

// 支付回调
export const paymentCallback = async (paymentNo: string) => {
  try {
    const response = await request.post('/payments/callback', null, {
      params: { paymentNo }
    });
    console.log('paymentCallback response:', response.data);
    return response.data;
  } catch (error) {
    console.error('Payment callback error:', error);
    throw error;
  }
};
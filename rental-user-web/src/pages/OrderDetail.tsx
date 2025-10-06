import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Card, Typography, Button, message, Descriptions, Divider, Modal } from 'antd';
import { getOrderDetail, cancelOrder } from '../services/orderService';
import { initiatePayment } from '../services/paymentService';
import { getOrderStatusDescription, canCancelOrder } from '../enums/orderStatus';
import dayjs from 'dayjs';

const { Title, Text } = Typography;

interface OrderDetail {
  id: number;
  orderNo: string;
  orderStatus: number;
  createTime: string;
  cancelTime?: string;
  driverName: string;
  driverIdCard: string;
  driverPhone: string;
  startTime: string;
  endTime: string;
  actualPickupTime?: string;
  actualReturnTime?: string;
  orderLocation?: string;
  carModelId: number;
  productId: number;
  licensePlate?: string;
  pickupType: number;
  returnType: number;
  pickupStoreId?: number;
  returnStoreId?: number;
  pickupDriver?: string;
  returnDriver?: string;
  basicRentalFee: number;
  serviceFee: number;
  insuranceFee: number;
  damageDeposit: number;
  violationDeposit: number;
  actualDeposit: number;
  isDepositPaid: boolean;
  vasSnapshot?: string;
  cancellationRuleSnapshot?: string;
  servicePolicySnapshot?: string;
}

const OrderDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [orderDetail, setOrderDetail] = useState<OrderDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [isPaymentModalVisible, setIsPaymentModalVisible] = useState(false);
  const [paymentType, setPaymentType] = useState<number>(2); // 2-押金

  useEffect(() => {
    if (id) {
      fetchOrderDetail(parseInt(id));
    }
  }, [id]);

  const fetchOrderDetail = async (orderId: number) => {
    try {
      const result = await getOrderDetail(orderId);
      setOrderDetail(result?.data || null);
    } catch (error) {
      message.error('获取订单详情失败');
    } finally {
      setLoading(false);
    }
  };

  // 取消订单
  const handleCancelOrder = () => {
    if (!orderDetail) return;
    
    Modal.confirm({
      title: '确认取消订单',
      content: '确定要取消这个订单吗？',
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        try {
          const result = await cancelOrder(orderDetail.id);
          if (result?.data) {
            message.success('订单取消成功');
            // 重新获取订单详情
            fetchOrderDetail(orderDetail.id);
          } else {
            message.error('订单取消失败');
          }
        } catch (error) {
          console.error('取消订单失败:', error);
          message.error('取消订单失败: ' + (error as Error).message);
        }
      }
    });
  };

  // 处理支付
  const handlePayment = async (method: number) => {
    if (!orderDetail) {
      message.error('订单信息不完整');
      return;
    }

    try {
      // 构造支付请求参数
      const paymentRequest = {
        orderId: orderDetail.id,
        orderNo: orderDetail.orderNo,
        paymentType: paymentType,
        amount: orderDetail.actualDeposit / 100, // 转换为元
        paymentMethod: method // 1-微信, 2-支付宝
      };

      // 调用支付接口
      const paymentResponse = await initiatePayment(paymentRequest);
      if (paymentResponse?.data) {
        message.success('押金支付成功！');
        setIsPaymentModalVisible(false);
        // 重新获取订单详情
        fetchOrderDetail(orderDetail.id);
      } else {
        message.error('支付失败');
      }
    } catch (error) {
      console.error('支付失败:', error);
      message.error('支付失败: ' + (error as Error).message);
    }
  };

  if (loading) {
    return <div>加载中...</div>;
  }

  if (!orderDetail) {
    return <div>未找到订单详情</div>;
  }

  const { 
    orderNo, orderStatus, createTime, driverName, driverIdCard, driverPhone,
    startTime, endTime, actualPickupTime, actualReturnTime, orderLocation,
    basicRentalFee, serviceFee, insuranceFee, damageDeposit, violationDeposit, 
    actualDeposit, isDepositPaid
  } = orderDetail;

  // 计算总金额（元）
  const totalAmount = (basicRentalFee + serviceFee + insuranceFee) / 100;

  return (
    <div style={{ padding: 20, height: '100%', overflowY: 'auto' }}>
      <Card>
        <Title level={3} style={{ textAlign: 'center' }}>订单详情</Title>
        
        <Descriptions title="订单信息" column={1} bordered>
          <Descriptions.Item label="订单号">{orderNo}</Descriptions.Item>
          <Descriptions.Item label="订单状态">
            <Text strong style={{ color: orderStatus === 1 ? '#ff4d4f' : '#52c41a' }}>
              {getOrderStatusDescription(orderStatus)}
            </Text>
          </Descriptions.Item>
          <Descriptions.Item label="下单时间">
            {dayjs(createTime).format('YYYY-MM-DD HH:mm:ss')}
          </Descriptions.Item>
          {orderStatus === 5 && orderDetail.cancelTime && (
            <Descriptions.Item label="取消时间">
              {dayjs(orderDetail.cancelTime).format('YYYY-MM-DD HH:mm:ss')}
            </Descriptions.Item>
          )}
        </Descriptions>
        
        <Divider />
        
        <Descriptions title="驾驶员信息" column={1} bordered>
          <Descriptions.Item label="驾驶员姓名">{driverName}</Descriptions.Item>
          <Descriptions.Item label="身份证号">{driverIdCard}</Descriptions.Item>
          <Descriptions.Item label="手机号">{driverPhone}</Descriptions.Item>
        </Descriptions>
        
        <Divider />
        
        <Descriptions title="用车信息" column={1} bordered>
          <Descriptions.Item label="取车时间">
            {dayjs(startTime).format('YYYY-MM-DD HH:mm')}
          </Descriptions.Item>
          <Descriptions.Item label="还车时间">
            {dayjs(endTime).format('YYYY-MM-DD HH:mm')}
          </Descriptions.Item>
          {actualPickupTime && (
            <Descriptions.Item label="实际取车时间">
              {dayjs(actualPickupTime).format('YYYY-MM-DD HH:mm:ss')}
            </Descriptions.Item>
          )}
          {actualReturnTime && (
            <Descriptions.Item label="实际还车时间">
              {dayjs(actualReturnTime).format('YYYY-MM-DD HH:mm:ss')}
            </Descriptions.Item>
          )}
          {orderLocation && (
            <Descriptions.Item label="下单位置">{orderLocation}</Descriptions.Item>
          )}
        </Descriptions>
        
        <Divider />
        
        <Descriptions title="费用信息" column={1} bordered>
          <Descriptions.Item label="基础租车费">¥{(basicRentalFee / 100).toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="服务费">¥{(serviceFee / 100).toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="保障费">¥{(insuranceFee / 100).toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="总金额">
            <Text strong style={{ fontSize: 18, color: '#ff4d4f' }}>
              ¥{totalAmount.toFixed(2)}
            </Text>
          </Descriptions.Item>
        </Descriptions>
        
        <Divider />
        
        <Descriptions title="押金信息" column={1} bordered>
          <Descriptions.Item label="车损押金">¥{(damageDeposit / 100).toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="违章押金">¥{(violationDeposit / 100).toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="实际押金">
            <Text strong style={{ fontSize: 18, color: '#ff4d4f' }}>
              ¥{(actualDeposit / 100).toFixed(2)}
            </Text>
          </Descriptions.Item>
          <Descriptions.Item label="押金状态">
            {isDepositPaid ? (
              <Text type="success">已支付</Text>
            ) : (
              <Text type="danger">未支付</Text>
            )}
          </Descriptions.Item>
        </Descriptions>
        
        <Divider />
        
        <div style={{ textAlign: 'center' }}>
          {canCancelOrder(orderStatus) && (
            <Button 
              type="primary" 
              danger 
              onClick={handleCancelOrder}
              style={{ marginRight: 20 }}
            >
              取消订单
            </Button>
          )}
          
          {!isDepositPaid && orderStatus >= 2 && (
            <Button 
              type="primary" 
              onClick={() => {
                setPaymentType(2); // 支付押金
                setIsPaymentModalVisible(true);
              }}
            >
              支付押金
            </Button>
          )}
        </div>
      </Card>

      {/* 支付弹窗 */}
      <Modal
        title="押金支付"
        visible={isPaymentModalVisible}
        onCancel={() => setIsPaymentModalVisible(false)}
        footer={null}
      >
        <div style={{ textAlign: 'center', padding: 20 }}>
          <p>订单号: {orderNo}</p>
          <p style={{ fontSize: 24, fontWeight: 'bold', color: '#ff4d4f' }}>
            支付金额: ¥{(actualDeposit / 100).toFixed(2)}
          </p>
          <div style={{ marginTop: 30 }}>
            <Button 
              type="primary" 
              style={{ marginRight: 20 }} 
              onClick={() => handlePayment(1)} // 微信支付
            >
              微信支付
            </Button>
            <Button 
              type="primary" 
              onClick={() => handlePayment(2)} // 支付宝支付
            >
              支付宝支付
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default OrderDetail;
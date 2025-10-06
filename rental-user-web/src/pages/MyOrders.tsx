import React, { useState, useEffect } from 'react';
import { Card, Typography, Tag, Button, message, Spin, Space, Input, List, Divider } from 'antd';
import { useNavigate } from 'react-router-dom';
import { getUserOrders } from '../services/orderService';
import { getOrderStatusDescription } from '../enums/orderStatus';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { Search } = Input;

// 定义订单项接口，确保字段与后端返回一致
interface OrderItem {
  id: number;
  orderNo: string;
  orderStatus: number;
  createTime: string;
  startTime: string;
  endTime: string;
  orderAmount: number; // 后端实际返回的字段名
  isDepositPaid: boolean;
}

const MyOrders: React.FC = () => {
  const [orders, setOrders] = useState<OrderItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchParams, setSearchParams] = useState({
    current: 1,
    size: 10,
    orderNo: '',
    status: undefined as number | undefined
  });
  const [total, setTotal] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    fetchOrders();
  }, [searchParams]);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const response = await getUserOrders(searchParams);
      // 确保正确解析分页数据
      const records = response?.data?.records || [];
      // 转换字段名以匹配前端接口定义
      const convertedOrders = records.map((order: any) => ({
        ...order,
        totalAmount: order.orderAmount // 将orderAmount映射为totalAmount
      }));
      setOrders(convertedOrders);
      setTotal(response?.data?.total || 0);
    } catch (error) {
      console.error('获取订单列表失败:', error);
      message.error('获取订单列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (value: string) => {
    setSearchParams(prev => ({
      ...prev,
      orderNo: value,
      current: 1
    }));
  };

  const handleTableChange = (pagination: any) => {
    setSearchParams(prev => ({
      ...prev,
      current: pagination.current,
      size: pagination.pageSize
    }));
  };

  const getStatusColor = (status: number) => {
    switch (status) {
      case 1: return 'orange'; // 待支付
      case 2: return 'blue';   // 待取车
      case 3: return 'green';  // 已取车
      case 4: return 'gray';   // 已完成
      case 5: return 'red';    // 已取消
      default: return 'default';
    }
  };

  // 移动端友好的订单项渲染
  const renderOrderItem = (order: OrderItem) => (
    <Card 
      key={order.id} 
      style={{ marginBottom: 16 }}
      onClick={() => navigate(`/order/${order.id}`)}
    >
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
        <Text strong>订单号: {order.orderNo}</Text>
        <Tag color={getStatusColor(order.orderStatus)}>
          {getOrderStatusDescription(order.orderStatus)}
        </Tag>
      </div>
      
      <div style={{ marginBottom: 8 }}>
        <Text type="secondary">下单时间: {dayjs(order.createTime).format('YYYY-MM-DD HH:mm:ss')}</Text>
      </div>
      
      <div style={{ marginBottom: 8 }}>
        <Text>用车时间:</Text>
        <div>{dayjs(order.startTime).format('YYYY-MM-DD HH:mm')}</div>
        <div>{dayjs(order.endTime).format('YYYY-MM-DD HH:mm')}</div>
      </div>
      
      <div style={{ marginBottom: 8 }}>
        <Text>总金额: <Text strong style={{ color: '#ff4d4f' }}>¥{(order.orderAmount / 100).toFixed(2)}</Text></Text>
      </div>
      
      <div style={{ textAlign: 'right' }}>
        <Button type="link" onClick={(e) => {
          e.stopPropagation();
          navigate(`/order/${order.id}`);
        }}>
          查看详情
        </Button>
      </div>
    </Card>
  );

  return (
    <div style={{ padding: 16 }}>
      <Card>
        <Title level={3} style={{ textAlign: 'center', marginBottom: 24 }}>我的订单</Title>
        
        <div style={{ marginBottom: 20 }}>
          <Space direction="vertical" style={{ width: '100%' }}>
            <Search 
              placeholder="搜索订单号" 
              onSearch={handleSearch}
              enterButton
              allowClear
            />
          </Space>
        </div>
        
        <Spin spinning={loading}>
          {orders.length > 0 ? (
            <>
              <List
                dataSource={orders}
                renderItem={renderOrderItem}
              />
              {/* 简单的分页信息显示 */}
              <div style={{ textAlign: 'center', marginTop: 16 }}>
                <Text type="secondary">
                  共 {total} 条记录，当前显示 {(searchParams.current - 1) * searchParams.size + 1} - {Math.min(searchParams.current * searchParams.size, total)} 条
                </Text>
              </div>
            </>
          ) : (
            <div style={{ textAlign: 'center', padding: 40 }}>
              <Text type="secondary">暂无订单记录</Text>
            </div>
          )}
        </Spin>
      </Card>
    </div>
  );
};

export default MyOrders;
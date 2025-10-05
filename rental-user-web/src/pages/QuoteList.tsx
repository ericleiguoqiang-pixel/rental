import React, { useEffect, useState } from 'react';
import { List, Card, Typography, Button, message } from 'antd';
import { useLocation, useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import { searchQuotes } from '../services/quoteService';

const { Title } = Typography;

interface Quote {
  id: string;
  productName: string;
  modelName: string;
  storeName: string;
  price: number;
  deliveryType: string; // 上门取送车 或 用户到店自取
  dailyRate: number;
  pickupFee: number;
  returnFee: number;
  storeFee: number;
  baseProtectionPrice: number;
  totalPrice: number;
}

const QuoteList: React.FC = () => {
  const [quotes, setQuotes] = useState<Quote[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchParams, setSearchParams] = useState<{
    pickupDateTime?: string;
    dropoffDateTime?: string;
    location?: { lng: number; lat: number };
  }>({});
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    // 获取从Home页面传递的参数
    if (location.state) {
      setSearchParams(location.state as any);
    }
  }, [location.state]);

  useEffect(() => {
    // 当搜索参数变化时，获取报价数据
    if (searchParams.pickupDateTime && searchParams.dropoffDateTime && searchParams.location) {
      fetchQuotes();
    }
  }, [searchParams]);

  const fetchQuotes = async () => {
    setLoading(true);
    try {
      // 调用后端API获取报价数据
      const params = {
        date: dayjs(searchParams.pickupDateTime).format('YYYY-MM-DD'),
        time: dayjs(searchParams.pickupDateTime).format('HH:mm:ss'),
        longitude: searchParams.location?.lng,
        latitude: searchParams.location?.lat
      };
      
      // 确保longitude和latitude不为undefined
      if (params.longitude !== undefined && params.latitude !== undefined) {
        const response: any = await searchQuotes({
          date: params.date,
          time: params.time,
          location: {
            lng: params.longitude,
            lat: params.latitude
          }
        });
        console.log('API response:', response);
        
        if (response && response.quotes) {
          // 转换数据格式以匹配前端接口
          const formattedQuotes = response.quotes.map((quote: any) => ({
            id: quote.id,
            productName: quote.productName,
            modelName: quote.modelName || '未知车型',
            storeName: quote.storeName,
            price: quote.totalPrice,
            deliveryType: quote.deliveryType,
            dailyRate: quote.dailyRate,
            pickupFee: quote.pickupFee,
            returnFee: quote.returnFee,
            storeFee: quote.storeFee,
            baseProtectionPrice: quote.baseProtectionPrice,
            totalPrice: quote.totalPrice
          }));
          setQuotes(formattedQuotes);
        } else {
          setQuotes([]);
        }
      }
    } catch (error) {
      console.error('获取报价失败:', error);
      message.error('获取报价失败');
      setQuotes([]);
    } finally {
      setLoading(false);
    }
  };

  const handleQuoteSelect = (quoteId: string) => {
    navigate(`/quote/${quoteId}`, { state: searchParams });
  };

  return (
    <div style={{ padding: 20 }}>
      <Title level={3} style={{ textAlign: 'center' }}>报价列表</Title>
      
      {/* 显示搜索条件 */}
      {searchParams.pickupDateTime && searchParams.dropoffDateTime && (
        <Card style={{ marginBottom: 20 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <div>
              <div><strong>取车时间：</strong>{dayjs(searchParams.pickupDateTime).format('YYYY-MM-DD HH:mm')}</div>
              <div><strong>还车时间：</strong>{dayjs(searchParams.dropoffDateTime).format('YYYY-MM-DD HH:mm')}</div>
            </div>
            {searchParams.location && (
              <div>
                <div><strong>用车地点：</strong></div>
                <div>经度: {searchParams.location.lng.toFixed(6)}</div>
                <div>纬度: {searchParams.location.lat.toFixed(6)}</div>
              </div>
            )}
          </div>
        </Card>
      )}
      
      <List
        loading={loading}
        dataSource={quotes}
        renderItem={item => (
          <List.Item>
            <Card style={{ width: '100%' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <h3>{item.productName}</h3>
                  <p>车型：{item.modelName}</p>
                  <p>门店：{item.storeName}</p>
                  <p>取还方式：{item.deliveryType}</p>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: 20, fontWeight: 'bold', color: '#ff4d4f' }}>
                    ¥{item.totalPrice}
                  </div>
                  <Button 
                    type="primary" 
                    onClick={() => handleQuoteSelect(item.id)}
                    style={{ marginTop: 10 }}
                  >
                    查看详情
                  </Button>
                </div>
              </div>
            </Card>
          </List.Item>
        )}
      />
    </div>
  );
};

export default QuoteList;
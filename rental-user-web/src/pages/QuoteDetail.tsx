import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Card, Typography, List, Button, message } from 'antd';
import { getQuoteDetail } from '../services/quoteService';

const { Title, Text } = Typography;

interface QuoteDetail {
  id: string;
  productName: string;
  modelName: string;
  storeName: string;
  price: number;
  deliveryType: string;
  vasTemplates: any[]; // 增值服务模板
  cancellationPolicy: any; // 取消规则
  servicePolicy: any; // 服务政策
}

const QuoteDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [quoteDetail, setQuoteDetail] = useState<QuoteDetail | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id) {
      fetchQuoteDetail(id);
    }
  }, [id]);

  const fetchQuoteDetail = async (quoteId: string) => {
    try {
      const result = await getQuoteDetail(quoteId);
      setQuoteDetail(result);
    } catch (error) {
      message.error('获取报价详情失败');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div>加载中...</div>;
  }

  if (!quoteDetail) {
    return <div>未找到报价详情</div>;
  }

  return (
    <div style={{ padding: 20 }}>
      <Card>
        <Title level={3} style={{ textAlign: 'center' }}>报价详情</Title>
        
        <div style={{ marginBottom: 20 }}>
          <Text strong>商品名称：</Text>
          <Text>{quoteDetail.productName}</Text>
        </div>
        
        <div style={{ marginBottom: 20 }}>
          <Text strong>车型：</Text>
          <Text>{quoteDetail.modelName}</Text>
        </div>
        
        <div style={{ marginBottom: 20 }}>
          <Text strong>门店：</Text>
          <Text>{quoteDetail.storeName}</Text>
        </div>
        
        <div style={{ marginBottom: 20 }}>
          <Text strong>取还方式：</Text>
          <Text>{quoteDetail.deliveryType}</Text>
        </div>
        
        <div style={{ marginBottom: 20 }}>
          <Text strong>价格：</Text>
          <Text style={{ fontSize: 20, fontWeight: 'bold', color: '#ff4d4f' }}>
            ¥{quoteDetail.price}
          </Text>
        </div>
        
        <div style={{ marginBottom: 20 }}>
          <Title level={4}>增值服务</Title>
          <List
            dataSource={quoteDetail.vasTemplates}
            renderItem={item => (
              <List.Item>
                <div>
                  <Text strong>{item.name}</Text>
                  <Text> - ¥{item.price}</Text>
                </div>
              </List.Item>
            )}
          />
        </div>
        
        <div style={{ marginBottom: 20 }}>
          <Title level={4}>取消规则</Title>
          <Text>{quoteDetail.cancellationPolicy?.description || '无特殊取消规则'}</Text>
        </div>
        
        <div style={{ marginBottom: 20 }}>
          <Title level={4}>服务政策</Title>
          <Text>{quoteDetail.servicePolicy?.description || '无特殊服务政策'}</Text>
        </div>
        
        <Button type="primary" style={{ width: '100%' }} size="large">
          立即下单
        </Button>
      </Card>
    </div>
  );
};

export default QuoteDetail;
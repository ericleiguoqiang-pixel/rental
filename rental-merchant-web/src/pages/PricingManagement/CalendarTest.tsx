import React, { useState } from 'react';
import { Button, Card, Space } from 'antd';
import SpecialPricingCalendarModal from '../../components/SpecialPricingCalendarModal';

const CalendarTest: React.FC = () => {
  const [visible, setVisible] = useState(false);
  const [productId] = useState(1); // 使用固定的商品ID进行测试

  return (
    <div style={{ padding: 24 }}>
      <Card title="特殊定价日历测试">
        <Space>
          <Button type="primary" onClick={() => setVisible(true)}>
            打开特殊定价日历
          </Button>
        </Space>
      </Card>
      
      <SpecialPricingCalendarModal
        visible={visible}
        productId={productId}
        onCancel={() => setVisible(false)}
        onOk={() => setVisible(false)}
      />
    </div>
  );
};

export default CalendarTest;
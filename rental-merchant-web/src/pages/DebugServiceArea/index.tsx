import React, { useState } from 'react';
import { Button, Card, message } from 'antd';
import ServiceAreaMapModal from '../../components/ServiceAreaMapModal';

const DebugServiceArea: React.FC = () => {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [coordinates, setCoordinates] = useState<any[]>([]);

  const handleFinish = (coords: any[]) => {
    console.log('调试页面接收到坐标:', coords);
    setCoordinates(coords);
    setIsModalVisible(false);
    message.success(`成功接收到 ${coords.length} 个坐标点`);
  };

  return (
    <div style={{ padding: 24 }}>
      <Card title="电子围栏调试页面">
        <p>此页面用于调试电子围栏绘制功能，排除白屏问题。</p>
        <Button 
          type="primary" 
          onClick={() => setIsModalVisible(true)}
          style={{ marginBottom: 16 }}
        >
          打开电子围栏绘制
        </Button>
        
        {coordinates.length > 0 && (
          <Card title="绘制结果" size="small">
            <p>坐标点数量: {coordinates.length}</p>
            <div style={{ maxHeight: 200, overflowY: 'auto' }}>
              {coordinates.map((coord, index) => (
                <div key={index}>
                  点 {index + 1}: ({coord[0].toFixed(6)}, {coord[1].toFixed(6)})
                </div>
              ))}
            </div>
          </Card>
        )}
      </Card>

      <ServiceAreaMapModal
        visible={isModalVisible}
        onCancel={() => setIsModalVisible(false)}
        onFinish={handleFinish}
        initialCoordinates={[]}
      />
    </div>
  );
};

export default DebugServiceArea;
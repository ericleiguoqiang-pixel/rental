import React, { useState } from 'react';
import { Button, Card, message, Alert } from 'antd';
import ServiceAreaMapModal from '../../components/ServiceAreaMapModal/ServiceAreaMapModal';

const DebugServiceAreaV2: React.FC = () => {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [coordinates, setCoordinates] = useState<any[]>([]);
  const [logs, setLogs] = useState<string[]>([]);

  const addLog = (msg: string) => {
    const timestamp = new Date().toLocaleTimeString();
    setLogs(prev => [...prev, `[${timestamp}] ${msg}`]);
    console.log(`[DebugServiceAreaV2] ${msg}`);
  };

  const handleFinish = (coords: any[]) => {
    addLog(`接收到 ${coords.length} 个坐标点`);
    setCoordinates(coords);
    setIsModalVisible(false);
    message.success(`成功接收到 ${coords.length} 个坐标点`);
  };

  const handleCancel = () => {
    addLog('关闭地图模态框');
    setIsModalVisible(false);
  };

  return (
    <div style={{ padding: 24 }}>
      <Card title="电子围栏调试页面 V2">
        <p>此页面用于详细调试电子围栏绘制功能，帮助排查双击后的问题。</p>
        <Button 
          type="primary" 
          onClick={() => {
            addLog('打开电子围栏绘制');
            setIsModalVisible(true);
          }}
          style={{ marginBottom: 16 }}
        >
          打开电子围栏绘制
        </Button>
        
        <Button 
          onClick={() => setLogs([])}
          style={{ marginBottom: 16, marginLeft: 16 }}
        >
          清空日志
        </Button>
        
        {coordinates.length > 0 && (
          <Card title="绘制结果" size="small" style={{ marginBottom: 16 }}>
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
        
        <Card title="调试日志" size="small">
          <div style={{ maxHeight: 300, overflowY: 'auto' }}>
            {logs.length === 0 ? (
              <p>暂无日志</p>
            ) : (
              logs.map((log, index) => (
                <div key={index} style={{ 
                  padding: '4px 8px', 
                  borderBottom: '1px solid #f0f0f0',
                  fontFamily: 'monospace',
                  fontSize: '12px'
                }}>
                  {log}
                </div>
              ))
            )}
          </div>
        </Card>
      </Card>

      <ServiceAreaMapModal
        visible={isModalVisible}
        onCancel={handleCancel}
        onFinish={handleFinish}
        initialCoordinates={[]}
      />
    </div>
  );
};

export default DebugServiceAreaV2;
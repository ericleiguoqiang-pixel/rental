import React, { useEffect, useRef, useState } from 'react';
import { Modal, Button, message } from 'antd';

// 声明AMap全局变量
declare global {
  interface Window {
    AMap: any;
  }
}

interface StoreLocationPickerProps {
  visible: boolean;
  onCancel: () => void;
  onConfirm: (location: { longitude: number; latitude: number }) => void;
  initialLocation?: { longitude: number; latitude: number };
}

const StoreLocationPicker: React.FC<StoreLocationPickerProps> = ({
  visible,
  onCancel,
  onConfirm,
  initialLocation
}) => {
  const mapContainerRef = useRef<HTMLDivElement>(null);
  const mapInstanceRef = useRef<any>(null);
  const markerRef = useRef<any>(null);
  const [selectedLocation, setSelectedLocation] = useState<{ longitude: number; latitude: number } | null>(initialLocation || null);

  useEffect(() => {
    if (visible && mapContainerRef.current && !mapInstanceRef.current) {
      initMap();
    }
    
    if (visible && mapInstanceRef.current && initialLocation) {
      setSelectedLocation(initialLocation);
      updateMarker(initialLocation);
    }
    
    return () => {
      if (mapInstanceRef.current) {
        mapInstanceRef.current.destroy();
        mapInstanceRef.current = null;
      }
    };
  }, [visible, initialLocation]);

  const initMap = () => {
    if (!mapContainerRef.current) return;
    
    // 检查是否已加载高德地图API
    if (typeof window.AMap === 'undefined') {
      message.error('地图加载失败，请检查网络连接');
      return;
    }

    try {
      // 创建地图实例
      const map = new window.AMap.Map(mapContainerRef.current, {
        zoom: 11,
        center: initialLocation ? [initialLocation.longitude, initialLocation.latitude] : [116.397428, 39.90923], // 默认北京中心
      });
      
      mapInstanceRef.current = map;

      // 添加点击事件监听器
      map.on('click', (e: any) => {
        const location = {
          longitude: e.lnglat.getLng(),
          latitude: e.lnglat.getLat()
        };
        setSelectedLocation(location);
        updateMarker(location);
      });

      // 如果有初始位置，添加标记
      if (initialLocation) {
        updateMarker(initialLocation);
      }
    } catch (error) {
      console.error('初始化地图错误:', error);
      message.error('地图初始化失败');
    }
  };

  const updateMarker = (location: { longitude: number; latitude: number }) => {
    if (!mapInstanceRef.current) return;

    // 清除之前的标记
    if (markerRef.current) {
      mapInstanceRef.current.remove(markerRef.current);
    }

    try {
      // 创建新的标记
      const marker = new window.AMap.Marker({
        position: [location.longitude, location.latitude],
        draggable: true,
        cursor: 'move'
      });

      // 添加拖拽事件
      marker.on('dragend', (e: any) => {
        const position = e.lnglat;
        const newLocation = {
          longitude: position.getLng(),
          latitude: position.getLat()
        };
        setSelectedLocation(newLocation);
      });

      mapInstanceRef.current.add(marker);
      markerRef.current = marker;

      // 调整地图视野以适应标记
      mapInstanceRef.current.setFitView([marker]);
    } catch (error) {
      console.error('创建标记错误:', error);
      message.error('设置位置时出现错误');
    }
  };

  const handleConfirm = () => {
    if (!selectedLocation) {
      message.warning('请先在地图上选择位置');
      return;
    }
    onConfirm(selectedLocation);
  };

  return (
    <Modal
      title="选择门店位置"
      open={visible}
      onCancel={onCancel}
      onOk={handleConfirm}
      width={800}
    >
      <div style={{ marginBottom: 16 }}>
        <p>点击地图选择门店位置，可拖拽标记调整位置</p>
      </div>
      <div 
        ref={mapContainerRef} 
        style={{ width: '100%', height: '400px' }}
      />
      {selectedLocation && (
        <div style={{ marginTop: 16 }}>
          <p>当前选择位置：</p>
          <p>经度: {selectedLocation.longitude.toFixed(6)}</p>
          <p>纬度: {selectedLocation.latitude.toFixed(6)}</p>
        </div>
      )}
    </Modal>
  );
};

export default StoreLocationPicker;
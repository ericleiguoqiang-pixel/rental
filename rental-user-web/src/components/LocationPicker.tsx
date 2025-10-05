import React, { useEffect, useRef, useState } from 'react';
import { Card, message } from 'antd';

// 声明AMap全局变量
declare global {
  interface Window {
    AMap: any;
  }
}

interface LocationPickerProps {
  onLocationSelect: (location: { lng: number; lat: number } | null) => void;
}

const LocationPicker: React.FC<LocationPickerProps> = ({ onLocationSelect }) => {
  const mapContainerRef = useRef<HTMLDivElement>(null);
  const mapInstanceRef = useRef<any>(null);
  const markerRef = useRef<any>(null);
  const [selectedLocation, setSelectedLocation] = useState<{ lng: number; lat: number } | null>(null);

  useEffect(() => {
    // 初始化地图
    initMap();
    
    // 组件卸载时清理地图资源
    return () => {
      if (mapInstanceRef.current) {
        mapInstanceRef.current.destroy();
        mapInstanceRef.current = null;
      }
    };
  }, []);

  const initMap = () => {
    // 等待高德地图API加载完成
    const checkAMap = setInterval(() => {
      if (typeof window.AMap !== 'undefined') {
        clearInterval(checkAMap);
        createMap();
      }
    }, 100);

    // 设置超时时间
    setTimeout(() => {
      clearInterval(checkAMap);
      if (typeof window.AMap === 'undefined') {
        message.error('地图加载超时，请检查网络连接');
      }
    }, 10000);
  };

  const createMap = () => {
    if (!mapContainerRef.current) return;

    try {
      // 创建地图实例
      const map = new window.AMap.Map(mapContainerRef.current, {
        zoom: 11,
        center: [116.397428, 39.90923], // 默认北京中心
      });
      
      mapInstanceRef.current = map;

      // 添加点击事件监听器
      map.on('click', (e: any) => {
        const location = {
          lng: e.lnglat.getLng(),
          lat: e.lnglat.getLat()
        };
        setSelectedLocation(location);
        updateMarker(location);
        onLocationSelect(location);
      });

    } catch (error) {
      console.error('初始化地图错误:', error);
      message.error('地图初始化失败');
    }
  };

  const updateMarker = (location: { lng: number; lat: number }) => {
    if (!mapInstanceRef.current) return;

    // 清除之前的标记
    if (markerRef.current) {
      mapInstanceRef.current.remove(markerRef.current);
    }

    try {
      // 创建新的标记
      const marker = new window.AMap.Marker({
        position: [location.lng, location.lat],
        draggable: true,
        cursor: 'move'
      });

      // 添加拖拽事件
      marker.on('dragend', (e: any) => {
        const position = e.lnglat;
        const newLocation = {
          lng: position.getLng(),
          lat: position.getLat()
        };
        setSelectedLocation(newLocation);
        onLocationSelect(newLocation);
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

  return (
    <Card>
      <div 
        ref={mapContainerRef} 
        style={{ width: '100%', height: '300px' }}
      />
      <div style={{ marginTop: 10, textAlign: 'center', color: '#888' }}>
        点击地图选择位置，可拖拽标记调整位置
      </div>
      
      {selectedLocation && (
        <div style={{ marginTop: 10, textAlign: 'center', color: '#52c41a' }}>
          已选择位置: 经度 {selectedLocation.lng.toFixed(6)}, 纬度 {selectedLocation.lat.toFixed(6)}
        </div>
      )}
    </Card>
  );
};

export default LocationPicker;
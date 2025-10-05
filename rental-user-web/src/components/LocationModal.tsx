import React, { useEffect, useRef, useState } from 'react';
import { Modal, message, Input, Button } from 'antd';

// 声明AMap全局变量
declare global {
  interface Window {
    AMap: any;
  }
}

interface LocationModalProps {
  visible: boolean;
  onCancel: () => void;
  onConfirm: (location: { 
    lng: number; 
    lat: number; 
    address?: string;
  }) => void;
}

const LocationModal: React.FC<LocationModalProps> = ({
  visible,
  onCancel,
  onConfirm
}) => {
  const mapContainerRef = useRef<HTMLDivElement>(null);
  const mapInstanceRef = useRef<any>(null);
  const markerRef = useRef<any>(null);
  const geocoderRef = useRef<any>(null);
  const [selectedLocation, setSelectedLocation] = useState<{
    lng: number;
    lat: number;
    address?: string;
  } | null>(null);
  const [address, setAddress] = useState<string>('');

  useEffect(() => {
    if (visible) {
      // 延迟初始化地图，确保DOM已渲染
      const timer = setTimeout(() => {
        initMap();
      }, 100);
      
      return () => {
        clearTimeout(timer);
      };
    }
    
    // 组件卸载时清理地图资源
    return () => {
      cleanupMap();
    };
  }, [visible]);

  const initMap = () => {
    if (!visible || !mapContainerRef.current) return;

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
      if (typeof window.AMap === 'undefined' && visible) {
        message.error('地图加载超时，请检查网络连接');
      }
    }, 10000);
  };

  const createMap = () => {
    if (!mapContainerRef.current) return;

    try {
      // 销毁之前可能存在的地图实例
      if (mapInstanceRef.current) {
        mapInstanceRef.current.destroy();
      }

      // 创建地图实例
      const map = new window.AMap.Map(mapContainerRef.current, {
        zoom: 11,
        center: [116.397428, 39.90923], // 默认北京中心
      });
      
      mapInstanceRef.current = map;

      // 异步加载地理编码插件
      window.AMap.plugin('AMap.Geocoder', () => {
        // 创建地理编码服务实例
        geocoderRef.current = new window.AMap.Geocoder({
          city: '全国'
        });
      });

      // 添加点击事件监听器
      map.on('click', (e: any) => {
        const location = {
          lng: e.lnglat.getLng(),
          lat: e.lnglat.getLat()
        };
        setSelectedLocation(location);
        updateMarker(location);
        reverseGeocode(location.lng, location.lat);
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
        reverseGeocode(newLocation.lng, newLocation.lat);
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

  // 逆地理编码获取地址信息
  const reverseGeocode = (lng: number, lat: number) => {
    // 等待地理编码插件加载完成
    const checkGeocoder = setInterval(() => {
      if (geocoderRef.current) {
        clearInterval(checkGeocoder);
        doReverseGeocode(lng, lat);
      }
    }, 100);

    // 设置超时时间
    setTimeout(() => {
      clearInterval(checkGeocoder);
      if (!geocoderRef.current) {
        console.error('地理编码插件加载超时');
      }
    }, 5000);
  };

  const doReverseGeocode = (lng: number, lat: number) => {
    if (!geocoderRef.current) return;

    geocoderRef.current.getAddress([lng, lat], (status: string, result: any) => {
      if (status === 'complete' && result.regeocode) {
        const address = result.regeocode.formattedAddress;
        setAddress(address);
        setSelectedLocation(prev => prev ? { ...prev, address } : null);
      } else {
        console.error('逆地理编码失败');
        setAddress('未知位置');
      }
    });
  };

  // 地图放大功能
  const handleZoomIn = () => {
    if (mapInstanceRef.current) {
      mapInstanceRef.current.zoomIn();
    }
  };

  // 地图缩小功能
  const handleZoomOut = () => {
    if (mapInstanceRef.current) {
      mapInstanceRef.current.zoomOut();
    }
  };

  const handleConfirm = () => {
    if (!selectedLocation) {
      message.warning('请先在地图上选择位置');
      return;
    }
    
    onConfirm({
      ...selectedLocation,
      address: address || undefined
    });
  };

  const cleanupMap = () => {
    // 清理标记
    if (markerRef.current) {
      // 从地图上移除标记
      if (mapInstanceRef.current) {
        mapInstanceRef.current.remove(markerRef.current);
      }
      markerRef.current = null;
    }
    
    // 清理地图实例
    if (mapInstanceRef.current) {
      mapInstanceRef.current.destroy();
      mapInstanceRef.current = null;
    }
    
    // 清理地理编码器
    if (geocoderRef.current) {
      geocoderRef.current = null;
    }
  };

  return (
    <Modal
      title="选择用车地点"
      open={visible}
      onCancel={onCancel}
      onOk={handleConfirm}
      width={800}
      destroyOnClose={true}
    >
      <div style={{ marginBottom: 16 }}>
        <p>点击地图选择用车地点，可拖拽标记调整位置</p>
      </div>
      <div style={{ position: 'relative' }}>
        <div 
          ref={mapContainerRef} 
          style={{ width: '100%', height: '400px' }}
        />
        {/* 地图缩放控件 */}
        <div style={{ 
          position: 'absolute', 
          top: 10, 
          right: 10, 
          zIndex: 100,
          backgroundColor: 'white',
          borderRadius: 4,
          boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
        }}>
          <Button 
            type="text" 
            onClick={handleZoomIn}
            style={{ 
              width: 30, 
              height: 30, 
              display: 'block',
              border: 'none',
              borderBottom: '1px solid #eee'
            }}
          >
            +
          </Button>
          <Button 
            type="text" 
            onClick={handleZoomOut}
            style={{ 
              width: 30, 
              height: 30, 
              display: 'block',
              border: 'none'
            }}
          >
            -
          </Button>
        </div>
      </div>
      
      {selectedLocation && (
        <div style={{ marginTop: 16 }}>
          <div style={{ marginBottom: 10 }}>
            <strong>当前位置信息：</strong>
          </div>
          <div style={{ marginBottom: 10 }}>
            <Input 
              value={address} 
              readOnly
              placeholder="地址信息获取中..."
            />
          </div>
          <div>
            <p>经度: {selectedLocation.lng.toFixed(6)}</p>
            <p>纬度: {selectedLocation.lat.toFixed(6)}</p>
          </div>
        </div>
      )}
    </Modal>
  );
};

export default LocationModal;
import React, { useState, useEffect } from 'react';
import { Button, DatePicker, TimePicker, Card, Typography, message, Modal } from 'antd';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import LocationModal from '../components/LocationModal';

const { Title } = Typography;

const Home: React.FC = () => {
  const [pickupDate, setPickupDate] = useState<dayjs.Dayjs | null>(null);
  const [pickupTime, setPickupTime] = useState<dayjs.Dayjs | null>(null);
  const [dropoffDate, setDropoffDate] = useState<dayjs.Dayjs | null>(null);
  const [dropoffTime, setDropoffTime] = useState<dayjs.Dayjs | null>(null);
  const [selectedLocation, setSelectedLocation] = useState<{ 
    lng: number; 
    lat: number; 
    address?: string;
  } | null>(null);
  const [isLocationModalVisible, setIsLocationModalVisible] = useState(false);
  const navigate = useNavigate();

  // 设置默认时间
  useEffect(() => {
    const tomorrow = dayjs().add(1, 'day');
    const dayAfterTomorrow = dayjs().add(2, 'day');
    const defaultTime = dayjs().hour(10).minute(0).second(0);
    
    setPickupDate(tomorrow);
    setPickupTime(defaultTime);
    setDropoffDate(dayAfterTomorrow);
    setDropoffTime(defaultTime);
  }, []);

  const showLocationModal = () => {
    setIsLocationModalVisible(true);
  };

  const handleLocationModalCancel = () => {
    setIsLocationModalVisible(false);
  };

  const handleLocationSelect = (location: { 
    lng: number; 
    lat: number; 
    address?: string;
  }) => {
    setSelectedLocation(location);
    setIsLocationModalVisible(false);
  };

  const handleSearch = () => {
    if (!pickupDate || !pickupTime || !dropoffDate || !dropoffTime) {
      message.warning('请选择完整的取还车时间');
      return;
    }

    if (!selectedLocation) {
      message.warning('请选择用车地点');
      return;
    }

    // 验证取车时间不能晚于还车时间
    const pickupDateTime = dayjs(`${pickupDate.format('YYYY-MM-DD')} ${pickupTime.format('HH:mm')}`);
    const dropoffDateTime = dayjs(`${dropoffDate.format('YYYY-MM-DD')} ${dropoffTime.format('HH:mm')}`);
    
    if (pickupDateTime.isAfter(dropoffDateTime)) {
      message.warning('取车时间不能晚于还车时间');
      return;
    }

    // 跳转到报价列表页，传递参数
    navigate('/quotes', {
      state: {
        pickupDateTime: pickupDateTime.toISOString(),
        dropoffDateTime: dropoffDateTime.toISOString(),
        location: selectedLocation
      }
    });
  };

  return (
    <div style={{ padding: 20 }}>
      <Card>
        <Title level={3} style={{ textAlign: 'center' }}>选择用车时间和地点</Title>
        
        <div style={{ marginBottom: 20 }}>
          <div style={{ marginBottom: 10 }}>
            <strong>取车日期：</strong>
          </div>
          <DatePicker 
            value={pickupDate} 
            onChange={setPickupDate} 
            style={{ width: '100%' }} 
          />
        </div>

        <div style={{ marginBottom: 20 }}>
          <div style={{ marginBottom: 10 }}>
            <strong>取车时间：</strong>
          </div>
          <TimePicker 
            value={pickupTime} 
            onChange={setPickupTime} 
            format="HH:mm"
            style={{ width: '100%' }} 
          />
        </div>

        <div style={{ marginBottom: 20 }}>
          <div style={{ marginBottom: 10 }}>
            <strong>还车日期：</strong>
          </div>
          <DatePicker 
            value={dropoffDate} 
            onChange={setDropoffDate} 
            style={{ width: '100%' }} 
          />
        </div>

        <div style={{ marginBottom: 20 }}>
          <div style={{ marginBottom: 10 }}>
            <strong>还车时间：</strong>
          </div>
          <TimePicker 
            value={dropoffTime} 
            onChange={setDropoffTime} 
            format="HH:mm"
            style={{ width: '100%' }} 
          />
        </div>

        <div style={{ marginBottom: 20 }}>
          <div style={{ marginBottom: 10 }}>
            <strong>用车地点：</strong>
          </div>
          <Button 
            type={selectedLocation ? "default" : "dashed"} 
            onClick={showLocationModal}
            style={{ width: '100%', height: '40px' }}
          >
            {selectedLocation ? (
              <div>
                <div>{selectedLocation.address || '已选择位置'}</div>
                <div style={{ fontSize: '12px', color: '#888' }}>
                  经度: {selectedLocation.lng.toFixed(6)}, 纬度: {selectedLocation.lat.toFixed(6)}
                </div>
              </div>
            ) : (
              "点击选择用车地点"
            )}
          </Button>
        </div>

        <Button 
          type="primary" 
          onClick={handleSearch} 
          style={{ width: '100%' }}
          size="large"
        >
          搜索报价
        </Button>
      </Card>

      <LocationModal
        visible={isLocationModalVisible}
        onCancel={handleLocationModalCancel}
        onConfirm={handleLocationSelect}
      />
    </div>
  );
};

export default Home;
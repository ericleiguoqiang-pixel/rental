import { useState, useEffect } from 'react';
import { message } from 'antd';
import { productAPI, vehicleRelationAPI } from '../services/api';

interface VehicleInfo {
  id: number;
  licensePlate: string;
  vehicleStatus: number;
  related: boolean;
}

export const useVehicleRelations = (productId: number | null) => {
  const [vehicles, setVehicles] = useState<VehicleInfo[]>([]);
  const [loading, setLoading] = useState(false);
  const [relatedVehicles, setRelatedVehicles] = useState<number[]>([]);

  // 获取门店下指定车型的可关联车辆
  const fetchAvailableVehicles = async () => {
    if (!productId) return;
    
    setLoading(true);
    try {
      // 调用后端接口获取车辆信息
      const response: any = await vehicleRelationAPI.getAvailableVehiclesForProduct(productId);
      if (response.code === 200) {
        const vehicleData: VehicleInfo[] = response.data.map((item: any) => ({
          id: item.id,
          licensePlate: item.licensePlate,
          vehicleStatus: item.vehicleStatus,
          related: item.related || false
        }));
        
        setVehicles(vehicleData);
        
        // 更新已关联车辆列表
        const relatedIds = vehicleData.filter(v => v.related).map(v => v.id);
        setRelatedVehicles(relatedIds);
      } else {
        message.error(response.message || '获取车辆信息失败');
        setVehicles([]);
      }
    } catch (error) {
      message.error('获取车辆信息失败');
      setVehicles([]);
    } finally {
      setLoading(false);
    }
  };

  // 关联车辆
  const relateVehicles = async (vehicleIds: number[]) => {
    if (!productId) return false;
    
    try {
      const response: any = await vehicleRelationAPI.relateVehicles({
        productId,
        vehicleIds
      });
      
      if (response.code === 200) {
        message.success('关联成功');
        setRelatedVehicles(vehicleIds);
        // 更新车辆列表中的关联状态
        setVehicles(prev => prev.map(vehicle => ({
          ...vehicle,
          related: vehicleIds.includes(vehicle.id)
        })));
        return true;
      } else {
        message.error(response.message || '关联失败');
        return false;
      }
    } catch (error) {
      message.error('关联失败');
      return false;
    }
  };

  // 取消关联车辆
  const unrelateVehicles = async (vehicleIds: number[]) => {
    if (!productId) return false;
    
    try {
      const response: any = await vehicleRelationAPI.unrelateVehicles({
        productId,
        vehicleIds
      });
      
      if (response.code === 200) {
        message.success('取消关联成功');
        const newRelatedIds = relatedVehicles.filter(id => !vehicleIds.includes(id));
        setRelatedVehicles(newRelatedIds);
        // 更新车辆列表中的关联状态
        setVehicles(prev => prev.map(vehicle => ({
          ...vehicle,
          related: newRelatedIds.includes(vehicle.id)
        })));
        return true;
      } else {
        message.error(response.message || '取消关联失败');
        return false;
      }
    } catch (error) {
      message.error('取消关联失败');
      return false;
    }
  };

  useEffect(() => {
    if (productId) {
      fetchAvailableVehicles();
    }
  }, [productId]);

  return {
    vehicles,
    loading,
    relatedVehicles,
    fetchAvailableVehicles,
    relateVehicles,
    unrelateVehicles
  };
};
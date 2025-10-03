import { useState, useEffect } from 'react';
import { message } from 'antd';
import { pricingAPI } from '../services/api';

export interface SpecialPricingInfo {
  id: number;
  productId: number;
  priceDate: string;
  price: number;
  createdTime: string;
  updatedTime: string;
}

export const useSpecialPricingCalendar = (productId: number | null) => {
  const [pricings, setPricings] = useState<Record<string, SpecialPricingInfo>>({});
  const [loading, setLoading] = useState(false);

  // 获取商品特殊定价日历数据
  const fetchPricingsForCalendar = async () => {
    if (!productId) return;
    
    setLoading(true);
    try {
      const response: any = await pricingAPI.getPricingsByProductForCalendar(productId);
      if (response.code === 200) {
        // 将返回的数据转换为以日期为键的对象
        const pricingMap: Record<string, SpecialPricingInfo> = {};
        Object.keys(response.data).forEach(date => {
          pricingMap[date] = response.data[date];
        });
        setPricings(pricingMap);
      } else {
        message.error(response.message || '获取特殊定价失败');
      }
    } catch (error) {
      message.error('获取特殊定价失败');
    } finally {
      setLoading(false);
    }
  };

  // 更新特殊定价
  const updatePricing = async (date: string, price: number) => {
    if (!productId) return false;
    
    try {
      // 如果价格为0或空，删除定价
      if (!price || price <= 0) {
        // 查找该日期是否已有定价
        const existingPricing = Object.values(pricings).find(p => p.priceDate === date);
        if (existingPricing) {
          // 删除现有定价
          const response: any = await pricingAPI.deletePricing(existingPricing.id);
          if (response.code === 200) {
            // 从本地状态中移除
            const newPricings = { ...pricings };
            delete newPricings[date];
            setPricings(newPricings);
            return true;
          } else {
            message.error(response.message || '删除定价失败');
            return false;
          }
        }
        return true;
      }
      
      // 查找该日期是否已有定价
      const existingPricing = Object.values(pricings).find(p => p.priceDate === date);
      
      let result = false;
      if (existingPricing) {
        // 更新现有定价
        const response: any = await pricingAPI.updatePricing(existingPricing.id, {
          productId,
          priceDate: date,
          price: Math.round(price * 100) // 转换为分
        });
        result = response.code === 200;
      } else {
        // 创建新定价
        const response: any = await pricingAPI.createPricing({
          productId,
          priceDate: date,
          price: Math.round(price * 100) // 转换为分
        });
        result = response.code === 200;
      }
      
      if (result) {
        // 更新本地状态
        await fetchPricingsForCalendar();
        return true;
      } else {
        message.error('保存定价失败');
        return false;
      }
    } catch (error) {
      message.error('保存定价失败');
      return false;
    }
  };

  useEffect(() => {
    if (productId) {
      fetchPricingsForCalendar();
    }
  }, [productId]);

  return {
    pricings,
    loading,
    fetchPricingsForCalendar,
    updatePricing
  };
};
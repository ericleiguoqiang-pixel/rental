import { useState, useEffect } from 'react';
import { message } from 'antd';
import { storeAPI, carModelAPI, templateAPI } from '../services/api';

interface Store {
  id: number;
  storeName: string;
  // 其他门店字段...
}

interface CarModel {
  id: number;
  brand: string;
  series: string;
  model: string;
  // 其他车型字段...
}

interface ValueAddedServiceTemplate {
  id: number;
  templateName: string;
  serviceType: number;
  price: number;
  deductible: number;
  includeTireDamage: number;
  includeGlassDamage: number;
  thirdPartyCoverage: number;
  chargeDepreciation: number;
  depreciationDeductible: number;
  depreciationRate: number;
  createdTime: string;
  updatedTime: string;
}

interface CancellationRuleTemplate {
  id: number;
  templateName: string;
  weekdayRule: string;
  holidayRule: string;
  createdTime: string;
  updatedTime: string;
}

interface ServicePolicyTemplate {
  id: number;
  templateName: string;
  mileageLimit: string;
  earlyPickup: string;
  latePickup: string;
  earlyReturn: string;
  renewal: string;
  forcedRenewal: string;
  pickupMaterials: string;
  cityRestriction: string;
  usageAreaLimit: string;
  fuelFee: string;
  personalBelongingsLoss: string;
  violationHandling: string;
  roadsideAssistance: string;
  forcedRecovery: string;
  etcFee: string;
  cleaningFee: string;
  invoiceInfo: string;
  createdTime: string;
  updatedTime: string;
}

export const useProductData = () => {
  const [stores, setStores] = useState<Store[]>([]);
  const [carModels, setCarModels] = useState<CarModel[]>([]);
  const [vasTemplates, setVasTemplates] = useState<ValueAddedServiceTemplate[]>([]);
  const [cancellationTemplates, setCancellationTemplates] = useState<CancellationRuleTemplate[]>([]);
  const [policyTemplates, setPolicyTemplates] = useState<ServicePolicyTemplate[]>([]);
  const [loading, setLoading] = useState(false);

  // 获取门店列表
  const fetchStores = async () => {
    try {
      const response: any = await storeAPI.getStores();
      if (response.code === 200) {
        setStores(response.data.records || response.data);
      } else {
        message.error(response.message || '获取门店列表失败');
      }
    } catch (error) {
      message.error('获取门店列表失败');
    }
  };

  // 获取车型列表
  const fetchCarModels = async () => {
    try {
      const response: any = await carModelAPI.getAllCarModels();
      if (response.code === 200) {
        setCarModels(response.data.records || response.data);
      } else {
        message.error(response.message || '获取车型列表失败');
      }
    } catch (error) {
      message.error('获取车型列表失败');
    }
  };

  // 获取增值服务模板列表
  const fetchVasTemplates = async () => {
    try {
      const response: any = await templateAPI.getValueAddedServiceTemplateList({ current: 1, size: 100 });
      if (response.code === 200) {
        setVasTemplates(response.data.records || []);
      } else {
        message.error(response.message || '获取增值服务模板列表失败');
      }
    } catch (error) {
      message.error('获取增值服务模板列表失败');
    }
  };

  // 获取取消规则模板列表
  const fetchCancellationTemplates = async () => {
    try {
      const response: any = await templateAPI.getCancellationRuleTemplateList({ current: 1, size: 100 });
      if (response.code === 200) {
        setCancellationTemplates(response.data.records || []);
      } else {
        message.error(response.message || '获取取消规则模板列表失败');
      }
    } catch (error) {
      message.error('获取取消规则模板列表失败');
    }
  };

  // 获取服务政策模板列表
  const fetchPolicyTemplates = async () => {
    try {
      const response: any = await templateAPI.getServicePolicyTemplateList({ current: 1, size: 100 });
      if (response.code === 200) {
        setPolicyTemplates(response.data.records || []);
      } else {
        message.error(response.message || '获取服务政策模板列表失败');
      }
    } catch (error) {
      message.error('获取服务政策模板列表失败');
    }
  };

  // 初始化数据
  const fetchAllData = async () => {
    setLoading(true);
    try {
      await Promise.all([
        fetchStores(),
        fetchCarModels(),
        fetchVasTemplates(),
        fetchCancellationTemplates(),
        fetchPolicyTemplates()
      ]);
    } catch (error) {
      message.error('获取数据失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAllData();
  }, []);

  return {
    stores,
    carModels,
    vasTemplates,
    cancellationTemplates,
    policyTemplates,
    loading,
    refresh: fetchAllData
  };
};
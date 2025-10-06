import React, { useEffect, useState } from 'react';
import { useParams, useLocation } from 'react-router-dom';
import { Card, Typography, List, Button, message, Descriptions, Divider, Radio, Form, Input, Checkbox, Alert, Modal } from 'antd';
import { getQuoteDetail } from '../services/quoteService';
import { createOrder } from '../services/orderService';
import { initiatePayment } from '../services/paymentService';
import { getUserInfo } from '../services/authService'; // 导入获取用户信息的方法
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { TextArea } = Input;

interface Quote {
  id: string;
  productId: number;
  productName: string;
  modelId: number;
  modelName: string;
  storeId: number;
  storeName: string;
  dailyRate: number;
  pickupFee: number;
  returnFee: number;
  storeFee: number;
  baseProtectionPrice: number;
  totalPrice: number;
  deliveryType: string;
  damageDeposit?: number; // 车损押金(分)
  violationDeposit?: number; // 违章押金(分)
}

interface ValueAddedServiceTemplate {
  id: number;
  templateName: string;
  serviceType: number; // 1: 基础保障, 2: 优享保障, 3: 尊享保障
  price: number;
  deductible: number;
  includeTireDamage: number;
  includeGlassDamage: number;
  thirdPartyCoverage: number;
  chargeDepreciation: number;
  depreciationDeductible: number;
  depreciationRate: number;
}

interface CancellationRuleTemplate {
  id: number;
  templateName: string;
  weekdayRule: string;
  holidayRule: string;
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
}

interface QuoteDetailResponse {
  quote: Quote;
  vasTemplates: ValueAddedServiceTemplate[];
  cancellationPolicy: CancellationRuleTemplate;
  servicePolicy: ServicePolicyTemplate;
}

const QuoteDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const location = useLocation();
  const [quoteDetail, setQuoteDetail] = useState<QuoteDetailResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [selectedVas, setSelectedVas] = useState<number[]>([]); // 选中的增值服务ID
  const [form] = Form.useForm();
  const [isPaymentModalVisible, setIsPaymentModalVisible] = useState(false);
  const [orderId, setOrderId] = useState<number | null>(null);
  const [orderNo, setOrderNo] = useState<string | null>(null);
  const [paymentType, setPaymentType] = useState<number>(1); // 1-租车费, 2-押金

  // 从location.state获取搜索参数
  const searchParams = location.state as {
    pickupDateTime?: string;
    dropoffDateTime?: string;
    location?: { lng: number; lat: number; address?: string };
  } | undefined;

  useEffect(() => {
    if (id) {
      fetchQuoteDetail(id);
    }
  }, [id]);

  const fetchQuoteDetail = async (quoteId: string) => {
    try {
      const result = await getQuoteDetail(quoteId);
      setQuoteDetail(result);
      
      // 默认选中基础保障(服务类型为1)
      if (result?.vasTemplates) {
        const basicVas = result.vasTemplates.find((template: ValueAddedServiceTemplate) => template.serviceType === 1);
        if (basicVas) {
          setSelectedVas([basicVas.id]);
        }
      }
    } catch (error) {
      message.error('获取报价详情失败');
    } finally {
      setLoading(false);
    }
  };

  // 计算总价
  const calculateTotalPrice = () => {
    if (!quoteDetail?.quote) return 0;
    
    let total = quoteDetail.quote.totalPrice || 0;
    
    // 减去基础保障费（因为默认已包含在总价中）
    const basicVas = quoteDetail.vasTemplates?.find((template: ValueAddedServiceTemplate) => template.serviceType === 1);
    if (basicVas) {
      total -= basicVas.price || 0;
    }
    
    // 加上选中的增值服务费
    selectedVas.forEach(vasId => {
      const vas = quoteDetail.vasTemplates?.find((template: ValueAddedServiceTemplate) => template.id === vasId);
      if (vas) {
        total += vas.price || 0;
      }
    });
    
    return total;
  };

  // 处理增值服务选择
  const handleVasChange = (vasId: number, serviceType: number, checked: boolean) => {
    if (serviceType === 1) {
      // 基础保障不能取消选中
      return;
    }
    
    setSelectedVas(prev => {
      if (checked) {
        // 选中
        // 如果是优享保障(2)或尊享保障(3)，需要确保只能选中其中一个
        if (serviceType === 2 || serviceType === 3) {
          // 移除已选中的优享或尊享保障
          const filtered = prev.filter(id => {
            const vas = quoteDetail?.vasTemplates?.find((template: ValueAddedServiceTemplate) => template.id === id);
            return vas?.serviceType !== 2 && vas?.serviceType !== 3;
          });
          return [...filtered, vasId];
        }
        return [...prev, vasId];
      } else {
        // 取消选中
        return prev.filter(id => id !== vasId);
      }
    });
  };

  // 提交订单
  const handleSubmitOrder = () => {
    form.validateFields().then(async values => {
      try {
        if (!quoteDetail?.quote) {
          message.error('报价信息不完整');
          return;
        }

        // 获取用户ID
        const userInfo = getUserInfo();
        const userId = userInfo?.id;

        // 构造创建订单请求参数
        const orderRequest = {
          quoteId: quoteDetail.quote.id,
          driverName: values.driverName,
          driverIdCard: values.idCard,
          driverPhone: values.phone,
          startTime: searchParams?.pickupDateTime || '',
          endTime: searchParams?.dropoffDateTime || '',
          orderLocation: searchParams?.location?.address || '',
          selectedVasIds: selectedVas,
          totalAmount: calculateTotalPrice(),
          damageDeposit: quoteDetail.quote.damageDeposit || 0,
          violationDeposit: quoteDetail.quote.violationDeposit || 0,
          userId: userId // 添加用户ID
        };

        // 调用创建订单接口
        const orderResponse = await createOrder(orderRequest);
        if (orderResponse?.data) {
          setOrderId(orderResponse.data.id);
          setOrderNo(orderResponse.data.orderNo);
          message.success('订单创建成功！');
          
          // 显示支付弹窗
          setPaymentType(1); // 默认支付租车费
          setIsPaymentModalVisible(true);
        } else {
          message.error('订单创建失败');
        }
      } catch (error) {
        console.error('创建订单失败:', error);
        message.error('创建订单失败: ' + (error as Error).message);
      }
    }).catch(errorInfo => {
      console.log('验证失败:', errorInfo);
    });
  };

  // 处理支付
  const handlePayment = async (method: number) => {
    if (!orderId || !orderNo) {
      message.error('订单信息不完整');
      return;
    }

    try {
      const totalPrice = calculateTotalPrice();
      const damageDeposit = quoteDetail?.quote?.damageDeposit || 0;
      const violationDeposit = quoteDetail?.quote?.violationDeposit || 0;
      const depositAmount = Math.max(damageDeposit, violationDeposit);
      
      // 根据支付类型确定支付金额
      const amount = paymentType === 1 ? totalPrice : depositAmount;

      // 构造支付请求参数
      const paymentRequest = {
        orderId: orderId,
        orderNo: orderNo,
        paymentType: paymentType,
        amount: amount,
        paymentMethod: method // 1-微信, 2-支付宝
      };

      // 调用支付接口
      const paymentResponse = await initiatePayment(paymentRequest);
      if (paymentResponse?.data) {
        message.success(`${paymentType === 1 ? '租车费' : '押金'}支付成功！`);
        setIsPaymentModalVisible(false);
        
        // 如果是支付租车费，提示用户还可以支付押金
        if (paymentType === 1) {
          Modal.confirm({
            title: '租车费支付成功',
            content: '您还可以选择支付押金，是否现在支付押金？',
            okText: '支付押金',
            cancelText: '稍后支付',
            onOk: () => {
              setPaymentType(2); // 支付押金
              setIsPaymentModalVisible(true);
            },
            onCancel: () => {
              // 跳转到订单详情页面
              message.info('您可以稍后在订单详情页面支付押金');
            }
          });
        }
      } else {
        message.error('支付失败');
      }
    } catch (error) {
      console.error('支付失败:', error);
      message.error('支付失败: ' + (error as Error).message);
    }
  };

  if (loading) {
    return <div>加载中...</div>;
  }

  if (!quoteDetail || !quoteDetail.quote) {
    return <div>未找到报价详情</div>;
  }

  const { quote, vasTemplates, cancellationPolicy, servicePolicy } = quoteDetail;
  const totalPrice = calculateTotalPrice();

  // 计算押金信息 (后端已经转换为元，无需再次转换)
  const damageDeposit = quote.damageDeposit || 0;
  const violationDeposit = quote.violationDeposit || 0;
  const totalDeposit = Math.max(damageDeposit, violationDeposit); // 押金总额

  // 按服务类型分组增值服务
  const vasByType: Record<number, ValueAddedServiceTemplate[]> = {};
  vasTemplates?.forEach((template: ValueAddedServiceTemplate) => {
    if (!vasByType[template.serviceType]) {
      vasByType[template.serviceType] = [];
    }
    vasByType[template.serviceType].push(template);
  });

  return (
    <div style={{ padding: 20, height: '100%', overflowY: 'auto' }}>
      <Card>
        <Title level={3} style={{ textAlign: 'center' }}>报价详情</Title>
        
        {/* 取还车时间和位置信息 */}
        {searchParams && (
          <>
            <Descriptions title="用车信息" column={1} bordered>
              <Descriptions.Item label="取车时间">
                {searchParams.pickupDateTime ? dayjs(searchParams.pickupDateTime).format('YYYY-MM-DD HH:mm') : '未设置'}
              </Descriptions.Item>
              <Descriptions.Item label="还车时间">
                {searchParams.dropoffDateTime ? dayjs(searchParams.dropoffDateTime).format('YYYY-MM-DD HH:mm') : '未设置'}
              </Descriptions.Item>
              <Descriptions.Item label="用车地点">
                {searchParams.location?.address || `经度: ${searchParams.location?.lng?.toFixed(6)}, 纬度: ${searchParams.location?.lat?.toFixed(6)}`}
              </Descriptions.Item>
            </Descriptions>
            <Divider />
          </>
        )}
        
        <Descriptions title="基本信息" column={1} bordered>
          <Descriptions.Item label="商品名称">{quote.productName}</Descriptions.Item>
          <Descriptions.Item label="车型">{quote.modelName || '未知车型'}</Descriptions.Item>
          <Descriptions.Item label="门店">{quote.storeName}</Descriptions.Item>
          <Descriptions.Item label="取还方式">{quote.deliveryType}</Descriptions.Item>
        </Descriptions>
        
        <Divider />
        
        <Descriptions title="价格明细" column={1} bordered>
          <Descriptions.Item label="日租金">¥{quote.dailyRate?.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="取车费">¥{quote.pickupFee?.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="还车费">¥{quote.returnFee?.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="门店手续费">¥{quote.storeFee?.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="基础保障费">¥{quote.baseProtectionPrice?.toFixed(2)}</Descriptions.Item>
          
          {/* 展示选中的增值服务 */}
          {vasTemplates && vasTemplates
            .filter((template: ValueAddedServiceTemplate) => selectedVas.includes(template.id) && template.serviceType !== 1)
            .map((template: ValueAddedServiceTemplate) => (
              <Descriptions.Item 
                key={template.id} 
                label={`${template.templateName}`}
              >
                ¥{template.price?.toFixed(2)}
              </Descriptions.Item>
            ))
          }
          
          <Descriptions.Item label="总价格">
            <Text strong style={{ fontSize: 18, color: '#ff4d4f' }}>
              ¥{totalPrice.toFixed(2)}
            </Text>
          </Descriptions.Item>
        </Descriptions>
        
        <Divider />
        
        {/* 押金信息 */}
        <Descriptions title="押金信息" column={1} bordered>
          <Descriptions.Item label="车损押金">¥{damageDeposit.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="违章押金">¥{violationDeposit.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="押金总额">
            <Text strong style={{ fontSize: 18, color: '#ff4d4f' }}>
              ¥{totalDeposit.toFixed(2)}
            </Text>
          </Descriptions.Item>
        </Descriptions>
        
        <Alert 
          message="押金解冻说明" 
          description={
            <div>
              <div>• 还车后立即解冻车损押金</div>
              <div>• 还车后30天解冻违章押金</div>
            </div>
          } 
          type="info" 
          showIcon 
          style={{ marginTop: 16 }}
        />
        
        <Divider />
        
        <Title level={4}>增值服务</Title>
        {vasTemplates && vasTemplates.length > 0 ? (
          <div>
            {Object.entries(vasByType).map(([serviceType, templates]) => (
              <div key={serviceType} style={{ marginBottom: 20 }}>
                <Title level={5}>
                  {serviceType === '1' ? '基础保障' : serviceType === '2' ? '优享保障' : '尊享保障'}
                </Title>
                {templates.map(item => (
                  <Card 
                    size="small" 
                    title={item.templateName} 
                    style={{ width: '100%', marginBottom: 10 }}
                    extra={
                      serviceType === '1' ? (
                        <Text type="success">已选中</Text>
                      ) : (
                        <Checkbox 
                          checked={selectedVas.includes(item.id)} 
                          onChange={(e) => handleVasChange(item.id, item.serviceType, e.target.checked)}
                        />
                      )
                    }
                  >
                    <Descriptions column={1} size="small">
                      <Descriptions.Item label="价格">¥{item.price?.toFixed(2)}</Descriptions.Item>
                      <Descriptions.Item label="起赔额">{item.deductible}元</Descriptions.Item>
                      <Descriptions.Item label="包含轮胎损失">{item.includeTireDamage === 1 ? '是' : '否'}</Descriptions.Item>
                      <Descriptions.Item label="包含玻璃损失">{item.includeGlassDamage === 1 ? '是' : '否'}</Descriptions.Item>
                      <Descriptions.Item label="第三方保障">{item.thirdPartyCoverage}万元</Descriptions.Item>
                    </Descriptions>
                  </Card>
                ))}
              </div>
            ))}
          </div>
        ) : (
          <Text>暂无增值服务</Text>
        )}
        
        <Divider />
        
        <Title level={4}>取消规则</Title>
        {cancellationPolicy ? (
          <Card size="small">
            <Descriptions column={1}>
              <Descriptions.Item label="模板名称">{cancellationPolicy.templateName}</Descriptions.Item>
              <Descriptions.Item label="平日取消规则">{cancellationPolicy.weekdayRule}</Descriptions.Item>
              <Descriptions.Item label="节假日取消规则">{cancellationPolicy.holidayRule}</Descriptions.Item>
            </Descriptions>
          </Card>
        ) : (
          <Text>暂无取消规则</Text>
        )}
        
        <Divider />
        
        <Title level={4}>服务政策</Title>
        {servicePolicy ? (
          <Card size="small">
            <Descriptions column={1}>
              <Descriptions.Item label="模板名称">{servicePolicy.templateName}</Descriptions.Item>
              <Descriptions.Item label="里程限制">{servicePolicy.mileageLimit}</Descriptions.Item>
              <Descriptions.Item label="提前取车">{servicePolicy.earlyPickup}</Descriptions.Item>
              <Descriptions.Item label="延迟取车">{servicePolicy.latePickup}</Descriptions.Item>
              <Descriptions.Item label="提前还车">{servicePolicy.earlyReturn}</Descriptions.Item>
              <Descriptions.Item label="续租">{servicePolicy.renewal}</Descriptions.Item>
              <Descriptions.Item label="强行续租">{servicePolicy.forcedRenewal}</Descriptions.Item>
              <Descriptions.Item label="取车材料">{servicePolicy.pickupMaterials}</Descriptions.Item>
              <Descriptions.Item label="城市限行规则">{servicePolicy.cityRestriction}</Descriptions.Item>
              <Descriptions.Item label="用车区域限制">{servicePolicy.usageAreaLimit}</Descriptions.Item>
              <Descriptions.Item label="油费电费">{servicePolicy.fuelFee}</Descriptions.Item>
              <Descriptions.Item label="随车物品损失">{servicePolicy.personalBelongingsLoss}</Descriptions.Item>
              <Descriptions.Item label="违章处理">{servicePolicy.violationHandling}</Descriptions.Item>
              <Descriptions.Item label="道路救援">{servicePolicy.roadsideAssistance}</Descriptions.Item>
              <Descriptions.Item label="强制收车">{servicePolicy.forcedRecovery}</Descriptions.Item>
              <Descriptions.Item label="ETC费用">{servicePolicy.etcFee}</Descriptions.Item>
              <Descriptions.Item label="清洁费">{servicePolicy.cleaningFee}</Descriptions.Item>
              <Descriptions.Item label="发票说明">{servicePolicy.invoiceInfo}</Descriptions.Item>
            </Descriptions>
          </Card>
        ) : (
          <Text>暂无服务政策</Text>
        )}
        
        <Divider />
        
        {/* 驾驶员信息填写 */}
        <Title level={4}>驾驶员信息</Title>
        <Form form={form} layout="vertical">
          <Form.Item 
            name="driverName" 
            label="驾驶员姓名" 
            rules={[{ required: true, message: '请输入驾驶员姓名' }]}
          >
            <Input placeholder="请输入驾驶员姓名" />
          </Form.Item>
          
          <Form.Item 
            name="idCard" 
            label="身份证号" 
            rules={[{ required: true, message: '请输入身份证号' }]}
          >
            <Input placeholder="请输入身份证号" />
          </Form.Item>
          
          <Form.Item 
            name="phone" 
            label="手机号" 
            rules={[{ required: true, message: '请输入手机号' }]}
          >
            <Input placeholder="请输入手机号" />
          </Form.Item>
        </Form>
        
        <Divider />
        
        <Button 
          type="primary" 
          onClick={handleSubmitOrder} 
          style={{ width: '100%' }} 
          size="large"
        >
          立即下单 (¥{totalPrice.toFixed(2)})
        </Button>
      </Card>

      {/* 支付弹窗 */}
      <Modal
        title={`${paymentType === 1 ? '租车费' : '押金'}支付`}
        visible={isPaymentModalVisible}
        onCancel={() => setIsPaymentModalVisible(false)}
        footer={null}
      >
        <div style={{ textAlign: 'center', padding: 20 }}>
          <p>订单号: {orderNo}</p>
          <p style={{ fontSize: 24, fontWeight: 'bold', color: '#ff4d4f' }}>
            支付金额: ¥{paymentType === 1 ? calculateTotalPrice().toFixed(2) : totalDeposit.toFixed(2)}
          </p>
          <div style={{ marginTop: 30 }}>
            <Button 
              type="primary" 
              style={{ marginRight: 20 }} 
              onClick={() => handlePayment(1)} // 微信支付
            >
              微信支付
            </Button>
            <Button 
              type="primary" 
              onClick={() => handlePayment(2)} // 支付宝支付
            >
              支付宝支付
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default QuoteDetail;
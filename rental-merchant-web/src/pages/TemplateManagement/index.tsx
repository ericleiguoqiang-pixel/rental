import React, { useState, useEffect } from 'react';
import { 
  Table, 
  Button, 
  Modal, 
  Form, 
  Input, 
  InputNumber, 
  Select, 
  Space, 
  message, 
  Card, 
  Row, 
  Col,
  Divider,
  Tabs
} from 'antd';
import type { TableColumnsType, TabsProps } from 'antd';
import { 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined,
  SearchOutlined
} from '@ant-design/icons';
import MainLayout from '../../components/MainLayout';
import { templateAPI } from '../../services/api';

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

const TemplateManagement: React.FC = () => {
  const [activeTab, setActiveTab] = useState('vas');
  const [vasTemplates, setVasTemplates] = useState<ValueAddedServiceTemplate[]>([]);
  const [cancellationTemplates, setCancellationTemplates] = useState<CancellationRuleTemplate[]>([]);
  const [policyTemplates, setPolicyTemplates] = useState<ServicePolicyTemplate[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingTemplate, setEditingTemplate] = useState<any | null>(null);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });

  // 获取模板列表
  const fetchTemplates = async (tab: string, page = 1, pageSize = 10) => {
    setLoading(true);
    try {
      let response: any;
      switch (tab) {
        case 'vas':
          response = await templateAPI.getValueAddedServiceTemplateList({ current: page, size: pageSize });
          if (response.code === 200) {
            setVasTemplates(response.data.records);
            setPagination({
              current: response.data.pageNum,
              pageSize: response.data.pageSize,
              total: response.data.total,
            });
          }
          break;
        case 'cancellation':
          response = await templateAPI.getCancellationRuleTemplateList({ current: page, size: pageSize });
          if (response.code === 200) {
            setCancellationTemplates(response.data.records);
            setPagination({
              current: response.data.pageNum,
              pageSize: response.data.pageSize,
              total: response.data.total,
            });
          }
          break;
        case 'policy':
          response = await templateAPI.getServicePolicyTemplateList({ current: page, size: pageSize });
          if (response.code === 200) {
            setPolicyTemplates(response.data.records);
            setPagination({
              current: response.data.pageNum,
              pageSize: response.data.pageSize,
              total: response.data.total,
            });
          }
          break;
      }
      
      if (response && response.code !== 200) {
        message.error(response.message || '获取模板列表失败');
      }
    } catch (error) {
      message.error('获取模板列表失败');
    } finally {
      setLoading(false);
    }
  };

  // 处理分页变化
  const handleTableChange = (pagination: any) => {
    const { current, pageSize } = pagination;
    fetchTemplates(activeTab, current, pageSize);
  };

  // 处理标签页切换
  const handleTabChange = (key: string) => {
    setActiveTab(key);
    fetchTemplates(key, 1, pagination.pageSize);
  };

  // 打开添加/编辑模态框
  const openModal = (record?: any) => {
    if (record) {
      setEditingTemplate(record);
      form.setFieldsValue({
        ...record,
        // 金额转换为元
        price: record.price ? record.price / 100 : undefined,
        deductible: record.deductible ? record.deductible / 100 : undefined,
        depreciationDeductible: record.depreciationDeductible ? record.depreciationDeductible / 100 : undefined,
      });
    } else {
      setEditingTemplate(null);
      form.resetFields();
    }
    setModalVisible(true);
  };

  // 关闭模态框
  const closeModal = () => {
    setModalVisible(false);
    form.resetFields();
    setEditingTemplate(null);
  };

  // 提交表单
  const handleSubmit = async (values: any) => {
    try {
      // 金额转换为分
      const submitValues = {
        ...values,
        price: values.price ? Math.round(values.price * 100) : undefined,
        deductible: values.deductible ? Math.round(values.deductible * 100) : undefined,
        depreciationDeductible: values.depreciationDeductible ? Math.round(values.depreciationDeductible * 100) : undefined,
      };
      
      let response: any;
      if (editingTemplate) {
        // 编辑模板
        switch (activeTab) {
          case 'vas':
            response = await templateAPI.updateValueAddedServiceTemplate(editingTemplate.id, submitValues);
            break;
          case 'cancellation':
            response = await templateAPI.updateCancellationRuleTemplate(editingTemplate.id, submitValues);
            break;
          case 'policy':
            response = await templateAPI.updateServicePolicyTemplate(editingTemplate.id, submitValues);
            break;
        }
      } else {
        // 添加模板
        switch (activeTab) {
          case 'vas':
            response = await templateAPI.createValueAddedServiceTemplate(submitValues);
            break;
          case 'cancellation':
            response = await templateAPI.createCancellationRuleTemplate(submitValues);
            break;
          case 'policy':
            response = await templateAPI.createServicePolicyTemplate(submitValues);
            break;
        }
      }
      
      if (response && response.code === 200) {
        message.success(editingTemplate ? '更新成功' : '添加成功');
        closeModal();
        fetchTemplates(activeTab, pagination.current, pagination.pageSize);
      } else {
        message.error(response?.message || (editingTemplate ? '更新失败' : '添加失败'));
      }
    } catch (error) {
      message.error(editingTemplate ? '更新失败' : '添加失败');
    }
  };

  // 删除模板
  const handleDelete = async (id: number) => {
    try {
      let response: any;
      switch (activeTab) {
        case 'vas':
          response = await templateAPI.deleteValueAddedServiceTemplate(id);
          break;
        case 'cancellation':
          response = await templateAPI.deleteCancellationRuleTemplate(id);
          break;
        case 'policy':
          response = await templateAPI.deleteServicePolicyTemplate(id);
          break;
      }
      
      if (response && response.code === 200) {
        message.success('删除成功');
        fetchTemplates(activeTab, pagination.current, pagination.pageSize);
      } else {
        message.error(response?.message || '删除失败');
      }
    } catch (error) {
      message.error('删除失败');
    }
  };

  // 初始化数据
  useEffect(() => {
    fetchTemplates(activeTab);
  }, []);

  const vasColumns: TableColumnsType<ValueAddedServiceTemplate> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
    },
    {
      title: '模板名称',
      dataIndex: 'templateName',
    },
    {
      title: '服务类型',
      dataIndex: 'serviceType',
      render: (value) => {
        switch (value) {
          case 1: return '基础保障';
          case 2: return '优享保障';
          case 3: return '尊享保障';
          default: return '-';
        }
      },
    },
    {
      title: '价格(元)',
      dataIndex: 'price',
      render: (value) => (value / 100).toFixed(2),
    },
    {
      title: '起赔额(元)',
      dataIndex: 'deductible',
      render: (value) => (value / 100).toFixed(2),
    },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      render: (text) => new Date(text).toLocaleString(),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space size="middle">
          <Button 
            type="link" 
            icon={<EditOutlined />} 
            onClick={() => openModal(record)}
          >
            编辑
          </Button>
        </Space>
      ),
    },
  ];

  const cancellationColumns: TableColumnsType<CancellationRuleTemplate> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
    },
    {
      title: '模板名称',
      dataIndex: 'templateName',
    },
    {
      title: '平日规则',
      dataIndex: 'weekdayRule',
    },
    {
      title: '节假日规则',
      dataIndex: 'holidayRule',
    },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      render: (text) => new Date(text).toLocaleString(),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space size="middle">
          <Button 
            type="link" 
            icon={<EditOutlined />} 
            onClick={() => openModal(record)}
          >
            编辑
          </Button>
        </Space>
      ),
    },
  ];

  const policyColumns: TableColumnsType<ServicePolicyTemplate> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
    },
    {
      title: '模板名称',
      dataIndex: 'templateName',
    },
    {
      title: '里程限制',
      dataIndex: 'mileageLimit',
    },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      render: (text) => new Date(text).toLocaleString(),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space size="middle">
          <Button 
            type="link" 
            icon={<EditOutlined />} 
            onClick={() => openModal(record)}
          >
            编辑
          </Button>
        </Space>
      ),
    },
  ];

  const items: TabsProps['items'] = [
    {
      key: 'vas',
      label: '增值服务模板',
      children: (
        <Table
          columns={vasColumns}
          dataSource={vasTemplates}
          loading={loading}
          pagination={{
            ...pagination,
            showSizeChanger: true,
            showQuickJumper: true,
          }}
          onChange={handleTableChange}
          rowKey="id"
        />
      ),
    },
    {
      key: 'cancellation',
      label: '取消规则模板',
      children: (
        <Table
          columns={cancellationColumns}
          dataSource={cancellationTemplates}
          loading={loading}
          pagination={{
            ...pagination,
            showSizeChanger: true,
            showQuickJumper: true,
          }}
          onChange={handleTableChange}
          rowKey="id"
        />
      ),
    },
    {
      key: 'policy',
      label: '服务政策模板',
      children: (
        <Table
          columns={policyColumns}
          dataSource={policyTemplates}
          loading={loading}
          pagination={{
            ...pagination,
            showSizeChanger: true,
            showQuickJumper: true,
          }}
          onChange={handleTableChange}
          rowKey="id"
        />
      ),
    },
  ];

  return (
    <MainLayout title="模板管理">
      <Card>
        <div style={{ marginBottom: 16 }}>
          <Button 
            type="primary" 
            icon={<PlusOutlined />} 
            onClick={() => openModal()}
          >
            新增模板
          </Button>
        </div>
        
        <Tabs 
          activeKey={activeTab} 
          items={items} 
          onChange={handleTabChange}
        />
      </Card>
      
      {/* 添加/编辑模板模态框 */}
      <Modal
        title={editingTemplate ? "编辑模板" : "新增模板"}
        open={modalVisible}
        onCancel={closeModal}
        footer={null}
        width={800}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          {activeTab === 'vas' && (
            <>
              <Form.Item
                name="templateName"
                label="模板名称"
                rules={[{ required: true, message: '请输入模板名称' }]}
              >
                <Input placeholder="请输入模板名称" />
              </Form.Item>
              
              <Form.Item
                name="serviceType"
                label="服务类型"
                rules={[{ required: true, message: '请选择服务类型' }]}
              >
                <Select placeholder="请选择服务类型">
                  <Select.Option value={1}>基础保障</Select.Option>
                  <Select.Option value={2}>优享保障</Select.Option>
                  <Select.Option value={3}>尊享保障</Select.Option>
                </Select>
              </Form.Item>
              
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="price"
                    label="价格(元)"
                    rules={[{ required: true, message: '请输入价格' }]}
                  >
                    <InputNumber 
                      placeholder="请输入价格" 
                      min={0} 
                      step={0.01}
                      style={{ width: '100%' }} 
                    />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="deductible"
                    label="起赔额(元)"
                    rules={[{ required: true, message: '请输入起赔额' }]}
                  >
                    <InputNumber 
                      placeholder="请输入起赔额" 
                      min={0} 
                      step={0.01}
                      style={{ width: '100%' }} 
                    />
                  </Form.Item>
                </Col>
              </Row>
              
              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item
                    name="includeTireDamage"
                    label="包含轮胎损失"
                    rules={[{ required: true, message: '请选择是否包含轮胎损失' }]}
                  >
                    <Select placeholder="请选择">
                      <Select.Option value={0}>否</Select.Option>
                      <Select.Option value={1}>是</Select.Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item
                    name="includeGlassDamage"
                    label="包含玻璃损失"
                    rules={[{ required: true, message: '请选择是否包含玻璃损失' }]}
                  >
                    <Select placeholder="请选择">
                      <Select.Option value={0}>否</Select.Option>
                      <Select.Option value={1}>是</Select.Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item
                    name="chargeDepreciation"
                    label="收取折旧费"
                    rules={[{ required: true, message: '请选择是否收取折旧费' }]}
                  >
                    <Select placeholder="请选择">
                      <Select.Option value={0}>否</Select.Option>
                      <Select.Option value={1}>是</Select.Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>
              
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="thirdPartyCoverage"
                    label="第三方保障(万元)"
                    rules={[{ required: true, message: '请输入第三方保障' }]}
                  >
                    <InputNumber 
                      placeholder="请输入第三方保障" 
                      min={0}
                      style={{ width: '100%' }} 
                    />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="depreciationDeductible"
                    label="折旧费免赔额(元)"
                    rules={[{ required: true, message: '请输入折旧费免赔额' }]}
                  >
                    <InputNumber 
                      placeholder="请输入折旧费免赔额" 
                      min={0} 
                      step={0.01}
                      style={{ width: '100%' }} 
                    />
                  </Form.Item>
                </Col>
              </Row>
              
              <Form.Item
                name="depreciationRate"
                label="折旧费收取比例(%)"
                rules={[{ required: true, message: '请输入折旧费收取比例' }]}
              >
                <InputNumber 
                  placeholder="请输入折旧费收取比例" 
                  min={0} 
                  max={100}
                  style={{ width: '100%' }} 
                />
              </Form.Item>
            </>
          )}
          
          {activeTab === 'cancellation' && (
            <>
              <Form.Item
                name="templateName"
                label="模板名称"
                rules={[{ required: true, message: '请输入模板名称' }]}
              >
                <Input placeholder="请输入模板名称" />
              </Form.Item>
              
              <Form.Item
                name="weekdayRule"
                label="平日取消规则"
                rules={[{ required: true, message: '请输入平日取消规则' }]}
              >
                <Input.TextArea placeholder="请输入平日取消规则" rows={3} />
              </Form.Item>
              
              <Form.Item
                name="holidayRule"
                label="节假日取消规则"
                rules={[{ required: true, message: '请输入节假日取消规则' }]}
              >
                <Input.TextArea placeholder="请输入节假日取消规则" rows={3} />
              </Form.Item>
            </>
          )}
          
          {activeTab === 'policy' && (
            <>
              <Form.Item
                name="templateName"
                label="模板名称"
                rules={[{ required: true, message: '请输入模板名称' }]}
              >
                <Input placeholder="请输入模板名称" />
              </Form.Item>
              
              <Form.Item
                name="mileageLimit"
                label="里程限制"
              >
                <Input placeholder="请输入里程限制" />
              </Form.Item>
              
              <Form.Item
                name="earlyPickup"
                label="提前取车"
              >
                <Input placeholder="请输入提前取车规则" />
              </Form.Item>
              
              <Form.Item
                name="latePickup"
                label="延迟取车"
              >
                <Input placeholder="请输入延迟取车规则" />
              </Form.Item>
              
              <Form.Item
                name="earlyReturn"
                label="提前还车"
              >
                <Input placeholder="请输入提前还车规则" />
              </Form.Item>
              
              <Form.Item
                name="renewal"
                label="续租"
              >
                <Input placeholder="请输入续租规则" />
              </Form.Item>
              
              <Form.Item
                name="forcedRenewal"
                label="强行续租"
              >
                <Input placeholder="请输入强行续租规则" />
              </Form.Item>
              
              <Form.Item
                name="pickupMaterials"
                label="取车材料"
              >
                <Input placeholder="请输入取车所需材料" />
              </Form.Item>
              
              <Form.Item
                name="cityRestriction"
                label="城市限行规则"
              >
                <Input placeholder="请输入城市限行规则" />
              </Form.Item>
              
              <Form.Item
                name="usageAreaLimit"
                label="用车区域限制"
              >
                <Input placeholder="请输入用车区域限制" />
              </Form.Item>
              
              <Form.Item
                name="fuelFee"
                label="油费电费"
              >
                <Input placeholder="请输入油费电费规则" />
              </Form.Item>
              
              <Form.Item
                name="personalBelongingsLoss"
                label="随车物品损失"
              >
                <Input placeholder="请输入随车物品损失规则" />
              </Form.Item>
              
              <Form.Item
                name="violationHandling"
                label="违章处理"
              >
                <Input placeholder="请输入违章处理规则" />
              </Form.Item>
              
              <Form.Item
                name="roadsideAssistance"
                label="道路救援"
              >
                <Input placeholder="请输入道路救援规则" />
              </Form.Item>
              
              <Form.Item
                name="forcedRecovery"
                label="强制收车"
              >
                <Input placeholder="请输入强制收车规则" />
              </Form.Item>
              
              <Form.Item
                name="etcFee"
                label="ETC费用"
              >
                <Input placeholder="请输入ETC费用规则" />
              </Form.Item>
              
              <Form.Item
                name="cleaningFee"
                label="清洁费"
              >
                <Input placeholder="请输入清洁费规则" />
              </Form.Item>
              
              <Form.Item
                name="invoiceInfo"
                label="发票说明"
              >
                <Input placeholder="请输入发票说明" />
              </Form.Item>
            </>
          )}
          
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                {editingTemplate ? '更新' : '添加'}
              </Button>
              <Button onClick={closeModal}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </MainLayout>
  );
};

export default TemplateManagement;
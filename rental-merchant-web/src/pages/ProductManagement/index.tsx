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
  Switch,
  Tag,
  Checkbox
} from 'antd';
import type { TableColumnsType } from 'antd';
import { 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined,
  SearchOutlined,
  UploadOutlined
} from '@ant-design/icons';
import MainLayout from '../../components/MainLayout';
import { productAPI } from '../../services/api';
import { useProductData } from '../../hooks/useProductData';
import { useVehicleRelations } from '../../hooks/useVehicleRelations';
import VehicleRelationModal from '../../components/VehicleRelationModal';
import SpecialPricingCalendarModal from '../../components/SpecialPricingCalendarModal';

interface CarModelProduct {
  id: number;
  tenantId: number;
  storeId: number;
  carModelId: number;
  productName: string;
  productDescription: string;
  damageDeposit: number;
  violationDeposit: number;
  weekdayPrice: number;
  weekendPrice: number;
  weekdayDefinition: string;
  weekendDefinition: string;
  tags: string;
  vasTemplateId: number;
  vasTemplateIdVip: number;
  vasTemplateIdVvip: number;
  cancellationTemplateId: number;
  servicePolicyTemplateId: number;
  onlineStatus: number;
  createdTime: string;
  updatedTime: string;
}

const ProductManagement: React.FC = () => {
  const [products, setProducts] = useState<CarModelProduct[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingProduct, setEditingProduct] = useState<CarModelProduct | null>(null);
  const [vehicleModalVisible, setVehicleModalVisible] = useState(false);
  const [pricingModalVisible, setPricingModalVisible] = useState(false);
  const [currentProductId, setCurrentProductId] = useState<number | null>(null);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });

  // 使用自定义Hook获取相关数据
  const { stores, carModels, vasTemplates, cancellationTemplates, policyTemplates, refresh } = useProductData();

  // 获取商品列表
  const fetchProducts = async (page = 1, pageSize = 10, searchParams: any = {}) => {
    setLoading(true);
    try {
      const response: any = await productAPI.getProductList({
        current: page,
        size: pageSize,
        ...searchParams
      });
      
      if (response.code === 200) {
        setProducts(response.data.records);
        setPagination({
          current: response.data.pageNum,
          pageSize: response.data.pageSize,
          total: response.data.total,
        });
      } else {
        message.error(response.message || '获取商品列表失败');
      }
    } catch (error) {
      message.error('获取商品列表失败');
    } finally {
      setLoading(false);
    }
  };

  // 处理分页变化
  const handleTableChange = (pagination: any) => {
    const { current, pageSize } = pagination;
    const values = searchForm.getFieldsValue();
    // 过滤掉undefined和空值，避免传递给后端
    const filteredValues = Object.fromEntries(
      Object.entries(values).filter(([_, value]) => value !== undefined && value !== null && value !== '')
    );
    fetchProducts(current, pageSize, filteredValues);
  };

  // 处理搜索
  const handleSearch = () => {
    const values = searchForm.getFieldsValue();
    // 过滤掉undefined和空值，避免传递给后端
    const filteredValues = Object.fromEntries(
      Object.entries(values).filter(([_, value]) => value !== undefined && value !== null && value !== '')
    );
    fetchProducts(1, pagination.pageSize, filteredValues);
  };

  // 重置搜索
  const handleReset = () => {
    searchForm.resetFields();
    fetchProducts(1, pagination.pageSize, {});
  };

  // 打开添加/编辑模态框
  const openModal = (record?: CarModelProduct) => {
    if (record) {
      setEditingProduct(record);
      // 解析周定义字符串为数组
      const weekdayDefinition = record.weekdayDefinition ? record.weekdayDefinition.split(',') : [];
      const weekendDefinition = record.weekendDefinition ? record.weekendDefinition.split(',') : [];
      
      form.setFieldsValue({
        ...record,
        // 金额转换为元
        damageDeposit: record.damageDeposit / 100,
        violationDeposit: record.violationDeposit / 100,
        weekdayPrice: record.weekdayPrice / 100,
        weekendPrice: record.weekendPrice / 100,
        // 周定义
        weekdayDefinition,
        weekendDefinition,
      });
    } else {
      setEditingProduct(null);
      form.resetFields();
      // 设置默认值
      form.setFieldsValue({
        weekdayDefinition: defaultWeekdayDefinition,
        weekendDefinition: defaultWeekendDefinition,
      });
    }
    setModalVisible(true);
  };

  // 关闭模态框
  const closeModal = () => {
    setModalVisible(false);
    form.resetFields();
    setEditingProduct(null);
  };

  // 打开车辆关联模态框
  const openVehicleModal = (productId: number) => {
    setCurrentProductId(productId);
    setVehicleModalVisible(true);
  };

  // 关闭车辆关联模态框
  const closeVehicleModal = () => {
    setVehicleModalVisible(false);
    setCurrentProductId(null);
  };

  // 打开特殊定价模态框
  const openPricingModal = (productId: number) => {
    setCurrentProductId(productId);
    setPricingModalVisible(true);
  };

  // 关闭特殊定价模态框
  const closePricingModal = () => {
    setPricingModalVisible(false);
    setCurrentProductId(null);
  };

  // 提交表单
  const handleSubmit = async (values: any) => {
    // 检查周中和周末定义是否有重复
    const weekdayDef = values.weekdayDefinition || [];
    const weekendDef = values.weekendDefinition || [];
    const intersection = weekdayDef.filter((day: string) => weekendDef.includes(day));
    
    if (intersection.length > 0) {
      message.error('周中定义和周末定义不能有重复的天数');
      return;
    }
    
    try {
      // 金额转换为分
      const submitValues = {
        ...values,
        // 将周定义数组转换为字符串
        weekdayDefinition: weekdayDef.join(','),
        weekendDefinition: weekendDef.join(','),
        damageDeposit: Math.round(values.damageDeposit * 100),
        violationDeposit: Math.round(values.violationDeposit * 100),
        weekdayPrice: Math.round(values.weekdayPrice * 100),
        weekendPrice: Math.round(values.weekendPrice * 100),
      };
      
      let response: any;
      if (editingProduct) {
        // 编辑商品
        response = await productAPI.updateProduct(editingProduct.id, submitValues);
      } else {
        // 添加商品
        response = await productAPI.createProduct(submitValues);
      }
      
      if (response.code === 200) {
        message.success(editingProduct ? '更新成功' : '添加成功');
        closeModal();
        const searchParams = searchForm.getFieldsValue();
        // 过滤掉undefined和空值，避免传递给后端
        const filteredParams = Object.fromEntries(
          Object.entries(searchParams).filter(([_, value]) => value !== undefined && value !== null && value !== '')
        );
        fetchProducts(pagination.current, pagination.pageSize, filteredParams);
      } else {
        message.error(response.message || (editingProduct ? '更新失败' : '添加失败'));
      }
    } catch (error) {
      message.error(editingProduct ? '更新失败' : '添加失败');
    }
  };

  // 删除商品
  const handleDelete = async (id: number) => {
    try {
      const response: any = await productAPI.deleteProduct(id);
      if (response.code === 200) {
        message.success('删除成功');
        const searchParams = searchForm.getFieldsValue();
        // 过滤掉undefined和空值，避免传递给后端
        const filteredParams = Object.fromEntries(
          Object.entries(searchParams).filter(([_, value]) => value !== undefined && value !== null && value !== '')
        );
        fetchProducts(pagination.current, pagination.pageSize, filteredParams);
      } else {
        message.error(response.message || '删除失败');
      }
    } catch (error) {
      message.error('删除失败');
    }
  };

  // 上下架商品
  const handleOnlineStatusChange = async (id: number, onlineStatus: number) => {
    try {
      const response: any = onlineStatus === 1 
        ? await productAPI.onlineProduct(id) 
        : await productAPI.offlineProduct(id);
      
      if (response.code === 200) {
        message.success(onlineStatus === 1 ? '上架成功' : '下架成功');
        const searchParams = searchForm.getFieldsValue();
        // 过滤掉undefined和空值，避免传递给后端
        const filteredParams = Object.fromEntries(
          Object.entries(searchParams).filter(([_, value]) => value !== undefined && value !== null && value !== '')
        );
        fetchProducts(pagination.current, pagination.pageSize, filteredParams);
      } else {
        message.error(response.message || (onlineStatus === 1 ? '上架失败' : '下架失败'));
      }
    } catch (error) {
      message.error(onlineStatus === 1 ? '上架失败' : '下架失败');
    }
  };

  // 周定义选项
  const weekOptions = [
    { label: '周一', value: '1' },
    { label: '周二', value: '2' },
    { label: '周三', value: '3' },
    { label: '周四', value: '4' },
    { label: '周五', value: '5' },
    { label: '周六', value: '6' },
    { label: '周日', value: '7' },
  ];

  // 默认周中定义（周一到周五）
  const defaultWeekdayDefinition = ['1', '2', '3', '4', '5'];
  // 默认周末定义（周六、周日）
  const defaultWeekendDefinition = ['6', '7'];

  // 处理周中定义变化
  const handleWeekdayChange = (checkedValues: any[]) => {
    const weekendValues = form.getFieldValue('weekendDefinition') || [];
    // 检查是否有交集
    const intersection = checkedValues.filter(value => weekendValues.includes(value));
    if (intersection.length > 0) {
      // 如果有交集，从周末定义中移除交集的值
      const newWeekendValues = weekendValues.filter(value => !checkedValues.includes(value));
      form.setFieldsValue({ weekendDefinition: newWeekendValues });
    }
  };

  // 处理周末定义变化
  const handleWeekendChange = (checkedValues: any[]) => {
    const weekdayValues = form.getFieldValue('weekdayDefinition') || [];
    // 检查是否有交集
    const intersection = checkedValues.filter(value => weekdayValues.includes(value));
    if (intersection.length > 0) {
      // 如果有交集，从周中定义中移除交集的值
      const newWeekdayValues = weekdayValues.filter(value => !checkedValues.includes(value));
      form.setFieldsValue({ weekdayDefinition: newWeekdayValues });
    }
  };

  // 初始化数据
  useEffect(() => {
    fetchProducts();
  }, []);

  const columns: TableColumnsType<CarModelProduct> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
    },
    {
      title: '商品名称',
      dataIndex: 'productName',
      sorter: true,
    },
    {
      title: '门店',
      dataIndex: 'storeId',
      render: (value) => {
        const store = stores.find(s => s.id === value);
        return store ? store.storeName : value;
      },
    },
    {
      title: '车型',
      dataIndex: 'carModelId',
      render: (value) => {
        const carModel = carModels.find(c => c.id === value);
        return carModel ? `${carModel.brand} ${carModel.series} ${carModel.model}` : value;
      },
    },
    {
      title: '周中价格(元)',
      dataIndex: 'weekdayPrice',
      render: (value) => (value / 100).toFixed(2),
    },
    {
      title: '周末价格(元)',
      dataIndex: 'weekendPrice',
      render: (value) => (value / 100).toFixed(2),
    },
    {
      title: '车损押金(元)',
      dataIndex: 'damageDeposit',
      render: (value) => (value / 100).toFixed(2),
    },
    {
      title: '违章押金(元)',
      dataIndex: 'violationDeposit',
      render: (value) => (value / 100).toFixed(2),
    },
    {
      title: '状态',
      dataIndex: 'onlineStatus',
      render: (value) => (
        <Tag color={value === 1 ? 'green' : 'red'}>
          {value === 1 ? '已上架' : '已下架'}
        </Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      render: (text) => new Date(text).toLocaleString(),
    },
    {
      title: '操作',
      key: 'action',
      width: 250,
      render: (_, record) => (
        <Space size="middle">
          <Button 
            type="link" 
            icon={<EditOutlined />} 
            onClick={() => openModal(record)}
          >
            编辑
          </Button>
          <Button 
            type="link" 
            onClick={() => openVehicleModal(record.id)}
          >
            关联车辆
          </Button>
          <Button 
            type="link" 
            onClick={() => openPricingModal(record.id)}
          >
            特殊定价
          </Button>
          <Button 
            type="link" 
            icon={record.onlineStatus === 1 ? <UploadOutlined /> : <UploadOutlined />} 
            onClick={() => handleOnlineStatusChange(record.id, record.onlineStatus === 1 ? 0 : 1)}
          >
            {record.onlineStatus === 1 ? '下架' : '上架'}
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <MainLayout title="商品管理">
      <Card>
        {/* 搜索表单 */}
        <Form form={searchForm} layout="inline" onFinish={handleSearch}>
          <Row gutter={16} style={{ width: '100%' }}>
            <Col span={6}>
              <Form.Item name="productName" label="商品名称">
                <Input placeholder="请输入商品名称" />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="storeId" label="门店">
                <Select placeholder="请选择门店" allowClear>
                  {stores.map(store => (
                    <Select.Option key={store.id} value={store.id}>
                      {store.storeName}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="onlineStatus" label="上架状态">
                <Select placeholder="请选择状态" allowClear>
                  <Select.Option value={1}>已上架</Select.Option>
                  <Select.Option value={0}>已下架</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item>
                <Space>
                  <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                    搜索
                  </Button>
                  <Button onClick={handleReset}>
                    重置
                  </Button>
                </Space>
              </Form.Item>
            </Col>
          </Row>
        </Form>
        
        <Divider />
        
        {/* 操作按钮 */}
        <div style={{ marginBottom: 16 }}>
          <Button 
            type="primary" 
            icon={<PlusOutlined />} 
            onClick={() => openModal()}
          >
            新增商品
          </Button>
        </div>
        
        {/* 商品列表 */}
        <Table
          columns={columns}
          dataSource={products}
          loading={loading}
          pagination={{
            ...pagination,
            showSizeChanger: true,
            showQuickJumper: true,
          }}
          onChange={handleTableChange}
          rowKey="id"
        />
      </Card>
      
      {/* 添加/编辑商品模态框 */}
      <Modal
        title={editingProduct ? "编辑商品" : "新增商品"}
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
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="storeId"
                label="门店"
                rules={[{ required: true, message: '请选择门店' }]}
              >
                <Select placeholder="请选择门店">
                  {stores.map(store => (
                    <Select.Option key={store.id} value={store.id}>
                      {store.storeName}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="productName"
                label="商品名称"
                rules={[{ required: true, message: '请输入商品名称' }]}
              >
                <Input placeholder="请输入商品名称" />
              </Form.Item>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="carModelId"
                label="车型"
                rules={[{ required: true, message: '请选择车型' }]}
              >
                <Select 
                  placeholder="请选择车型" 
                  showSearch
                  optionFilterProp="children"
                  filterOption={(input, option) => 
                    (option?.children as unknown as string)?.toLowerCase().indexOf(input.toLowerCase()) >= 0
                  }
                >
                  {carModels.map(carModel => (
                    <Select.Option key={carModel.id} value={carModel.id}>
                      {`${carModel.brand} ${carModel.series} ${carModel.model}`}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="weekdayPrice"
                label="周中价格(元)"
                rules={[{ required: true, message: '请输入周中价格' }]}
              >
                <InputNumber 
                  placeholder="请输入周中价格" 
                  min={0} 
                  step={0.01}
                  style={{ width: '100%' }} 
                />
              </Form.Item>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="weekendPrice"
                label="周末价格(元)"
                rules={[{ required: true, message: '请输入周末价格' }]}
              >
                <InputNumber 
                  placeholder="请输入周末价格" 
                  min={0} 
                  step={0.01}
                  style={{ width: '100%' }} 
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="damageDeposit"
                label="车损押金(元)"
                rules={[{ required: true, message: '请输入车损押金' }]}
              >
                <InputNumber 
                  placeholder="请输入车损押金" 
                  min={0} 
                  step={0.01}
                  style={{ width: '100%' }} 
                />
              </Form.Item>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="violationDeposit"
                label="违章押金(元)"
                rules={[{ required: true, message: '请输入违章押金' }]}
              >
                <InputNumber 
                  placeholder="请输入违章押金" 
                  min={0} 
                  step={0.01}
                  style={{ width: '100%' }} 
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="vasTemplateId"
                label="基础保障模板"
              >
                <Select placeholder="请选择基础保障模板" allowClear>
                  {vasTemplates
                    .filter((template: any) => template.serviceType === 1) // 只显示基础保障类型
                    .map((template: any) => (
                      <Select.Option key={template.id} value={template.id}>
                        {template.templateName}
                      </Select.Option>
                    ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="vasTemplateIdVip"
                label="优享保障模板"
              >
                <Select placeholder="请选择优享保障模板" allowClear>
                  {vasTemplates
                    .filter((template: any) => template.serviceType === 2) // 只显示优享保障类型
                    .map((template: any) => (
                      <Select.Option key={template.id} value={template.id}>
                        {template.templateName}
                      </Select.Option>
                    ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="vasTemplateIdVvip"
                label="尊享保障模板"
              >
                <Select placeholder="请选择尊享保障模板" allowClear>
                  {vasTemplates
                    .filter((template: any) => template.serviceType === 3) // 只显示尊享保障类型
                    .map((template: any) => (
                      <Select.Option key={template.id} value={template.id}>
                        {template.templateName}
                      </Select.Option>
                    ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="cancellationTemplateId"
                label="取消规则模板"
              >
                <Select placeholder="请选择取消规则模板" allowClear>
                  {cancellationTemplates.map((template: any) => (
                    <Select.Option key={template.id} value={template.id}>
                      {template.templateName}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="servicePolicyTemplateId"
                label="服务政策模板"
              >
                <Select placeholder="请选择服务政策模板" allowClear>
                  {policyTemplates.map((template: any) => (
                    <Select.Option key={template.id} value={template.id}>
                      {template.templateName}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          <Form.Item
            name="weekdayDefinition"
            label="周中定义"
            rules={[{ required: true, message: '请选择周中定义' }]}
          >
            <Checkbox.Group 
              options={weekOptions} 
              onChange={handleWeekdayChange}
            />
          </Form.Item>
          
          <Form.Item
            name="weekendDefinition"
            label="周末定义"
            rules={[{ required: true, message: '请选择周末定义' }]}
          >
            <Checkbox.Group 
              options={weekOptions} 
              onChange={handleWeekendChange}
            />
          </Form.Item>
          
          <Form.Item
            name="productDescription"
            label="商品描述"
          >
            <Input.TextArea placeholder="请输入商品描述" rows={3} />
          </Form.Item>
          
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                {editingProduct ? '更新' : '添加'}
              </Button>
              <Button onClick={closeModal}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      {/* 车辆关联模态框 */}
      {vehicleModalVisible && (
        <VehicleRelationModal
          visible={vehicleModalVisible}
          productId={currentProductId}
          onCancel={closeVehicleModal}
          onOk={closeVehicleModal}
        />
      )}
      
      {/* 特殊定价模态框 */}
      {pricingModalVisible && (
        <SpecialPricingCalendarModal
          visible={pricingModalVisible}
          productId={currentProductId}
          onCancel={closePricingModal}
          onOk={closePricingModal}
        />
      )}
    </MainLayout>
  );
};

export default ProductManagement;
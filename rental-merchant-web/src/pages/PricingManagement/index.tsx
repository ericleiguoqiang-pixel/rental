import React, { useState, useEffect } from 'react';
import { 
  Table, 
  Button, 
  Modal, 
  Form, 
  InputNumber, 
  Space, 
  message, 
  Card, 
  Row, 
  Col,
  Divider,
  DatePicker,
  Select
} from 'antd';
import type { TableColumnsType } from 'antd';
import { 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined,
  SearchOutlined
} from '@ant-design/icons';
import MainLayout from '../../components/MainLayout';
import { pricingAPI } from '../../services/api';
import dayjs from 'dayjs';

interface SpecialPricing {
  id: number;
  productId: number;
  priceDate: string;
  price: number;
  createdTime: string;
  updatedTime: string;
}

const PricingManagement: React.FC = () => {
  const [pricings, setPricings] = useState<SpecialPricing[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingPricing, setEditingPricing] = useState<SpecialPricing | null>(null);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });

  // 获取特殊定价列表
  const fetchPricings = async (page = 1, pageSize = 10, searchParams: any = {}) => {
    setLoading(true);
    try {
      const response: any = await pricingAPI.getPricingList({
        current: page,
        size: pageSize,
        ...searchParams
      });
      
      if (response.code === 200) {
        setPricings(response.data.records);
        setPagination({
          current: response.data.pageNum,
          pageSize: response.data.pageSize,
          total: response.data.total,
        });
      } else {
        message.error(response.message || '获取特殊定价列表失败');
      }
    } catch (error) {
      message.error('获取特殊定价列表失败');
    } finally {
      setLoading(false);
    }
  };

  // 处理分页变化
  const handleTableChange = (pagination: any) => {
    const { current, pageSize } = pagination;
    const searchParams = searchForm.getFieldsValue();
    fetchPricings(current, pageSize, searchParams);
  };

  // 处理搜索
  const handleSearch = () => {
    const values = searchForm.getFieldsValue();
    // 转换日期格式
    if (values.startDate) {
      values.startDate = dayjs(values.startDate).format('YYYY-MM-DD');
    }
    if (values.endDate) {
      values.endDate = dayjs(values.endDate).format('YYYY-MM-DD');
    }
    fetchPricings(1, pagination.pageSize, values);
  };

  // 重置搜索
  const handleReset = () => {
    searchForm.resetFields();
    fetchPricings(1, pagination.pageSize);
  };

  // 打开添加/编辑模态框
  const openModal = (record?: SpecialPricing) => {
    if (record) {
      setEditingPricing(record);
      form.setFieldsValue({
        ...record,
        priceDate: dayjs(record.priceDate),
        // 金额转换为元
        price: record.price / 100,
      });
    } else {
      setEditingPricing(null);
      form.resetFields();
    }
    setModalVisible(true);
  };

  // 关闭模态框
  const closeModal = () => {
    setModalVisible(false);
    form.resetFields();
    setEditingPricing(null);
  };

  // 提交表单
  const handleSubmit = async (values: any) => {
    try {
      // 转换日期格式和金额
      const submitValues = {
        ...values,
        priceDate: dayjs(values.priceDate).format('YYYY-MM-DD'),
        price: Math.round(values.price * 100),
      };
      
      let response: any;
      if (editingPricing) {
        // 编辑特殊定价
        response = await pricingAPI.updatePricing(editingPricing.id, submitValues);
      } else {
        // 添加特殊定价
        response = await pricingAPI.createPricing(submitValues);
      }
      
      if (response.code === 200) {
        message.success(editingPricing ? '更新成功' : '添加成功');
        closeModal();
        fetchPricings(pagination.current, pagination.pageSize, searchForm.getFieldsValue());
      } else {
        message.error(response.message || (editingPricing ? '更新失败' : '添加失败'));
      }
    } catch (error) {
      message.error(editingPricing ? '更新失败' : '添加失败');
    }
  };

  // 删除特殊定价
  const handleDelete = async (id: number) => {
    try {
      const response: any = await pricingAPI.deletePricing(id);
      if (response.code === 200) {
        message.success('删除成功');
        fetchPricings(pagination.current, pagination.pageSize, searchForm.getFieldsValue());
      } else {
        message.error(response.message || '删除失败');
      }
    } catch (error) {
      message.error('删除失败');
    }
  };

  // 初始化数据
  useEffect(() => {
    fetchPricings();
  }, []);

  const columns: TableColumnsType<SpecialPricing> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
    },
    {
      title: '商品ID',
      dataIndex: 'productId',
    },
    {
      title: '定价日期',
      dataIndex: 'priceDate',
    },
    {
      title: '价格(元)',
      dataIndex: 'price',
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
          <Button 
            type="link" 
            danger 
            icon={<DeleteOutlined />} 
            onClick={() => handleDelete(record.id)}
          >
            删除
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <MainLayout title="特殊定价管理">
      <Card>
        {/* 搜索表单 */}
        <Form form={searchForm} layout="inline" onFinish={handleSearch}>
          <Row gutter={16} style={{ width: '100%' }}>
            <Col span={6}>
              <Form.Item name="productId" label="商品ID">
                <InputNumber placeholder="请输入商品ID" style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="startDate" label="开始日期">
                <DatePicker style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="endDate" label="结束日期">
                <DatePicker style={{ width: '100%' }} />
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
            新增特殊定价
          </Button>
        </div>
        
        {/* 特殊定价列表 */}
        <Table
          columns={columns}
          dataSource={pricings}
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
      
      {/* 添加/编辑特殊定价模态框 */}
      <Modal
        title={editingPricing ? "编辑特殊定价" : "新增特殊定价"}
        open={modalVisible}
        onCancel={closeModal}
        footer={null}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="productId"
                label="商品ID"
                rules={[{ required: true, message: '请输入商品ID' }]}
              >
                <InputNumber placeholder="请输入商品ID" style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="priceDate"
                label="定价日期"
                rules={[{ required: true, message: '请选择定价日期' }]}
              >
                <DatePicker style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>
          
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
          
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                {editingPricing ? '更新' : '添加'}
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

export default PricingManagement;
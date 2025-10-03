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
  Popconfirm
} from 'antd';
import type { TableColumnsType } from 'antd';
import { 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined,
  SearchOutlined
} from '@ant-design/icons';
import MainLayout from '../../components/MainLayout';
import { carModelAPI } from '../../services/api';

interface CarModel {
  id: number;
  brand: string;
  series: string;
  model: string;
  year: number;
  transmission: number;
  driveType: number;
  seatCount: number;
  doorCount: number;
  category: string;
  description: string;
  createdTime: string;
  updatedTime: string;
}

const CarModelManagement: React.FC = () => {
  const [carModels, setCarModels] = useState<CarModel[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingModel, setEditingModel] = useState<CarModel | null>(null);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });

  // 获取车型列表
  const fetchCarModels = async (page = 1, pageSize = 10, searchParams: any = {}) => {
    setLoading(true);
    try {
      const response: any = await carModelAPI.getCarModelList({
        current: page,
        size: pageSize,
        ...searchParams
      });
      
      if (response.code === 200) {
        setCarModels(response.data.records);
        setPagination({
          current: response.data.pageNum,
          pageSize: response.data.pageSize,
          total: response.data.total,
        });
      } else {
        message.error(response.message || '获取车型列表失败');
      }
    } catch (error) {
      message.error('获取车型列表失败');
    } finally {
      setLoading(false);
    }
  };

  // 处理分页变化
  const handleTableChange = (pagination: any) => {
    const { current, pageSize } = pagination;
    const searchParams = searchForm.getFieldsValue();
    fetchCarModels(current, pageSize, searchParams);
  };

  // 处理搜索
  const handleSearch = () => {
    const values = searchForm.getFieldsValue();
    fetchCarModels(1, pagination.pageSize, values);
  };

  // 重置搜索
  const handleReset = () => {
    searchForm.resetFields();
    fetchCarModels(1, pagination.pageSize);
  };

  // 打开添加/编辑模态框
  const openModal = (record?: CarModel) => {
    if (record) {
      setEditingModel(record);
      form.setFieldsValue(record);
    } else {
      setEditingModel(null);
      form.resetFields();
    }
    setModalVisible(true);
  };

  // 关闭模态框
  const closeModal = () => {
    setModalVisible(false);
    form.resetFields();
    setEditingModel(null);
  };

  // 提交表单
  const handleSubmit = async (values: any) => {
    try {
      let response: any;
      if (editingModel) {
        // 编辑车型
        response = await carModelAPI.updateCarModel(editingModel.id, values);
      } else {
        // 添加车型
        response = await carModelAPI.createCarModel(values);
      }
      
      if (response.code === 200) {
        message.success(editingModel ? '更新成功' : '添加成功');
        closeModal();
        fetchCarModels(pagination.current, pagination.pageSize, searchForm.getFieldsValue());
      } else {
        message.error(response.message || (editingModel ? '更新失败' : '添加失败'));
      }
    } catch (error) {
      message.error(editingModel ? '更新失败' : '添加失败');
    }
  };

  // 删除车型
  const handleDelete = async (id: number) => {
    try {
      const response: any = await carModelAPI.deleteCarModel(id);
      if (response.code === 200) {
        message.success('删除成功');
        fetchCarModels(pagination.current, pagination.pageSize, searchForm.getFieldsValue());
      } else {
        message.error(response.message || '删除失败');
      }
    } catch (error) {
      message.error('删除失败');
    }
  };

  // 初始化数据
  useEffect(() => {
    fetchCarModels();
  }, []);

  const columns: TableColumnsType<CarModel> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
    },
    {
      title: '品牌',
      dataIndex: 'brand',
      sorter: true,
    },
    {
      title: '车系',
      dataIndex: 'series',
      sorter: true,
    },
    {
      title: '车型',
      dataIndex: 'model',
    },
    {
      title: '年款',
      dataIndex: 'year',
      sorter: true,
    },
    {
      title: '档位',
      dataIndex: 'transmission',
      render: (value) => value === 1 ? '自动' : '手动',
    },
    {
      title: '驱动类型',
      dataIndex: 'driveType',
      render: (value) => {
        switch (value) {
          case 1: return '燃油';
          case 2: return '纯电';
          case 3: return '混动';
          default: return '-';
        }
      },
    },
    {
      title: '座位数',
      dataIndex: 'seatCount',
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
          <Popconfirm
            title="确认删除"
            description="确定要删除这个车型吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确认"
            cancelText="取消"
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <MainLayout title="车型管理">
      <Card>
        {/* 搜索表单 */}
        <Form form={searchForm} layout="inline" onFinish={handleSearch}>
          <Row gutter={16} style={{ width: '100%' }}>
            <Col span={6}>
              <Form.Item name="brand" label="品牌">
                <Input placeholder="请输入品牌" />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="series" label="车系">
                <Input placeholder="请输入车系" />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="seatCount" label="座位数">
                <InputNumber placeholder="请输入座位数" style={{ width: '100%' }} />
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
            新增车型
          </Button>
        </div>
        
        {/* 车型列表 */}
        <Table
          columns={columns}
          dataSource={carModels}
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
      
      {/* 添加/编辑车型模态框 */}
      <Modal
        title={editingModel ? "编辑车型" : "新增车型"}
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
                name="brand"
                label="品牌"
                rules={[{ required: true, message: '请输入品牌' }]}
              >
                <Input placeholder="请输入品牌" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="series"
                label="车系"
                rules={[{ required: true, message: '请输入车系' }]}
              >
                <Input placeholder="请输入车系" />
              </Form.Item>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="model"
                label="车型"
                rules={[{ required: true, message: '请输入车型' }]}
              >
                <Input placeholder="请输入车型" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="year"
                label="年款"
                rules={[{ required: true, message: '请输入年款' }]}
              >
                <InputNumber 
                  placeholder="请输入年款" 
                  min={2000} 
                  max={2030} 
                  style={{ width: '100%' }} 
                />
              </Form.Item>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="transmission"
                label="档位类型"
                rules={[{ required: true, message: '请选择档位类型' }]}
              >
                <Select placeholder="请选择档位类型">
                  <Select.Option value={1}>自动</Select.Option>
                  <Select.Option value={2}>手动</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="driveType"
                label="驱动类型"
                rules={[{ required: true, message: '请选择驱动类型' }]}
              >
                <Select placeholder="请选择驱动类型">
                  <Select.Option value={1}>燃油</Select.Option>
                  <Select.Option value={2}>纯电</Select.Option>
                  <Select.Option value={3}>混动</Select.Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="seatCount"
                label="座位数"
                rules={[{ required: true, message: '请输入座位数' }]}
              >
                <InputNumber 
                  placeholder="请输入座位数" 
                  min={2} 
                  max={9} 
                  style={{ width: '100%' }} 
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="doorCount"
                label="车门数"
                rules={[{ required: true, message: '请输入车门数' }]}
              >
                <InputNumber 
                  placeholder="请输入车门数" 
                  min={2} 
                  max={5} 
                  style={{ width: '100%' }} 
                />
              </Form.Item>
            </Col>
          </Row>
          
          <Form.Item
            name="category"
            label="车型分类"
          >
            <Input placeholder="请输入车型分类，多个用逗号分隔" />
          </Form.Item>
          
          <Form.Item
            name="description"
            label="车型描述"
          >
            <Input.TextArea placeholder="请输入车型描述" rows={3} />
          </Form.Item>
          
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                {editingModel ? '更新' : '添加'}
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

export default CarModelManagement;
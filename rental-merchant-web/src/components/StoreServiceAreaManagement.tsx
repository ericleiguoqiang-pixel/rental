import React, { useEffect, useState } from 'react';
import { Button, Card, Col, Form, Input, InputNumber, Modal, Radio, Row, Space, Table, TimePicker, message } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import ServiceAreaMapModal from './ServiceAreaMapModal/ServiceAreaMapModal';
import { serviceAreaApi } from '../services/api';
import dayjs from 'dayjs';
import './StoreServiceAreaManagement.css';

interface ServiceArea {
  id?: number;
  storeId: number;
  areaName: string;
  areaType: number;
  fenceCoordinates: string;
  advanceHours: number;
  serviceStartTime: string;
  serviceEndTime: string;
  doorToDoorDelivery: number;
  deliveryFee: number;
  freePickupToStore: number;
}

interface StoreServiceAreaManagementProps {
  storeId: string;
  storeName: string;
}

const StoreServiceAreaManagement: React.FC<StoreServiceAreaManagementProps> = ({ storeId, storeName }) => {
  const [form] = Form.useForm();
  const [areas, setAreas] = useState<ServiceArea[]>([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [isMapModalVisible, setIsMapModalVisible] = useState(false);
  const [editingArea, setEditingArea] = useState<ServiceArea | null>(null);
  const [selectedCoordinates, setSelectedCoordinates] = useState<any[]>([]);

  // 获取服务范围列表
  const fetchServiceAreas = async () => {
    setLoading(true);
    try {
      const response = await serviceAreaApi.listByStoreId(Number(storeId));
      setAreas(response.data || []);
    } catch (error) {
      message.error('获取服务范围列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (storeId) {
      fetchServiceAreas();
    }
  }, [storeId]);

  // 处理提交表单
  const handleFinish = async (values: any) => {
    try {
      const serviceArea: ServiceArea = {
        ...editingArea,
        ...values,
        storeId: Number(storeId),
        serviceStartTime: values.serviceTime[0].format('HH:mm'),
        serviceEndTime: values.serviceTime[1].format('HH:mm'),
        fenceCoordinates: JSON.stringify(selectedCoordinates),
      };

      if (editingArea?.id) {
        await serviceAreaApi.update(serviceArea);
        message.success('更新成功');
      } else {
        await serviceAreaApi.create(serviceArea);
        message.success('创建成功');
      }

      setIsModalVisible(false);
      form.resetFields();
      setEditingArea(null);
      setSelectedCoordinates([]);
      fetchServiceAreas();
    } catch (error) {
      message.error(editingArea?.id ? '更新失败' : '创建失败');
    }
  };

  // 处理编辑
  const handleEdit = (record: ServiceArea) => {
    setEditingArea(record);
    form.setFieldsValue({
      ...record,
      serviceTime: [
        record.serviceStartTime ? dayjs(record.serviceStartTime, 'HH:mm') : null,
        record.serviceEndTime ? dayjs(record.serviceEndTime, 'HH:mm') : null,
      ],
    });
    
    // 解析坐标数据
    if (record.fenceCoordinates) {
      try {
        const coords = JSON.parse(record.fenceCoordinates);
        setSelectedCoordinates(coords);
      } catch (e) {
        setSelectedCoordinates([]);
      }
    }
    
    setIsModalVisible(true);
  };

  // 处理删除
  const handleDelete = (id: number) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这个服务范围吗？',
      onOk: async () => {
        try {
          await serviceAreaApi.delete(id);
          message.success('删除成功');
          fetchServiceAreas();
        } catch (error) {
          message.error('删除失败');
        }
      },
    });
  };

  // 打开地图模态框
  const handleOpenMap = () => {
    setIsMapModalVisible(true);
  };

  // 处理地图坐标选择完成
  const handleMapFinish = (coordinates: any[]) => {
    setSelectedCoordinates(coordinates);
    setIsMapModalVisible(false);
  };

  // 表格列定义
  const columns = [
    {
      title: '区域名称',
      dataIndex: 'areaName',
      key: 'areaName',
    },
    {
      title: '区域类型',
      dataIndex: 'areaType',
      key: 'areaType',
      render: (type: number) => (type === 1 ? '取车区域' : '还车区域'),
    },
    {
      title: '服务时间',
      key: 'serviceTime',
      render: (_: any, record: ServiceArea) => (
        <span>{record.serviceStartTime} - {record.serviceEndTime}</span>
      ),
    },
    {
      title: '提前预定时间',
      dataIndex: 'advanceHours',
      key: 'advanceHours',
      render: (hours: number) => `${hours}小时`,
    },
    {
      title: '送车上门',
      dataIndex: 'doorToDoorDelivery',
      key: 'doorToDoorDelivery',
      render: (delivery: number) => (delivery === 1 ? '是' : '否'),
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: ServiceArea) => (
        <Space size="middle">
          <Button 
            type="primary" 
            icon={<EditOutlined />} 
            onClick={() => handleEdit(record)}
            size="small"
          >
            编辑
          </Button>
          <Button 
            danger 
            icon={<DeleteOutlined />} 
            onClick={() => handleDelete(record.id!)}
            size="small"
          >
            删除
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div className="store-service-area-management">
      <Card 
        title={`[${storeName}] 服务范围管理`} 
        extra={
          <Button 
            type="primary" 
            icon={<PlusOutlined />} 
            onClick={() => {
              setEditingArea(null);
              form.resetFields();
              setSelectedCoordinates([]);
              setIsModalVisible(true);
            }}
          >
            新增服务范围
          </Button>
        }
      >
        <Table 
          dataSource={areas} 
          columns={columns} 
          loading={loading}
          rowKey="id"
          pagination={{ pageSize: 10 }}
        />
      </Card>

      {/* 服务范围表单模态框 */}
      <Modal
        title={editingArea?.id ? "编辑服务范围" : "新增服务范围"}
        visible={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          form.resetFields();
          setEditingArea(null);
          setSelectedCoordinates([]);
        }}
        onOk={() => form.submit()}
        width={800}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleFinish}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="areaName"
                label="区域名称"
                rules={[{ required: true, message: '请输入区域名称' }]}
              >
                <Input placeholder="请输入区域名称" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="areaType"
                label="区域类型"
                rules={[{ required: true, message: '请选择区域类型' }]}
              >
                <Radio.Group>
                  <Radio value={1}>取车区域</Radio>
                  <Radio value={2}>还车区域</Radio>
                </Radio.Group>
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            label="电子围栏"
            required
          >
            <Button onClick={handleOpenMap}>
              {selectedCoordinates.length > 0 ? '已设置电子围栏' : '设置电子围栏'}
            </Button>
            {selectedCoordinates.length > 0 && (
              <div style={{ marginTop: 8 }}>
                已选择 {selectedCoordinates.length} 个坐标点
              </div>
            )}
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="advanceHours"
                label="提前预定时间(小时)"
                rules={[{ required: true, message: '请输入提前预定时间' }]}
              >
                <InputNumber min={0} style={{ width: '100%' }} placeholder="请输入提前预定时间" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="serviceTime"
                label="服务时间"
                rules={[{ required: true, message: '请选择服务时间' }]}
              >
                <TimePicker.RangePicker style={{ width: '100%' }} format="HH:mm" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="doorToDoorDelivery"
                label="是否提供送车上门"
                rules={[{ required: true, message: '请选择是否提供送车上门' }]}
              >
                <Radio.Group>
                  <Radio value={1}>是</Radio>
                  <Radio value={0}>否</Radio>
                </Radio.Group>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="deliveryFee"
                label="送车上门费用(元)"
                rules={[{ required: true, message: '请输入送车上门费用' }]}
              >
                <InputNumber 
                  min={0} 
                  style={{ width: '100%' }} 
                  placeholder="请输入送车上门费用" 
                  formatter={value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                  parser={(value: string | undefined) => value ? parseFloat(value.replace(/,*/g, '')) : 0}
                />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            name="freePickupToStore"
            label="是否免费接至门店"
            rules={[{ required: true, message: '请选择是否免费接至门店' }]}
          >
            <Radio.Group>
              <Radio value={1}>是</Radio>
              <Radio value={0}>否</Radio>
            </Radio.Group>
          </Form.Item>
        </Form>
      </Modal>

      {/* 地图模态框 */}
      <ServiceAreaMapModal
        visible={isMapModalVisible}
        onCancel={() => setIsMapModalVisible(false)}
        onFinish={handleMapFinish}
        initialCoordinates={selectedCoordinates}
      />
    </div>
  );
};

export default StoreServiceAreaManagement;
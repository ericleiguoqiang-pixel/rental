import React, { useState, useEffect } from 'react';
import {
  Table,
  Card,
  Button,
  Space,
  Tag,
  Modal,
  Form,
  Input,
  Radio,
  message,
  Descriptions,
  Typography,
  Image,
  DatePicker,
  Select,
  Pagination
} from 'antd';
import {
  EyeOutlined,
  CheckOutlined,
  CloseOutlined,
  SearchOutlined,
  CarOutlined,
  AuditOutlined
} from '@ant-design/icons';
import MainLayout from '../../components/MainLayout';
import { vehicleAuditAPI } from '../../services/api';
import { usePaginationApi } from '../../hooks/useApi';

const { Title } = Typography;
const { TextArea } = Input;
const { Option } = Select;
const { RangePicker } = DatePicker;

interface VehicleApplication {
  id: string;
  merchantName: string;
  vehicleBrand: string;
  vehicleModel: string;
  vehicleYear: string;
  plateNumber: string;
  vin: string;
  engineNumber: string;
  color: string;
  seats: number;
  fuelType: string;
  transmission: string;
  mileage: number;
  dailyRate: number;
  vehicleImages: string[];
  registrationCert: string;
  insuranceCert: string;
  submitTime: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  auditRemark?: string;
  auditTime?: string;
  auditor?: string;
  
  // 后端实际返回的字段
  storeName?: string;
  licensePlate?: string;
  carModel?: {
    brand?: string;
    model?: string;
    year?: number;
  };
  auditStatus?: number;
  createdTime?: string;
}

const VehicleAudit: React.FC = () => {
  const { loading, data: vehicles, pagination, execute, reset } = usePaginationApi<VehicleApplication>();
  const [selectedVehicle, setSelectedVehicle] = useState<VehicleApplication | null>(null);
  const [detailVisible, setDetailVisible] = useState(false);
  const [auditVisible, setAuditVisible] = useState(false);
  const [auditForm] = Form.useForm();
  const [searchForm] = Form.useForm();

  useEffect(() => {
    loadVehicles();
  }, []);

  const loadVehicles = async (page = 1, pageSize = 10) => {
    await execute(vehicleAuditAPI.getPendingVehicles({
      current: page,
      size: pageSize
    }));
  };

  const handleSearch = (values: any) => {
    console.log('搜索条件：', values);
    // 实现搜索逻辑
    loadVehicles();
  };

  const handleViewDetail = async (record: VehicleApplication) => {
    try {
      // 调用后端API获取车辆详情
      const response: any = await vehicleAuditAPI.getVehicleDetail(record.id);
      if (response.code === 200) {
        // 将后端数据映射到前端期望的格式
        const vehicleData = {
          ...response.data,
          id: response.data.id,
          merchantName: response.data.storeName,
          vehicleBrand: response.data.carModel?.brand,
          vehicleModel: response.data.carModel?.model,
          vehicleYear: response.data.carModel?.year,
          plateNumber: response.data.licensePlate,
          submitTime: response.data.createdTime,
          status: response.data.auditStatus === 0 ? 'PENDING' : response.data.auditStatus === 1 ? 'APPROVED' : 'REJECTED'
        };
        setSelectedVehicle(vehicleData);
        setDetailVisible(true);
      } else {
        message.error(response.message || '获取车辆详情失败');
      }
    } catch (error: any) {
      message.error('获取车辆详情失败：' + (error.message || '网络错误'));
    }
  };

  const handleAudit = (record: VehicleApplication) => {
    setSelectedVehicle(record);
    auditForm.resetFields();
    setAuditVisible(true);
  };

  const handleAuditSubmit = async (values: any) => {
    if (!selectedVehicle) return;
    
    try {
      // 调用后端API进行审核
      const response: any = await vehicleAuditAPI.auditVehicle(selectedVehicle.id, values);
      
      if (response.code === 200 && response.data) {
        // 审核成功，重新加载数据
        await loadVehicles(pagination.current, pagination.pageSize);
        setAuditVisible(false);
        message.success('审核完成');
      } else {
        message.error(response.message || '审核失败');
      }
    } catch (error: any) {
      message.error('审核失败：' + (error.message || '网络错误'));
    }
  };

  const handleTableChange = (page: number, pageSize?: number) => {
    loadVehicles(page, pageSize || 10);
  };

  const getStatusTag = (status: string) => {
    switch (status) {
      case 'PENDING':
        return <Tag color="orange">待审核</Tag>;
      case 'APPROVED':
        return <Tag color="green">已通过</Tag>;
      case 'REJECTED':
        return <Tag color="red">已拒绝</Tag>;
      default:
        return <Tag>未知</Tag>;
    }
  };

  const columns = [
    {
      title: '商户名称',
      dataIndex: 'storeName',
      key: 'storeName',
      width: 180
    },
    {
      title: '车辆信息',
      key: 'vehicleInfo',
      width: 200,
      render: (record: VehicleApplication) => (
        <div>
          <div><strong>{record.carModel?.brand} {record.carModel?.model}</strong></div>
          <div style={{ color: '#666', fontSize: '12px' }}>{record.carModel?.year}年</div>
        </div>
      )
    },
    {
      title: '车牌号',
      dataIndex: 'licensePlate',
      key: 'licensePlate',
      width: 100
    },
    {
      title: '提交时间',
      dataIndex: 'createdTime',
      key: 'createdTime',
      width: 150
    },
    {
      title: '状态',
      dataIndex: 'auditStatus',
      key: 'auditStatus',
      width: 100,
      render: (auditStatus: number) => {
        switch (auditStatus) {
          case 0:
            return <Tag color="orange">待审核</Tag>;
          case 1:
            return <Tag color="green">已通过</Tag>;
          case 2:
            return <Tag color="red">已拒绝</Tag>;
          default:
            return <Tag>未知</Tag>;
        }
      }
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (record: VehicleApplication) => (
        <Space>
          <Button
            type="link"
            icon={<EyeOutlined />}
            onClick={() => handleViewDetail(record)}
          >
            查看
          </Button>
          {record.auditStatus === 0 && (
            <Button
              type="link"
              icon={<AuditOutlined />}
              onClick={() => handleAudit(record)}
            >
              审核
            </Button>
          )}
        </Space>
      )
    }
  ];

  return (
    <MainLayout title="车辆审核">
      <div>
        <Title level={4}>车辆审核管理</Title>
        
        {/* 搜索表单 */}
        <Card style={{ marginBottom: 16 }}>
          <Form
            form={searchForm}
            layout="inline"
            onFinish={handleSearch}
          >
            <Form.Item name="merchantName" label="商户名称">
              <Input placeholder="请输入商户名称" style={{ width: 200 }} />
            </Form.Item>
            <Form.Item name="vehicleBrand" label="车辆品牌">
              <Input placeholder="请输入车辆品牌" style={{ width: 150 }} />
            </Form.Item>
            <Form.Item name="plateNumber" label="车牌号">
              <Input placeholder="请输入车牌号" style={{ width: 150 }} />
            </Form.Item>
            <Form.Item name="status" label="状态">
              <Select placeholder="请选择状态" style={{ width: 120 }} allowClear>
                <Option value="PENDING">待审核</Option>
                <Option value="APPROVED">已通过</Option>
                <Option value="REJECTED">已拒绝</Option>
              </Select>
            </Form.Item>
            <Form.Item name="dateRange" label="提交时间">
              <RangePicker style={{ width: 280 }} />
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                  搜索
                </Button>
                <Button onClick={() => searchForm.resetFields()}>
                  重置
                </Button>
              </Space>
            </Form.Item>
          </Form>
        </Card>

        {/* 数据表格 */}
        <Card>
          <Table
            columns={columns}
            dataSource={vehicles || []}
            rowKey="id"
            loading={loading}
            pagination={false}
            scroll={{ x: 1200 }}
          />
          <div style={{ marginTop: 16, textAlign: 'right' }}>
            <Pagination
              current={pagination.current}
              pageSize={pagination.pageSize}
              total={pagination.total}
              onChange={handleTableChange}
              showSizeChanger
              showQuickJumper
              showTotal={(total) => `共 ${total} 条记录`}
            />
          </div>
        </Card>

        {/* 详情弹窗 */}
        <Modal
          title="车辆申请详情"
          open={detailVisible}
          onCancel={() => setDetailVisible(false)}
          width={1000}
          footer={[
            <Button key="close" onClick={() => setDetailVisible(false)}>
              关闭
            </Button>,
            selectedVehicle?.status === 'PENDING' && (
              <Button key="audit" type="primary" onClick={() => {
                setDetailVisible(false);
                handleAudit(selectedVehicle);
              }}>
                立即审核
              </Button>
            )
          ]}
        >
          {selectedVehicle && (
            <div>
              <Descriptions column={2} bordered style={{ marginBottom: 16 }}>
                <Descriptions.Item label="商户名称" span={2}>
                  {selectedVehicle?.storeName || ''}
                </Descriptions.Item>
                <Descriptions.Item label="车辆品牌">
                  {selectedVehicle?.carModel?.brand || ''}
                </Descriptions.Item>
                <Descriptions.Item label="车辆型号">
                  {selectedVehicle?.carModel?.model || ''}
                </Descriptions.Item>
                <Descriptions.Item label="年份">
                  {selectedVehicle?.carModel?.year || ''}年
                </Descriptions.Item>
                <Descriptions.Item label="车牌号">
                  {selectedVehicle?.licensePlate || ''}
                </Descriptions.Item>
                <Descriptions.Item label="VIN码" span={2}>
                  {selectedVehicle.vin || ''}
                </Descriptions.Item>
                <Descriptions.Item label="发动机号" span={2}>
                  {selectedVehicle.engineNumber || ''}
                </Descriptions.Item>
                <Descriptions.Item label="燃料类型">
                  {selectedVehicle.fuelType || ''}
                </Descriptions.Item>
                <Descriptions.Item label="变速器">
                  {selectedVehicle.transmission || ''}
                </Descriptions.Item>
                <Descriptions.Item label="里程数">
                  {(selectedVehicle.mileage || 0).toLocaleString()} km
                </Descriptions.Item>
                <Descriptions.Item label="日租金">
                  ¥{selectedVehicle.dailyRate || 0}/天
                </Descriptions.Item>
                <Descriptions.Item label="提交时间">
                  {selectedVehicle.submitTime || ''}
                </Descriptions.Item>
                <Descriptions.Item label="状态">
                  {getStatusTag(selectedVehicle.status)}
                </Descriptions.Item>
                {selectedVehicle.auditRemark && (
                  <>
                    <Descriptions.Item label="审核时间">
                      {selectedVehicle.auditTime || ''}
                    </Descriptions.Item>
                    <Descriptions.Item label="审核人">
                      {selectedVehicle.auditor || ''}
                    </Descriptions.Item>
                    <Descriptions.Item label="审核意见" span={2}>
                      {selectedVehicle.auditRemark || ''}
                    </Descriptions.Item>
                  </>
                )}
              </Descriptions>
              
              {/* 车辆图片 */}
              <div style={{ marginBottom: 16 }}>
                <Title level={5}>车辆图片</Title>
                <Image.PreviewGroup>
                  {(selectedVehicle.vehicleImages || []).map((image, index) => (
                    <Image
                      key={index}
                      width={150}
                      height={100}
                      src={image}
                      style={{ marginRight: 8, marginBottom: 8 }}
                    />
                  ))}
                </Image.PreviewGroup>
              </div>
              
              {/* 证件图片 */}
              <div>
                <Title level={5}>相关证件</Title>
                <Image.PreviewGroup>
                  <div style={{ marginBottom: 8 }}>行驶证：</div>
                  <Image
                    width={150}
                    height={100}
                    src={selectedVehicle.registrationCert || ''}
                    style={{ marginRight: 8 }}
                  />
                  <div style={{ marginBottom: 8, marginTop: 16 }}>保险单：</div>
                  <Image
                    width={150}
                    height={100}
                    src={selectedVehicle.insuranceCert || ''}
                  />
                </Image.PreviewGroup>
              </div>
            </div>
          )}
        </Modal>

        {/* 审核弹窗 */}
        <Modal
          title="车辆审核"
          open={auditVisible}
          onCancel={() => setAuditVisible(false)}
          onOk={() => auditForm.submit()}
          width={600}
        >
          <Form
            form={auditForm}
            layout="vertical"
            onFinish={handleAuditSubmit}
          >
            {selectedVehicle && (
              <div style={{ marginBottom: 16, padding: 16, background: '#f5f5f5', borderRadius: 6 }}>
                <p><strong>商户名称：</strong>{selectedVehicle.merchantName}</p>
                <p><strong>车辆信息：</strong>{selectedVehicle.vehicleBrand} {selectedVehicle.vehicleModel}</p>
                <p><strong>车牌号：</strong>{selectedVehicle.plateNumber}</p>
              </div>
            )}
            
            <Form.Item
              name="status"
              label="审核结果"
              rules={[{ required: true, message: '请选择审核结果' }]}
            >
              <Radio.Group>
                <Radio value="APPROVED">
                  <CheckOutlined style={{ color: '#52c41a' }} /> 通过
                </Radio>
                <Radio value="REJECTED">
                  <CloseOutlined style={{ color: '#ff4d4f' }} /> 拒绝
                </Radio>
              </Radio.Group>
            </Form.Item>
            
            <Form.Item
              name="reason"
              label="审核意见"
              rules={[{ required: true, message: '请输入审核意见' }]}
            >
              <TextArea
                rows={4}
                placeholder="请输入审核意见..."
              />
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </MainLayout>
  );
};

export default VehicleAudit;
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
  ShopOutlined,
  AuditOutlined,
  EnvironmentOutlined
} from '@ant-design/icons';
import MainLayout from '../../components/MainLayout';
import { storeAuditAPI } from '../../services/api';
import { usePaginationApi } from '../../hooks/useApi';

const { Title } = Typography;
const { TextArea } = Input;
const { Option } = Select;
const { RangePicker } = DatePicker;

interface StoreApplication {
  id: string;
  merchantName: string;
  storeManager: string;
  contactPhone: string;
  province: string;
  city: string;
  district: string;
  businessHours: string;
  storeArea: number;
  capacity: number;
  facilities: string[];
  storeImages: string[];
  businessLicense: string;
  rentalContract: string;
  submitTime: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  auditRemark?: string;
  auditTime?: string;
  auditor?: string;
  
  // 后端实际返回的字段
  storeName?: string;
  cityName?: string;
  address?: string;
  businessStartTime?: string;
  businessEndTime?: string;
  auditStatus?: number;
  createdTime?: string;
}

const StoreAudit: React.FC = () => {
  const { loading, data: stores, pagination, execute, reset } = usePaginationApi<StoreApplication>();
  const [selectedStore, setSelectedStore] = useState<StoreApplication | null>(null);
  const [detailVisible, setDetailVisible] = useState(false);
  const [auditVisible, setAuditVisible] = useState(false);
  const [auditForm] = Form.useForm();
  const [searchForm] = Form.useForm();

  useEffect(() => {
    loadStores();
  }, []);

  const loadStores = async (page = 1, pageSize = 10) => {
    await execute(storeAuditAPI.getPendingStores({
      current: page,
      size: pageSize
    }));
  };

  const handleSearch = (values: any) => {
    console.log('搜索条件：', values);
    // 实现搜索逻辑
    loadStores();
  };

  const handleViewDetail = async (record: StoreApplication) => {
    try {
      // 调用后端API获取门店详情
      const response: any = await storeAuditAPI.getStoreDetail(record.id);
      if (response.code === 200) {
        // 将后端数据映射到前端期望的格式
        const storeData = {
          ...response.data,
          id: response.data.id,
          merchantName: response.data.storeName,
          storeName: response.data.storeName,
          city: response.data.cityName,
          address: response.data.address,
          businessHours: `${response.data.businessStartTime || ''}-${response.data.businessEndTime || ''}`,
          submitTime: response.data.createdTime,
          status: response.data.auditStatus === 0 ? 'PENDING' : response.data.auditStatus === 1 ? 'APPROVED' : 'REJECTED'
        };
        setSelectedStore(storeData);
        setDetailVisible(true);
      } else {
        message.error(response.message || '获取门店详情失败');
      }
    } catch (error: any) {
      message.error('获取门店详情失败：' + (error.message || '网络错误'));
    }
  };

  const handleAudit = (record: StoreApplication) => {
    setSelectedStore(record);
    auditForm.resetFields();
    setAuditVisible(true);
  };

  const handleAuditSubmit = async (values: any) => {
    if (!selectedStore) return;
    
    try {
      // 调用后端API进行审核
      const response: any = await storeAuditAPI.auditStore(selectedStore.id, values);
      
      if (response.code === 200 && response.data) {
        // 审核成功，重新加载数据
        await loadStores(pagination.current, pagination.pageSize);
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
    loadStores(page, pageSize || 10);
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
      title: '门店名称',
      dataIndex: 'storeName',
      key: 'storeName',
      width: 180
    },
    {
      title: '所在城市',
      dataIndex: 'cityName',
      key: 'cityName',
      width: 100
    },
    {
      title: '详细地址',
      dataIndex: 'address',
      key: 'address',
      width: 200
    },
    {
      title: '营业时间',
      key: 'businessHours',
      width: 120,
      render: (record: StoreApplication) => {
        return `${record.businessStartTime || ''}-${record.businessEndTime || ''}`;
      }
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
      render: (record: StoreApplication) => (
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
    <MainLayout title="门店审核">
      <div>
        <Title level={4}>门店审核管理</Title>
        
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
            <Form.Item name="storeName" label="门店名称">
              <Input placeholder="请输入门店名称" style={{ width: 150 }} />
            </Form.Item>
            <Form.Item name="city" label="所在城市">
              <Input placeholder="请输入城市" style={{ width: 120 }} />
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
            dataSource={stores || []}
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
          title="门店申请详情"
          open={detailVisible}
          onCancel={() => setDetailVisible(false)}
          width={1000}
          footer={[
            <Button key="close" onClick={() => setDetailVisible(false)}>
              关闭
            </Button>,
            selectedStore?.status === 'PENDING' && (
              <Button key="audit" type="primary" onClick={() => {
                setDetailVisible(false);
                handleAudit(selectedStore);
              }}>
                立即审核
              </Button>
            )
          ]}
        >
          {selectedStore && (
            <div>
              <Descriptions column={2} bordered style={{ marginBottom: 16 }}>
                <Descriptions.Item label="商户名称" span={2}>
                  {selectedStore.merchantName || ''}
                </Descriptions.Item>
                <Descriptions.Item label="门店名称">
                  {selectedStore?.storeName || ''}
                </Descriptions.Item>
                <Descriptions.Item label="所在城市">
                  {selectedStore?.cityName || ''}
                </Descriptions.Item>
                <Descriptions.Item label="详细地址" span={2}>
                  {selectedStore?.address || ''}
                </Descriptions.Item>
                <Descriptions.Item label="营业时间">
                  {(selectedStore?.businessStartTime || '') + ' - ' + (selectedStore?.businessEndTime || '')}
                </Descriptions.Item>
                <Descriptions.Item label="门店面积">
                  {(selectedStore.storeArea || 0)}m²
                </Descriptions.Item>
                <Descriptions.Item label="容车量">
                  {(selectedStore.capacity || 0)}辆
                </Descriptions.Item>
                <Descriptions.Item label="所在地区" span={2}>
                  {(selectedStore.province || '') + ' ' + (selectedStore.city || '') + ' ' + (selectedStore.district || '')}
                </Descriptions.Item>
                <Descriptions.Item label="详细地址" span={2}>
                  {selectedStore.address || ''}
                </Descriptions.Item>
                <Descriptions.Item label="门店设施" span={2}>
                  {(selectedStore.facilities || []).map((facility, index) => (
                    <Tag key={index} color="blue" style={{ marginBottom: 4 }}>
                      {facility}
                    </Tag>
                  ))}
                </Descriptions.Item>
                <Descriptions.Item label="提交时间">
                  {selectedStore.submitTime || ''}
                </Descriptions.Item>
                <Descriptions.Item label="状态">
                  {getStatusTag(selectedStore.status)}
                </Descriptions.Item>
                {selectedStore.auditRemark && (
                  <>
                    <Descriptions.Item label="审核时间">
                      {selectedStore.auditTime || ''}
                    </Descriptions.Item>
                    <Descriptions.Item label="审核人">
                      {selectedStore.auditor || ''}
                    </Descriptions.Item>
                    <Descriptions.Item label="审核意见" span={2}>
                      {selectedStore.auditRemark || ''}
                    </Descriptions.Item>
                  </>
                )}
              </Descriptions>
              
              {/* 门店图片 */}
              <div style={{ marginBottom: 16 }}>
                <Title level={5}>门店图片</Title>
                <Image.PreviewGroup>
                  {(selectedStore.storeImages || []).map((image, index) => (
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
                  <div style={{ marginBottom: 8 }}>营业执照：</div>
                  <Image
                    width={150}
                    height={100}
                    src={selectedStore.businessLicense || ''}
                    style={{ marginRight: 8 }}
                  />
                  <div style={{ marginBottom: 8, marginTop: 16 }}>租赁合同：</div>
                  <Image
                    width={150}
                    height={100}
                    src={selectedStore.rentalContract || ''}
                  />
                </Image.PreviewGroup>
              </div>
            </div>
          )}
        </Modal>

        {/* 审核弹窗 */}
        <Modal
          title="门店审核"
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
            {selectedStore && (
              <div style={{ marginBottom: 16, padding: 16, background: '#f5f5f5', borderRadius: 6 }}>
                <p><strong>商户名称：</strong>{selectedStore.merchantName}</p>
                <p><strong>门店名称：</strong>{selectedStore.storeName}</p>
                <p><strong>门店经理：</strong>{selectedStore.storeManager}</p>
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

export default StoreAudit;
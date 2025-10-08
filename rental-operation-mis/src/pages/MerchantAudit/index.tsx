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
  Avatar,

  DatePicker,
  Select,
  Pagination
} from 'antd';
import {
  EyeOutlined,
  CheckOutlined,
  CloseOutlined,
  SearchOutlined,
  UserOutlined,
  AuditOutlined
} from '@ant-design/icons';
import MainLayout from '../../components/MainLayout';
import { merchantAuditAPI } from '../../services/api';
import { usePaginationApi } from '../../hooks/useApi';

const { Title } = Typography;
const { TextArea } = Input;
const { Option } = Select;
const { RangePicker } = DatePicker;

interface MerchantApplication {
  id: string;
  contactName: string;
  contactPhone: string;
  contactEmail: string;
  businessLicense: string;
  address: string;
  registeredCapital: string;
  businessScope: string;
  submitTime: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  auditRemark?: string;
  auditTime?: string;
  auditor?: string;
  
  // 后端实际返回的字段
  companyName?: string;
  applicationStatus?: number;
  createdTime?: string;
}

const MerchantAudit: React.FC = () => {
  const { loading, data: merchants, pagination, execute } = usePaginationApi<MerchantApplication>();
  const [selectedMerchant, setSelectedMerchant] = useState<MerchantApplication | null>(null);
  const [detailVisible, setDetailVisible] = useState(false);
  const [auditVisible, setAuditVisible] = useState(false);
  const [auditForm] = Form.useForm();
  const [searchForm] = Form.useForm();

  useEffect(() => {
    loadMerchants();
  }, []);

  const loadMerchants = async (page = 1, pageSize = 10) => {
    await execute(merchantAuditAPI.getPendingMerchants({
      current: page,
      size: pageSize
    }));
  };

  const handleSearch = (values: any) => {
    console.log('搜索条件：', values);
    // 实现搜索逻辑
    loadMerchants();
  };

  const handleViewDetail = async (record: MerchantApplication) => {
    try {
      // 调用后端API获取商户详情
      const response: any = await merchantAuditAPI.getMerchantDetail(record.id);
      if (response.code === 200) {
        // 将后端数据映射到前端期望的格式
        const merchantData = {
          ...response.data,
          id: response.data.id,
          companyName: response.data.companyName,
          submitTime: response.data.createdTime,
          status: response.data.applicationStatus === 0 ? 'PENDING' : response.data.applicationStatus === 1 ? 'APPROVED' : 'REJECTED'
        };
        setSelectedMerchant(merchantData);
        setDetailVisible(true);
      } else {
        message.error(response.message || '获取商户详情失败');
      }
    } catch (error: any) {
      message.error('获取商户详情失败：' + (error.message || '网络错误'));
    }
  };

  const handleAudit = (record: MerchantApplication) => {
    setSelectedMerchant(record);
    auditForm.resetFields();
    setAuditVisible(true);
  };

  const handleAuditSubmit = async (values: any) => {
    if (!selectedMerchant) return;
    
    try {
      // 调用后端API进行审核
      const response: any = await merchantAuditAPI.auditMerchant(selectedMerchant.id, values);
      
      if (response.code === 200 && response.data) {
        // 审核成功，重新加载数据
        await loadMerchants(pagination.current, pagination.pageSize);
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
    loadMerchants(page, pageSize || 10);
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
      title: '公司名称',
      dataIndex: 'companyName',
      key: 'companyName',
      width: 200
    },
    {
      title: '联系人',
      dataIndex: 'contactName',
      key: 'contactName',
      width: 100
    },
    {
      title: '联系电话',
      dataIndex: 'contactPhone',
      key: 'contactPhone',
      width: 120
    },
    {
      title: '提交时间',
      dataIndex: 'createdTime',
      key: 'createdTime',
      width: 150
    },
    {
      title: '状态',
      dataIndex: 'applicationStatus',
      key: 'applicationStatus',
      width: 100,
      render: (applicationStatus: number) => {
        switch (applicationStatus) {
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
      render: (record: MerchantApplication) => (
        <Space>
          <Button
            type="link"
            icon={<EyeOutlined />}
            onClick={() => handleViewDetail(record)}
          >
            查看
          </Button>
          {record.applicationStatus === 0 && (
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
    <MainLayout title="商户审核">
      <div>
        <Title level={4}>商户审核管理</Title>
        
        {/* 搜索表单 */}
        <Card style={{ marginBottom: 16 }}>
          <Form
            form={searchForm}
            layout="inline"
            onFinish={handleSearch}
          >
            <Form.Item name="companyName" label="公司名称">
              <Input placeholder="请输入公司名称" style={{ width: 200 }} />
            </Form.Item>
            <Form.Item name="contactName" label="联系人">
              <Input placeholder="请输入联系人" style={{ width: 150 }} />
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
            dataSource={merchants || []}
            rowKey="id"
            loading={loading}
            pagination={false}
            scroll={{ x: 1000 }}
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
          title="商户申请详情"
          open={detailVisible}
          onCancel={() => setDetailVisible(false)}
          width={800}
          footer={[
            <Button key="close" onClick={() => setDetailVisible(false)}>
              关闭
            </Button>,
            selectedMerchant?.status === 'PENDING' && (
              <Button key="audit" type="primary" onClick={() => {
                setDetailVisible(false);
                handleAudit(selectedMerchant);
              }}>
                立即审核
              </Button>
            )
          ]}
        >
          {selectedMerchant && (
            <Descriptions column={2} bordered>
              <Descriptions.Item label="公司名称" span={2}>
                {selectedMerchant.companyName}
              </Descriptions.Item>
              <Descriptions.Item label="联系人">
                <Avatar size="small" icon={<UserOutlined />} style={{ marginRight: 8 }} />
                {selectedMerchant.contactName}
              </Descriptions.Item>
              <Descriptions.Item label="联系电话">
                {selectedMerchant.contactPhone}
              </Descriptions.Item>
              <Descriptions.Item label="邮箱" span={2}>
                {selectedMerchant.contactEmail}
              </Descriptions.Item>
              <Descriptions.Item label="营业执照号">
                {selectedMerchant.businessLicense}
              </Descriptions.Item>
              <Descriptions.Item label="注册资本">
                {selectedMerchant.registeredCapital}
              </Descriptions.Item>
              <Descriptions.Item label="公司地址" span={2}>
                {selectedMerchant.address}
              </Descriptions.Item>
              <Descriptions.Item label="经营范围" span={2}>
                {selectedMerchant.businessScope}
              </Descriptions.Item>
              <Descriptions.Item label="提交时间">
                {selectedMerchant.submitTime}
              </Descriptions.Item>
              <Descriptions.Item label="状态">
                {getStatusTag(selectedMerchant.status)}
              </Descriptions.Item>
              {selectedMerchant.auditRemark && (
                <>
                  <Descriptions.Item label="审核时间">
                    {selectedMerchant.auditTime}
                  </Descriptions.Item>
                  <Descriptions.Item label="审核人">
                    {selectedMerchant.auditor}
                  </Descriptions.Item>
                  <Descriptions.Item label="审核意见" span={2}>
                    {selectedMerchant.auditRemark}
                  </Descriptions.Item>
                </>
              )}
            </Descriptions>
          )}
        </Modal>

        {/* 审核弹窗 */}
        <Modal
          title="商户审核"
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
            {selectedMerchant && (
              <div style={{ marginBottom: 16, padding: 16, background: '#f5f5f5', borderRadius: 6 }}>
                <p><strong>公司名称：</strong>{selectedMerchant.companyName}</p>
                <p><strong>联系人：</strong>{selectedMerchant.contactName}</p>
                <p><strong>联系电话：</strong>{selectedMerchant.contactPhone}</p>
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

export default MerchantAudit;
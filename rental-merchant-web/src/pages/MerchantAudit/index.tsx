import React, { useState } from 'react'
import { Card, Table, Button, Space, Tag, Modal, Form, Input, Select, message, Descriptions, Row, Col } from 'antd'
import { EyeOutlined, CheckOutlined, CloseOutlined } from '@ant-design/icons'
import MainLayout from '../../components/MainLayout'
import { useMerchantAudit } from '../../hooks/useMerchantAudit'

const { TextArea } = Input
const { Option } = Select

const MerchantAudit: React.FC = () => {
  const { merchants, loading, updateMerchantStatus } = useMerchantAudit()
  const [viewModalVisible, setViewModalVisible] = useState(false)
  const [auditModalVisible, setAuditModalVisible] = useState(false)
  const [selectedMerchant, setSelectedMerchant] = useState<any>(null)
  const [auditForm] = Form.useForm()

  const columns = [
    {
      title: '申请ID',
      dataIndex: 'id',
      key: 'id',
      width: 100,
    },
    {
      title: '企业名称',
      dataIndex: 'companyName',
      key: 'companyName',
    },
    {
      title: '法定代表人',
      dataIndex: 'legalPerson',
      key: 'legalPerson',
    },
    {
      title: '联系电话',
      dataIndex: 'contactPhone',
      key: 'contactPhone',
    },
    {
      title: '申请时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date: string) => new Date(date).toLocaleDateString(),
    },
    {
      title: '审核状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        const statusMap = {
          pending: { color: 'orange', text: '待审核' },
          approved: { color: 'green', text: '已通过' },
          rejected: { color: 'red', text: '已拒绝' },
          reviewing: { color: 'blue', text: '审核中' }
        }
        const statusInfo = statusMap[status as keyof typeof statusMap]
        return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_, record: any) => (
        <Space size="small">
          <Button
            type="link"
            icon={<EyeOutlined />}
            onClick={() => handleView(record)}
          >
            查看
          </Button>
          {record.status === 'pending' && (
            <>
              <Button
                type="link"
                icon={<CheckOutlined />}
                style={{ color: '#52c41a' }}
                onClick={() => handleAudit(record, 'approved')}
              >
                通过
              </Button>
              <Button
                type="link"
                icon={<CloseOutlined />}
                danger
                onClick={() => handleAudit(record, 'rejected')}
              >
                拒绝
              </Button>
            </>
          )}
        </Space>
      ),
    },
  ]

  const handleView = (record: any) => {
    setSelectedMerchant(record)
    setViewModalVisible(true)
  }

  const handleAudit = (record: any, status: string) => {
    setSelectedMerchant(record)
    auditForm.setFieldsValue({ status, reason: '' })
    setAuditModalVisible(true)
  }

  const handleAuditSubmit = async () => {
    try {
      const values = await auditForm.validateFields()
      await updateMerchantStatus(selectedMerchant.id, values.status, values.reason)
      setAuditModalVisible(false)
      setSelectedMerchant(null)
      auditForm.resetFields()
    } catch (error) {
      console.error('审核失败:', error)
    }
  }

  return (
    <MainLayout title="商户审核">
      <Card title="商户入住申请审核">
        <Table
          columns={columns}
          dataSource={merchants.map(item => ({ ...item, key: item.id }))}
          loading={loading}
          pagination={{
            total: merchants.length,
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条记录`,
          }}
        />
      </Card>

      {/* 查看详情模态框 */}
      <Modal
        title="商户详细信息"
        open={viewModalVisible}
        onCancel={() => setViewModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setViewModalVisible(false)}>
            关闭
          </Button>
        ]}
        width={800}
      >
        {selectedMerchant && (
          <div>
            <Descriptions title="基本信息" bordered column={2}>
              <Descriptions.Item label="企业名称" span={2}>
                {selectedMerchant.companyName}
              </Descriptions.Item>
              <Descriptions.Item label="统一社会信用代码" span={2}>
                {selectedMerchant.businessLicense}
              </Descriptions.Item>
              <Descriptions.Item label="法定代表人">
                {selectedMerchant.legalPerson}
              </Descriptions.Item>
              <Descriptions.Item label="联系电话">
                {selectedMerchant.contactPhone}
              </Descriptions.Item>
              <Descriptions.Item label="企业邮箱" span={2}>
                {selectedMerchant.contactEmail}
              </Descriptions.Item>
              <Descriptions.Item label="经营地址" span={2}>
                {selectedMerchant.businessAddress}
              </Descriptions.Item>
              <Descriptions.Item label="经营范围" span={2}>
                {selectedMerchant.businessScope}
              </Descriptions.Item>
              <Descriptions.Item label="注册资本">
                {selectedMerchant.registeredCapital}万元
              </Descriptions.Item>
              <Descriptions.Item label="成立日期">
                {selectedMerchant.establishedDate}
              </Descriptions.Item>
            </Descriptions>

            <Descriptions title="联系人信息" bordered column={2} style={{ marginTop: 16 }}>
              <Descriptions.Item label="联系人姓名">
                {selectedMerchant.contactPersonName}
              </Descriptions.Item>
              <Descriptions.Item label="联系人电话">
                {selectedMerchant.contactPersonPhone}
              </Descriptions.Item>
              <Descriptions.Item label="联系人邮箱" span={2}>
                {selectedMerchant.contactPersonEmail}
              </Descriptions.Item>
            </Descriptions>

            <Descriptions title="账户信息" bordered column={2} style={{ marginTop: 16 }}>
              <Descriptions.Item label="登录用户名">
                {selectedMerchant.username}
              </Descriptions.Item>
              <Descriptions.Item label="申请时间">
                {new Date(selectedMerchant.createdAt).toLocaleString()}
              </Descriptions.Item>
              <Descriptions.Item label="当前状态" span={2}>
                <Tag color={
                  selectedMerchant.status === 'pending' ? 'orange' :
                  selectedMerchant.status === 'approved' ? 'green' : 'red'
                }>
                  {selectedMerchant.status === 'pending' ? '待审核' :
                   selectedMerchant.status === 'approved' ? '已通过' : '已拒绝'}
                </Tag>
              </Descriptions.Item>
            </Descriptions>

            {selectedMerchant.auditReason && (
              <Descriptions title="审核意见" bordered style={{ marginTop: 16 }}>
                <Descriptions.Item label="审核意见" span={2}>
                  {selectedMerchant.auditReason}
                </Descriptions.Item>
              </Descriptions>
            )}
          </div>
        )}
      </Modal>

      {/* 审核模态框 */}
      <Modal
        title="商户审核"
        open={auditModalVisible}
        onOk={handleAuditSubmit}
        onCancel={() => {
          setAuditModalVisible(false)
          auditForm.resetFields()
        }}
        okText="确认"
        cancelText="取消"
      >
        <Form form={auditForm} layout="vertical">
          <Form.Item
            label="审核结果"
            name="status"
            rules={[{ required: true, message: '请选择审核结果' }]}
          >
            <Select placeholder="请选择审核结果">
              <Option value="approved">通过</Option>
              <Option value="rejected">拒绝</Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="审核意见"
            name="reason"
            rules={[{ required: true, message: '请输入审核意见' }]}
          >
            <TextArea
              rows={4}
              placeholder="请输入审核意见（通过原因或拒绝理由）"
            />
          </Form.Item>
        </Form>
      </Modal>
    </MainLayout>
  )
}

export default MerchantAudit
import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Table, 
  Button, 
  Space, 
  Tag, 
  Modal, 
  Form, 
  Input, 
  Select, 
  message, 
  Popconfirm,
  Row,
  Col
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import MainLayout from '../../components/MainLayout';
import { useEmployees } from '../../hooks/useEmployees';
import { storeAPI } from '../../services/api';

interface Employee {
  id: number;
  storeId?: number;
  employeeName: string;
  phone: string;
  username: string;
  status: number;
  statusDesc: string;
  roleType: number;
  roleTypeDesc: string;
  lastLoginTime?: string;
  lastLoginIp?: string;
  createdTime?: string;
}

interface Store {
  id: number;
  storeName: string;
}

const { Option } = Select;

const EmployeeManagement: React.FC = () => {
  const { employees, loading, pageInfo, getEmployees, createEmployee, updateEmployee, deleteEmployee, resetPassword } = useEmployees();
  const [modalVisible, setModalVisible] = useState(false);
  const [editingEmployee, setEditingEmployee] = useState<Employee | null>(null);
  const [form] = Form.useForm();
  const [stores, setStores] = useState<Store[]>([]);

  // 获取门店列表
  const fetchStores = async () => {
    try {
      const response: any = await storeAPI.getStores();
      const storeData = response.data?.records || response.data || [];
      setStores(storeData);
    } catch (error: any) {
      message.error('获取门店列表失败: ' + (error.message || '未知错误'));
      // 使用mock数据
      setStores([
        { id: 1, storeName: '总店' },
        { id: 2, storeName: '分店1' },
        { id: 3, storeName: '分店2' }
      ]);
    }
  };

  // 处理分页变化
  const handlePageChange = (page: number, pageSize?: number) => {
    getEmployees({
      current: page,
      size: pageSize
    });
  };

  // 处理删除员工
  const handleDelete = async (id: number) => {
    try {
      await deleteEmployee(id);
      getEmployees();
    } catch (error: any) {
      // 错误信息已在hook中处理
    }
  };

  // 处理添加员工
  const handleAdd = () => {
    setEditingEmployee(null);
    form.resetFields();
    setModalVisible(true);
  };

  // 处理编辑员工
  const handleEdit = (employee: Employee) => {
    setEditingEmployee(employee);
    form.setFieldsValue({
      ...employee,
      password: undefined // 编辑时不显示密码
    });
    setModalVisible(true);
  };

  // 处理重置密码
  const handleResetPassword = async (id: number) => {
    try {
      await resetPassword(id);
    } catch (error: any) {
      // 错误信息已在hook中处理
    }
  };

  // 处理表单提交
  const handleModalSubmit = async (values: any) => {
    try {
      if (editingEmployee) {
        // 更新员工
        await updateEmployee({
          ...values,
          id: editingEmployee.id
        });
      } else {
        // 创建员工
        await createEmployee(values);
      }
      setModalVisible(false);
      form.resetFields();
      getEmployees();
    } catch (error: any) {
      // 错误信息已在hook中处理
    }
  };

  // 处理表单取消
  const handleModalCancel = () => {
    setModalVisible(false);
    form.resetFields();
  };

  // 组件挂载时获取数据
  useEffect(() => {
    getEmployees();
    fetchStores();
  }, []);

  const columns = [
    {
      title: '员工姓名',
      dataIndex: 'employeeName',
      key: 'employeeName',
    },
    {
      title: '所属门店',
      dataIndex: 'storeId',
      key: 'storeId',
      render: (storeId: number) => {
        const store = stores.find(s => s.id === storeId);
        return store ? store.storeName : '未分配';
      },
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone',
    },
    {
      title: '角色类型',
      dataIndex: 'roleTypeDesc',
      key: 'roleTypeDesc',
    },
    {
      title: '状态',
      dataIndex: 'statusDesc',
      key: 'statusDesc',
      render: (text: string, record: Employee) => (
        <Tag color={record.status === 1 ? 'green' : 'red'}>
          {text}
        </Tag>
      ),
    },
    {
      title: '最后登录时间',
      dataIndex: 'lastLoginTime',
      key: 'lastLoginTime',
      render: (text: string) => text || '从未登录',
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: Employee) => (
        <Space size="middle">
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title="重置密码"
            description="确定要重置该员工的密码吗？"
            onConfirm={() => handleResetPassword(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link">重置密码</Button>
          </Popconfirm>
          <Popconfirm
            title="删除员工"
            description="确定要删除这个员工吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
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
    <MainLayout title="员工管理">
      <Card 
        title="员工列表" 
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            新增员工
          </Button>
        }
      >
        <Table 
          columns={columns} 
          dataSource={employees}
          loading={loading}
          rowKey="id"
          pagination={{
            current: pageInfo.current,
            pageSize: pageInfo.pageSize,
            total: pageInfo.total,
            onChange: handlePageChange,
            showSizeChanger: true,
            showQuickJumper: true,
          }}
        />
      </Card>

      <Modal
        title={editingEmployee ? '编辑员工' : '新增员工'}
        open={modalVisible}
        onCancel={handleModalCancel}
        onOk={() => form.submit()}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleModalSubmit}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="employeeName"
                label="员工姓名"
                rules={[{ required: true, message: '请输入员工姓名' }]}
              >
                <Input placeholder="请输入员工姓名" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="username"
                label="用户名"
                rules={[{ required: true, message: '请输入用户名' }]}
              >
                <Input placeholder="请输入用户名" />
              </Form.Item>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="phone"
                label="手机号"
                rules={[
                  { required: true, message: '请输入手机号' },
                  { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }
                ]}
              >
                <Input placeholder="请输入手机号" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="storeId"
                label="所属门店"
                rules={[{ required: true, message: '请选择所属门店' }]}
              >
                <Select placeholder="请选择所属门店">
                  {stores.map(store => (
                    <Option key={store.id} value={store.id}>
                      {store.storeName}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="roleType"
                label="角色类型"
                rules={[{ required: true, message: '请选择角色类型' }]}
              >
                <Select placeholder="请选择角色类型">
                  <Option value={2}>门店管理员</Option>
                  <Option value={3}>车辆管理员</Option>
                  <Option value={4}>订单管理员</Option>
                  <Option value={5}>普通员工</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="status"
                label="状态"
                rules={[{ required: true, message: '请选择状态' }]}
              >
                <Select placeholder="请选择状态">
                  <Option value={1}>启用</Option>
                  <Option value={0}>禁用</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          {!editingEmployee && (
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  name="password"
                  label="密码"
                  rules={[{ required: true, message: '请输入密码' }]}
                >
                  <Input.Password placeholder="请输入密码" />
                </Form.Item>
              </Col>
            </Row>
          )}
        </Form>
      </Modal>
    </MainLayout>
  );
};

export default EmployeeManagement;
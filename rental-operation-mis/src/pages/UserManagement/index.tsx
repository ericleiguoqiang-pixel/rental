import React, { useEffect, useState } from 'react';
import { Table, Card, Typography, Button, Space, Input, Select, message } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { userManagementAPI } from '../../services/api';
import MainLayout from '../../components/MainLayout';

const { Title } = Typography;
const { Option } = Select;

interface User {
  id: number;
  phone: string;
  nickname: string;
  avatar: string;
  status: number;
  createdAt: string;
  updatedAt: string;
}

const UserManagement: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });
  
  const [searchParams, setSearchParams] = useState({
    phone: '',
    nickname: '',
    status: undefined as number | undefined,
  });

  const columns: ColumnsType<User> = [
    {
      title: '用户ID',
      dataIndex: 'id',
      key: 'id',
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone',
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
      key: 'nickname',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: number) => (status === 0 ? '正常' : '禁用'),
    },
    {
      title: '注册时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
    },
    {
      title: '最后更新',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <a onClick={() => handleViewDetail(record.id)}>查看详情</a>
          <a onClick={() => handleToggleStatus(record)}>
            {record.status === 0 ? '禁用' : '启用'}
          </a>
        </Space>
      ),
    },
  ];

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const params = {
        current: pagination.current,
        size: pagination.pageSize,
        phone: searchParams.phone || undefined,
        nickname: searchParams.nickname || undefined,
        status: searchParams.status,
      };
      
      const response = await userManagementAPI.getUserList(params);
      
      if (response && response.code === 200 && response.data) {
        setUsers(response.data.records || []);
        setPagination({
          ...pagination,
          total: response.data.total || 0,
        });
      } else {
        message.error('获取用户列表失败');
        setUsers([]);
      }
    } catch (error: any) {
      console.error('获取用户列表失败:', error);
      message.error('获取用户列表失败：' + (error.message || '未知错误'));
      setUsers([]);
    } finally {
      setLoading(false);
    }
  };

  const handleViewDetail = async (id: number) => {
    try {
      const response = await userManagementAPI.getUserDetail(id);
      if (response && response.code === 200 && response.data) {
        message.info(`用户ID: ${response.data.id}, 手机号: ${response.data.phone}`);
      } else {
        message.error('获取用户详情失败');
      }
    } catch (error: any) {
      console.error('获取用户详情失败:', error);
      message.error('获取用户详情失败：' + (error.message || '未知错误'));
    }
  };

  const handleToggleStatus = async (user: User) => {
    try {
      const newStatus = user.status === 0 ? 1 : 0;
      const response = await userManagementAPI.updateUserStatus(user.id, newStatus);
      
      if (response && response.code === 200) {
        message.success(`${newStatus === 0 ? '启用' : '禁用'}用户成功`);
        // 重新加载数据
        fetchUsers();
      } else {
        message.error(`${newStatus === 0 ? '启用' : '禁用'}用户失败`);
      }
    } catch (error: any) {
      console.error('更新用户状态失败:', error);
      message.error(`${user.status === 0 ? '禁用' : '启用'}用户失败：` + (error.message || '未知错误'));
    }
  };

  const handleSearch = () => {
    setPagination({
      ...pagination,
      current: 1,
    });
  };

  const handleReset = () => {
    setSearchParams({
      phone: '',
      nickname: '',
      status: undefined,
    });
    setPagination({
      ...pagination,
      current: 1,
    });
  };

  useEffect(() => {
    fetchUsers();
  }, [pagination.current, pagination.pageSize]);

  return (
    <MainLayout title="用户管理">
      <div>
        <Card style={{ marginBottom: 16 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Title level={4} style={{ margin: 0 }}>用户管理</Title>
          </div>
        </Card>

        <Card>
          <div style={{ marginBottom: 16 }}>
            <Space wrap>
              <Input 
                placeholder="手机号" 
                style={{ width: 200 }} 
                value={searchParams.phone}
                onChange={(e) => setSearchParams({...searchParams, phone: e.target.value})}
              />
              <Input 
                placeholder="昵称" 
                style={{ width: 200 }} 
                value={searchParams.nickname}
                onChange={(e) => setSearchParams({...searchParams, nickname: e.target.value})}
              />
              <Select
                placeholder="状态"
                style={{ width: 120 }}
                allowClear
                value={searchParams.status}
                onChange={(value) => setSearchParams({...searchParams, status: value})}
              >
                <Option value={0}>正常</Option>
                <Option value={1}>禁用</Option>
              </Select>
              <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>搜索</Button>
              <Button onClick={handleReset}>重置</Button>
            </Space>
          </div>

          <Table
            columns={columns}
            dataSource={users}
            loading={loading}
            pagination={{
              ...pagination,
              onChange: (page, pageSize) => {
                setPagination({
                  ...pagination,
                  current: page,
                  pageSize: pageSize || pagination.pageSize,
                });
              },
            }}
            rowKey="id"
          />
        </Card>
      </div>
    </MainLayout>
  );
};

export default UserManagement;
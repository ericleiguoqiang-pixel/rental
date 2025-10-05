import React, { useEffect, useState } from 'react';
import { Table, Card, Typography, Button, Space, Input, DatePicker } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';

const { Title } = Typography;
const { RangePicker } = DatePicker;

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
          <a>查看详情</a>
          <a>禁用</a>
        </Space>
      ),
    },
  ];

  const fetchUsers = async () => {
    setLoading(true);
    try {
      // 这里应该调用实际的API获取用户列表
      // 暂时使用模拟数据
      const mockUsers: User[] = Array.from({ length: 20 }, (_, index) => ({
        id: 10001 + index,
        phone: `138****${String(1000 + index).padStart(4, '0')}`,
        nickname: `用户${index + 1}`,
        avatar: '',
        status: index % 2 === 0 ? 0 : 1,
        createdAt: '2023-01-01 12:00:00',
        updatedAt: '2023-01-01 12:00:00',
      }));
      
      setUsers(mockUsers);
      setPagination({
        ...pagination,
        total: mockUsers.length,
      });
    } catch (error) {
      console.error('获取用户列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Title level={4} style={{ margin: 0 }}>用户管理</Title>
        </div>
      </Card>

      <Card>
        <div style={{ marginBottom: 16 }}>
          <Space wrap>
            <Input placeholder="手机号" style={{ width: 200 }} />
            <Input placeholder="昵称" style={{ width: 200 }} />
            <RangePicker />
            <Button type="primary" icon={<SearchOutlined />}>搜索</Button>
            <Button>重置</Button>
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
  );
};

export default UserManagement;
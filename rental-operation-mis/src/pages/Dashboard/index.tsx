import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Table, Typography, Spin, message } from 'antd';
import { 
  UserOutlined, 
  CarOutlined, 
  ShopOutlined, 
  AuditOutlined,
  BarChartOutlined,
  ClockCircleOutlined 
} from '@ant-design/icons';
import MainLayout from '../../components/MainLayout';
import { operationDashboardAPI } from '../../services/api';

const { Title } = Typography;

const Dashboard: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [stats, setStats] = useState({
    pendingMerchants: 0,
    pendingVehicles: 0,
    pendingStores: 0,
    totalAudits: 0
  });

  // 模拟待处理事项数据
  const pendingItems = [
    {
      key: '1',
      type: '商户审核',
      company: '优质租车服务有限公司',
      applicant: '张三',
      submitTime: '2024-10-01 09:30:00',
      status: '待审核',
    },
    {
      key: '2',
      type: '车辆审核',
      company: '城市出行租赁',
      vehicle: '宝马3系 京A12345',
      submitTime: '2024-10-01 10:15:00',
      status: '待审核',
    },
    {
      key: '3',
      type: '门店审核',
      company: '便民租车',
      store: '朝阳区总店',
      submitTime: '2024-10-01 11:00:00',
      status: '待审核',
    },
    {
      key: '4',
      type: '商户审核',
      company: '快捷汽车租赁',
      applicant: '李四',
      submitTime: '2024-10-01 14:20:00',
      status: '待审核',
    },
  ];

  const columns = [
    {
      title: '审核类型',
      dataIndex: 'type',
      key: 'type',
      render: (type: string) => {
        let icon;
        switch (type) {
          case '商户审核':
            icon = <UserOutlined style={{ marginRight: 8, color: '#1890ff' }} />;
            break;
          case '车辆审核':
            icon = <CarOutlined style={{ marginRight: 8, color: '#52c41a' }} />;
            break;
          case '门店审核':
            icon = <ShopOutlined style={{ marginRight: 8, color: '#fa8c16' }} />;
            break;
          default:
            icon = <AuditOutlined style={{ marginRight: 8 }} />;
        }
        return (
          <span>
            {icon}
            {type}
          </span>
        );
      },
    },
    {
      title: '商户名称',
      dataIndex: 'company',
      key: 'company',
    },
    {
      title: '详细信息',
      key: 'details',
      render: (record: any) => {
        if (record.applicant) return `申请人：${record.applicant}`;
        if (record.vehicle) return `车辆：${record.vehicle}`;
        if (record.store) return `门店：${record.store}`;
        return '-';
      },
    },
    {
      title: '提交时间',
      dataIndex: 'submitTime',
      key: 'submitTime',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => (
        <span style={{ color: '#fa8c16' }}>
          <ClockCircleOutlined style={{ marginRight: 4 }} />
          {status}
        </span>
      ),
    },
  ];

  useEffect(() => {
    const loadDashboardData = async () => {
      setLoading(true);
      try {
        // 调用实际的API获取运营概览数据
        const response = await operationDashboardAPI.getOverviewStats();
        if (response && response.code === 200 && response.data) {
          setStats(response.data);
        } else {
          message.error('获取运营数据失败');
        }
      } catch (error: any) {
        message.error('加载数据失败：' + (error.message || '未知错误'));
        console.error('加载运营数据失败:', error);
      } finally {
        setLoading(false);
      }
    };

    loadDashboardData();
  }, []);

  return (
    <MainLayout title="运营概览">
      <div style={{ padding: '0 0 24px 0' }}>
        <Title level={3} style={{ marginBottom: 24 }}>
          <BarChartOutlined style={{ marginRight: 8, color: '#1890ff' }} />
          运营数据概览
        </Title>
        
        <Spin spinning={loading}>
          {/* 统计卡片 */}
          <Row gutter={[24, 24]} style={{ marginBottom: 24 }}>
            <Col xs={24} sm={12} lg={6}>
              <Card>
                <Statistic
                  title="待审核商户"
                  value={stats.pendingMerchants}
                  prefix={<UserOutlined style={{ color: '#1890ff' }} />}
                  suffix="个"
                  valueStyle={{ color: '#1890ff' }}
                />
              </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
              <Card>
                <Statistic
                  title="待审核车辆"
                  value={stats.pendingVehicles}
                  prefix={<CarOutlined style={{ color: '#52c41a' }} />}
                  suffix="辆"
                  valueStyle={{ color: '#52c41a' }}
                />
              </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
              <Card>
                <Statistic
                  title="待审核门店"
                  value={stats.pendingStores}
                  prefix={<ShopOutlined style={{ color: '#fa8c16' }} />}
                  suffix="家"
                  valueStyle={{ color: '#fa8c16' }}
                />
              </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
              <Card>
                <Statistic
                  title="总审核数"
                  value={stats.totalAudits}
                  prefix={<AuditOutlined style={{ color: '#722ed1' }} />}
                  suffix="项"
                  valueStyle={{ color: '#722ed1' }}
                />
              </Card>
            </Col>
          </Row>

          {/* 待处理事项表格 */}
          <Card 
            title={
              <span>
                <ClockCircleOutlined style={{ marginRight: 8, color: '#fa8c16' }} />
                待处理事项
              </span>
            }
          >
            <Table
              columns={columns}
              dataSource={pendingItems}
              pagination={{ pageSize: 10, showSizeChanger: true }}
              scroll={{ x: 800 }}
            />
          </Card>
        </Spin>
      </div>
    </MainLayout>
  );
};

export default Dashboard;
import React, { useState } from 'react';
import { Layout, Menu, Avatar, Dropdown, Button, Typography } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  DashboardOutlined,
  ShopOutlined,
  CarOutlined,
  FileTextOutlined,
  UserOutlined,
  LogoutOutlined,
  SettingOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons';
import type { MenuProps } from 'antd';

const { Header, Sider, Content } = Layout;
const { Title } = Typography;

interface MainLayoutProps {
  children: React.ReactNode;
  title?: string;
}

const MainLayout: React.FC<MainLayoutProps> = ({ children, title = '仪表盘' }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);

  // 根据当前路径设置选中的菜单项
  const getSelectedKeys = () => {
    const path = location.pathname;
    if (path === '/' || path === '/dashboard') return ['dashboard'];
    if (path === '/stores') return ['stores'];
    if (path === '/vehicles') return ['vehicles'];
    if (path === '/orders') return ['orders'];
    return ['dashboard'];
  };

  const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
    switch (key) {
      case 'dashboard':
        navigate('/');
        break;
      case 'stores':
        navigate('/stores');
        break;
      case 'vehicles':
        navigate('/vehicles');
        break;
      case 'orders':
        navigate('/orders');
        break;
      default:
        break;
    }
  };

  const menuItems: MenuProps['items'] = [
    {
      key: 'dashboard',
      icon: <DashboardOutlined />,
      label: '仪表盘',
    },
    {
      key: 'stores',
      icon: <ShopOutlined />,
      label: '门店管理',
    },
    {
      key: 'vehicles',
      icon: <CarOutlined />,
      label: '车辆管理',
    },
    {
      key: 'orders',
      icon: <FileTextOutlined />,
      label: '订单管理',
    },
  ];

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  const userMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人信息',
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '设置',
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider 
        trigger={null} 
        collapsible 
        collapsed={collapsed}
        style={{
          background: '#fff',
          boxShadow: '2px 0 8px 0 rgba(29,35,41,.05)',
        }}
      >
        <div style={{ 
          height: 64, 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'center',
          borderBottom: '1px solid #f0f0f0'
        }}>
          <Title level={4} style={{ margin: 0, color: '#1890ff' }}>
            {collapsed ? '租赁' : '租赁管理系统'}
          </Title>
        </div>
        <Menu
          mode="inline"
          selectedKeys={getSelectedKeys()}
          onClick={handleMenuClick}
          items={menuItems}
          style={{ 
            borderRight: 0,
            marginTop: 16
          }}
        />
      </Sider>
      
      <Layout>
        <Header style={{ 
          padding: '0 16px', 
          background: '#fff',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          borderBottom: '1px solid #f0f0f0'
        }}>
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <Button
              type="text"
              icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
              onClick={() => setCollapsed(!collapsed)}
              style={{ marginRight: 16 }}
            />
            <Title level={4} style={{ margin: 0 }}>
              {title}
            </Title>
          </div>
          
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <div style={{ 
              display: 'flex', 
              alignItems: 'center', 
              cursor: 'pointer',
              padding: '8px 12px',
              borderRadius: 6,
              transition: 'background-color 0.3s'
            }}>
              <Avatar size="small" icon={<UserOutlined />} />
              <span style={{ marginLeft: 8 }}>管理员</span>
            </div>
          </Dropdown>
        </Header>
        
        <Content style={{ 
          margin: '16px',
          padding: '24px',
          background: '#fff',
          borderRadius: 6,
          boxShadow: '0 1px 3px 0 rgba(0,0,0,0.1)',
          overflow: 'auto'
        }}>
          {children}
        </Content>
      </Layout>
    </Layout>
  );
};

export default MainLayout;
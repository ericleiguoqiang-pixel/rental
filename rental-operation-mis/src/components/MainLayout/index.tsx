import React, { useState, useEffect } from 'react';
import { Layout, Menu, Avatar, Dropdown, Button, Typography, Space } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  DashboardOutlined,
  AuditOutlined,
  CarOutlined,
  ShopOutlined,
  UserOutlined,
  LogoutOutlined,
  SettingOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  SafetyCertificateOutlined,
  DatabaseOutlined,
} from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { useAuthStore } from '../../hooks/useAuth';

const { Header, Sider, Content } = Layout;
const { Title } = Typography;

interface MainLayoutProps {
  children: React.ReactNode;
  title?: string;
}

const MainLayout: React.FC<MainLayoutProps> = ({ children, title = '运营概览' }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);
  const [openKeys, setOpenKeys] = useState<string[]>([]); // 添加openKeys状态
  const { user, logout } = useAuthStore();

  // 根据当前路径设置选中的菜单项
  const getSelectedKeys = () => {
    const path = location.pathname;
    if (path === '/' || path === '/dashboard') return ['dashboard'];
    if (path === '/merchant-audit') return ['merchant-audit'];
    if (path === '/vehicle-audit') return ['vehicle-audit'];
    if (path === '/store-audit') return ['store-audit'];
    if (path === '/car-models') return ['car-models'];
    return ['dashboard'];
  };

  // 根据当前路径设置展开的菜单项
  const getDefaultOpenKeys = () => {
    const path = location.pathname;
    if (path === '/merchant-audit' || path === '/vehicle-audit' || path === '/store-audit') {
      return ['audit'];
    }
    return [];
  };

  // 初始化展开的菜单项
  useEffect(() => {
    setOpenKeys(getDefaultOpenKeys());
  }, []);

  const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
    switch (key) {
      case 'dashboard':
        navigate('/dashboard');
        break;
      case 'merchant-audit':
        navigate('/merchant-audit');
        break;
      case 'vehicle-audit':
        navigate('/vehicle-audit');
        break;
      case 'store-audit':
        navigate('/store-audit');
        break;
      case 'car-models':
        navigate('/car-models');
        break;
      default:
        break;
    }
  };

  // 处理菜单展开/收起事件
  const onOpenChange = (keys: string[]) => {
    // 保持"audit"菜单项在相关子菜单被选中时始终展开
    const path = location.pathname;
    if (path === '/merchant-audit' || path === '/vehicle-audit' || path === '/store-audit') {
      setOpenKeys(['audit', ...keys.filter(key => key !== 'audit')]);
    } else {
      setOpenKeys(keys);
    }
  };

  const menuItems: MenuProps['items'] = [
    {
      key: 'dashboard',
      icon: <DashboardOutlined />,
      label: '运营概览',
    },
    {
      key: 'audit',
      icon: <AuditOutlined />,
      label: '审核管理',
      children: [
        {
          key: 'merchant-audit',
          icon: <UserOutlined />,
          label: '商户审核',
        },
        {
          key: 'vehicle-audit',
          icon: <CarOutlined />,
          label: '车辆审核',
        },
        {
          key: 'store-audit',
          icon: <ShopOutlined />,
          label: '门店审核',
        },
      ],
    },
    {
      key: 'car-models',
      icon: <DatabaseOutlined />,
      label: '车型管理',
    },
  ];

  const handleLogout = () => {
    logout();
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
      label: '系统设置',
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
          borderBottom: '1px solid #f0f0f0',
          padding: '0 16px'
        }}>
          <SafetyCertificateOutlined 
            style={{ 
              fontSize: collapsed ? 24 : 20, 
              color: '#1890ff', 
              marginRight: collapsed ? 0 : 8 
            }} 
          />
          {!collapsed && (
            <Title level={4} style={{ margin: 0, color: '#1890ff' }}>
              运营MIS
            </Title>
          )}
        </div>
        <Menu
          mode="inline"
          selectedKeys={getSelectedKeys()}
          openKeys={openKeys}
          onOpenChange={onOpenChange}
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
              <Space style={{ marginLeft: 8 }}>
                <span>{user?.name || '运营管理员'}</span>
              </Space>
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
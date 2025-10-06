import React from 'react';
import { Dropdown, Menu, Avatar, message } from 'antd';
import { UserOutlined, LogoutOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { getUserInfo, logout } from '../services/authService';

const UserInfo: React.FC = () => {
  const navigate = useNavigate();
  const userInfo = getUserInfo();

  const handleMenuClick = async (e: any) => {
    if (e.key === 'logout') {
      try {
        const result = await logout();
        if (result.success) {
          message.success('登出成功');
          navigate('/login');
        } else {
          message.error(result.message || '登出失败');
        }
      } catch (error) {
        message.error('登出时发生错误');
        navigate('/login');
      }
    }
  };

  const menu = (
    <Menu onClick={handleMenuClick}>
      <Menu.Item key="profile" icon={<UserOutlined />}>
        个人中心
      </Menu.Item>
      <Menu.Item key="logout" icon={<LogoutOutlined />}>
        退出登录
      </Menu.Item>
    </Menu>
  );

  return (
    <Dropdown overlay={menu} placement="bottomRight">
      <Avatar 
        style={{ backgroundColor: '#87d068' }} 
        icon={<UserOutlined />} 
        size="large"
      >
        {userInfo?.employeeName?.substring(0, 1) || 'U'}
      </Avatar>
    </Dropdown>
  );
};

export default UserInfo;
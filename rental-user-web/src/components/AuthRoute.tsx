import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { isAuthenticated } from '../services/authService';

interface AuthRouteProps {
  children: React.ReactNode;
}

const AuthRoute: React.FC<AuthRouteProps> = ({ children }) => {
  const navigate = useNavigate();

  useEffect(() => {
    // 检查用户是否已认证
    if (!isAuthenticated()) {
      // 未认证，重定向到登录页面
      navigate('/login');
    }
  }, [navigate]);

  // 如果已认证，渲染子组件
  return isAuthenticated() ? <>{children}</> : null;
};

export default AuthRoute;
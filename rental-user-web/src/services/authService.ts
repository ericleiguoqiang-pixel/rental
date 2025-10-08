import request from './request';

// 登录接口
export const login = async (phone: string, code: string, captchaKey: string) => {
  try {
    const response = await request.post('/user/login', { 
      phone, 
      code
      // 移除了captcha和captchaKey字段，因为不再使用图片验证码
    });
    return response.data;
  } catch (error: any) {
    console.error('Login error:', error);
    return { 
      success: false, 
      message: error.response?.data?.message || '登录失败',
      code: error.response?.data?.code || 500
    };
  }
};

// 刷新令牌
export const refreshToken = async (refreshToken: string) => {
  try {
    const response = await request.post('/auth/refresh', null, {
      params: { refreshToken }
    });
    return response.data;
  } catch (error: any) {
    console.error('Refresh token error:', error);
    return { 
      success: false, 
      message: error.response?.data?.message || '刷新令牌失败',
      code: error.response?.data?.code || 500
    };
  }
};

// 登出
export const logout = async () => {
  try {
    const token = localStorage.getItem('token');
    if (token) {
      await request.post('/auth/logout', null, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
    }
    // 清除本地存储
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    return { success: true };
  } catch (error: any) {
    console.error('Logout error:', error);
    // 即使服务端登出失败，也要清除本地数据
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    return { 
      success: true, // 仍然认为登出成功
      message: error.response?.data?.message || '登出时发生错误'
    };
  }
};

// 验证令牌
export const verifyToken = async () => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      return { success: false, message: '未找到令牌' };
    }
    
    const response = await request.get('/auth/verify', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    return response.data;
  } catch (error: any) {
    console.error('Verify token error:', error);
    return { 
      success: false, 
      message: error.response?.data?.message || '令牌验证失败',
      code: error.response?.data?.code || 500
    };
  }
};

// 检查是否已登录
export const isAuthenticated = () => {
  const token = localStorage.getItem('token');
  return !!token;
};

// 获取存储的用户信息
export const getUserInfo = () => {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    try {
      return JSON.parse(userStr);
    } catch (e) {
      return null;
    }
  }
  return null;
};

// 设置认证令牌
export const setAuthTokens = (accessToken: string, refreshToken: string, userInfo: any) => {
  localStorage.setItem('token', accessToken);
  localStorage.setItem('refreshToken', refreshToken);
  localStorage.setItem('user', JSON.stringify(userInfo));
};

// 获取认证令牌
export const getAuthToken = () => {
  return localStorage.getItem('token');
};
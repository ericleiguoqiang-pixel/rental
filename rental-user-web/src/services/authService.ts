import request from './request';

// 登录接口
export const login = async (phone: string, code: string) => {
  try {
    const response = await request.post('/api/user/login', { phone, code });
    return response.data;
  } catch (error) {
    console.error('Login error:', error);
    return { success: false, message: '登录失败' };
  }
};

// 获取用户信息
export const getUserInfo = async () => {
  try {
    const response = await request.get('/api/user/info');
    return response.data;
  } catch (error) {
    console.error('Get user info error:', error);
    return null;
  }
};

// 登出
export const logout = async () => {
  try {
    await request.post('/api/user/logout');
    localStorage.removeItem('user');
    return { success: true };
  } catch (error) {
    console.error('Logout error:', error);
    return { success: false, message: '登出失败' };
  }
};
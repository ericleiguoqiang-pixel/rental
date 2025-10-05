import axios from 'axios';

// 创建axios实例
const request = axios.create({
  baseURL: '/api', // API基础路径
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 在发送请求之前做些什么
    const user = localStorage.getItem('user');
    if (user) {
      const userData = JSON.parse(user);
      if (config.headers) {
        config.headers['Authorization'] = `Bearer ${userData.token}`;
      }
    }
    return config;
  },
  (error) => {
    // 对请求错误做些什么
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 对响应数据做点什么
    return response;
  },
  (error) => {
    // 对响应错误做点什么
    console.error('Response error:', error);
    
    if (error.response?.status === 401) {
      // token过期或未授权，清除用户信息并跳转到登录页
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    
    return Promise.reject(error);
  }
);

export default request;
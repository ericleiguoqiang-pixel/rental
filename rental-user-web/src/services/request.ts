import axios from 'axios';
import { getAuthToken } from './authService';

// 创建axios实例
const request = axios.create({
  baseURL: '/api', // 所有请求都以/api开头
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 添加认证token
    const token = getAuthToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 可以在这里统一处理响应数据
    return response;
  },
  (error) => {
    // 统一处理错误
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，清除本地存储并跳转到登录页
          localStorage.removeItem('token');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('user');
          // 如果不是登录页面，跳转到登录页
          if (window.location.pathname !== '/login') {
            window.location.href = '/login';
          }
          break;
        case 403:
          // 禁止访问
          break;
        case 404:
          // 页面不存在
          break;
        case 500:
          // 服务器错误
          break;
        default:
          // 其他错误
      }
    }
    return Promise.reject(error);
  }
);

export default request;
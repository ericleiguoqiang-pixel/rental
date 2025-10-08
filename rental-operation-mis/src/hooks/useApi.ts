import { useState } from 'react';

// 定义API响应结构
interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

interface PageResponse<T> {
  records: T[];
  total: number;
  pageNum: number;
  pageSize: number;
  pages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

// 使用泛型来处理不同类型的API响应
export const useApi = <T,>() => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<T | null>(null);
  const [error, setError] = useState<string | null>(null);

  const execute = async (apiCall: Promise<any>): Promise<ApiResponse<T> | null> => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await apiCall;
      setData(response.data || null);
      return response;
    } catch (err: any) {
      const errorMessage = err.message || '网络错误';
      setError(errorMessage);
      return null;
    } finally {
      setLoading(false);
    }
  };

  return { loading, data, error, execute };
};

// 专门用于分页数据的Hook
export const usePaginationApi = <T,>() => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<T[]>([]);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });
  const [error, setError] = useState<string | null>(null);

  const execute = async (apiCall: Promise<any>): Promise<ApiResponse<PageResponse<T>> | null> => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await apiCall;
      if (response.code === 200) {
        setData(response.data.records || []);
        setPagination({
          current: response.data.pageNum || 1,
          pageSize: response.data.pageSize || 10,
          total: response.data.total || 0,
        });
      }
      return response;
    } catch (err: any) {
      const errorMessage = err.message || '网络错误';
      setError(errorMessage);
      setData([]);
      return null;
    } finally {
      setLoading(false);
    }
  };

  const reset = () => {
    setData([]);
    setPagination({
      current: 1,
      pageSize: 10,
      total: 0,
    });
    setError(null);
  };

  return { loading, data, pagination, error, execute, reset };
};
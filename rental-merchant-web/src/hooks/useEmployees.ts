import { useState } from 'react';
import { message } from 'antd';
import { employeeAPI } from '../services/api';
import type { Employee, EmployeePageRequest } from '../pages/EmployeeManagement/types';

export const useEmployees = () => {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(false);
  const [pageInfo, setPageInfo] = useState({
    current: 1,
    pageSize: 10,
    total: 0
  });

  // 获取员工列表
  const getEmployees = async (params?: EmployeePageRequest) => {
    setLoading(true);
    try {
      const response: any = await employeeAPI.getEmployees(params);
      const data = response.data;
      setEmployees(data.records || []);
      setPageInfo({
        current: data.pageNum || params?.current || 1,
        pageSize: data.pageSize || params?.size || 10,
        total: data.total || 0
      });
      return response;
    } catch (error: any) {
      message.error(error.message || '获取员工列表失败');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // 创建员工
  const createEmployee = async (data: any) => {
    try {
      const response: any = await employeeAPI.createEmployee(data);
      message.success('员工创建成功');
      return response;
    } catch (error: any) {
      message.error(error.message || '创建员工失败');
      throw error;
    }
  };

  // 更新员工
  const updateEmployee = async (data: any) => {
    try {
      const response: any = await employeeAPI.updateEmployee(data);
      message.success('员工更新成功');
      return response;
    } catch (error: any) {
      message.error(error.message || '更新员工失败');
      throw error;
    }
  };

  // 删除员工
  const deleteEmployee = async (id: number) => {
    try {
      const response: any = await employeeAPI.deleteEmployee(id);
      message.success('员工删除成功');
      return response;
    } catch (error: any) {
      message.error(error.message || '删除员工失败');
      throw error;
    }
  };

  // 重置密码
  const resetPassword = async (id: number) => {
    try {
      const response: any = await employeeAPI.resetPassword(id);
      message.success('密码重置成功');
      return response;
    } catch (error: any) {
      message.error(error.message || '密码重置失败');
      throw error;
    }
  };

  return {
    employees,
    loading,
    pageInfo,
    getEmployees,
    createEmployee,
    updateEmployee,
    deleteEmployee,
    resetPassword
  };
};
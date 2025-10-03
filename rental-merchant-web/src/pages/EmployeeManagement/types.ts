export interface Employee {
  id: number;
  storeId?: number;
  employeeName: string;
  phone: string;
  username: string;
  status: number;
  statusDesc: string;
  roleType: number;
  roleTypeDesc: string;
  lastLoginTime?: string;
  lastLoginIp?: string;
  createdTime?: string;
}

export interface EmployeePageRequest {
  current?: number;
  size?: number;
  employeeName?: string;
  phone?: string;
  status?: number;
}
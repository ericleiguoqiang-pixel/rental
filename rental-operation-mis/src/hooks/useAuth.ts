import { create } from 'zustand';

interface User {
  id: number;
  username: string;
  name: string;
  role: string;
  permissions: string[];
}

interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  token: string | null;
  login: (token: string, user: User) => void;
  logout: () => void;
  initAuth: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  isAuthenticated: false,
  user: null,
  token: null,
  
  login: (token: string, user: User) => {
    localStorage.setItem('mis_token', token);
    localStorage.setItem('mis_user', JSON.stringify(user));
    set({
      isAuthenticated: true,
      user,
      token,
    });
  },
  
  logout: () => {
    localStorage.removeItem('mis_token');
    localStorage.removeItem('mis_user');
    set({
      isAuthenticated: false,
      user: null,
      token: null,
    });
  },
  
  initAuth: () => {
    const token = localStorage.getItem('mis_token');
    const userStr = localStorage.getItem('mis_user');
    
    if (token && userStr) {
      try {
        const user = JSON.parse(userStr);
        set({
          isAuthenticated: true,
          user,
          token,
        });
      } catch (error) {
        // 解析失败，清除数据
        localStorage.removeItem('mis_token');
        localStorage.removeItem('mis_user');
      }
    }
  },
}));
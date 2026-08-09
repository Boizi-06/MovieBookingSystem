import React, { createContext, useContext, useState, useEffect } from 'react';
import { api, setTokens, clearTokens } from '../services/api';

interface UserInfo {
  id: number;
  fullname: string;
  email: string;
  phone: string | null;
  avatarUrl: string | null;
  role: 'CUSTOMER' | 'ADMIN';
  status: string;
}

interface AuthContextType {
  user: UserInfo | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  isAdmin: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserInfo | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  // Khôi phục phiên đăng nhập khi khởi chạy ứng dụng
  useEffect(() => {
    const initializeAuth = () => {
      try {
        const storedUser = localStorage.getItem('user_info');
        const token = localStorage.getItem('access_token');
        if (storedUser && token) {
          setUser(JSON.parse(storedUser));
        }
      } catch (e) {
        console.error('Lỗi khôi phục phiên đăng nhập', e);
        clearTokens();
      } finally {
        setLoading(false);
      }
    };

    initializeAuth();

    // Lắng nghe sự kiện auth_logout từ Axios Interceptor (khi refresh token hết hạn)
    const handleLogoutEvent = () => {
      logout();
    };

    window.addEventListener('auth_logout', handleLogoutEvent);
    return () => {
      window.removeEventListener('auth_logout', handleLogoutEvent);
    };
  }, []);

  const login = async (email: string, password: string) => {
    setLoading(true);
    try {
      const response = await api.post('/api/v1/auth/login', { email, password });
      if (response.data && response.data.success) {
        const { accessToken, refreshToken, user: loggedUser } = response.data.data;
        setTokens(accessToken, refreshToken);
        localStorage.setItem('user_info', JSON.stringify(loggedUser));
        setUser(loggedUser);
      } else {
        throw new Error(response.data.message || 'Đăng nhập thất bại');
      }
    } catch (error: any) {
      const errorMsg = error.response?.data?.message || error.message || 'Lỗi kết nối hệ thống';
      throw new Error(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    clearTokens();
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        login,
        logout,
        isAuthenticated: !!user,
        isAdmin: (user?.role || '').toUpperCase().replace('ROLE_', '') === 'ADMIN',
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth phải được sử dụng bên trong AuthProvider');
  }
  return context;
};

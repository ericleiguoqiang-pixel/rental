import React, { useEffect, useState } from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { Spin } from 'antd'

interface AuthGuardProps {
  children: React.ReactNode
}

const AuthGuard: React.FC<AuthGuardProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const location = useLocation()

  useEffect(() => {
    checkAuthStatus()
  }, [])

  const checkAuthStatus = async () => {
    try {
      const token = localStorage.getItem('token')
      const userInfo = localStorage.getItem('userInfo')

      if (!token || !userInfo) {
        setIsAuthenticated(false)
        setIsLoading(false)
        return
      }

      // 验证 token 是否有效
      try {
        const response = await fetch('/api/auth/verify', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        })

        if (response.ok) {
          setIsAuthenticated(true)
        } else {
          // Token 无效，清除本地存储
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          localStorage.removeItem('refreshToken')
          setIsAuthenticated(false)
        }
      } catch (error) {
        // 网络错误或后端不可用时，如果有 token 就认为已登录
        console.warn('Token 验证失败，使用本地验证:', error)
        setIsAuthenticated(true)
      }
    } catch (error) {
      console.error('认证检查失败:', error)
      setIsAuthenticated(false)
    } finally {
      setIsLoading(false)
    }
  }

  if (isLoading) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        flexDirection: 'column'
      }}>
        <Spin size="large" />
        <p style={{ marginTop: 16, color: '#666' }}>正在验证身份...</p>
      </div>
    )
  }

  if (!isAuthenticated) {
    // 保存当前页面路径，登录后可以跳转回来
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  return <>{children}</>
}

export default AuthGuard
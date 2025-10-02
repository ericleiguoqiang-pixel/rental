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
    // 防止无限循环，只在组件首次加载时检查
    checkAuthStatus()
  }, []) // 空依赖数组，确保只执行一次

  const checkAuthStatus = async () => {
    try {
      const token = localStorage.getItem('token')
      const userInfo = localStorage.getItem('userInfo')

      console.log('AuthGuard 检查认证状态:', { hasToken: !!token, hasUserInfo: !!userInfo })

      if (!token || !userInfo) {
        console.log('缺少 token 或 userInfo，设置为未认证')
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
          console.log('Token 验证成功')
          setIsAuthenticated(true)
        } else {
          console.log('Token 验证失败，状态码:', response.status)
          // Token 无效，清除本地存储
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          localStorage.removeItem('refreshToken')
          setIsAuthenticated(false)
        }
      } catch (error) {
        console.warn('Token 验证接口调用失败:', error)
        // 网络错误或后端不可用时，根据token的有效性判断
        // 如果token看起来像是有效的（不是null且有内容），暂时认为已登录
        if (token && token.length > 10 && !token.startsWith('undefined')) {
          console.log('后端服务不可用，但本地有有效token，允许访问')
          setIsAuthenticated(true)
        } else {
          console.log('token无效或后端服务不可用，设置为未认证')
          setIsAuthenticated(false)
        }
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
import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { ConfigProvider } from 'antd'
import zhCN from 'antd/locale/zh_CN'
import AuthGuard from './components/AuthGuard'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import MerchantAudit from './pages/MerchantAudit'
import VehicleAudit from './pages/VehicleAudit'
import StoreAudit from './pages/StoreAudit'
import CarModelManagement from './pages/CarModelManagement'
import './App.css'

function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <Router>
        <div className="App">
          <Routes>
            {/* 公开路由 - 不需要登录 */}
            <Route path="/login" element={<Login />} />

            {/* 受保护路由 - 需要登录 */}
            <Route path="/dashboard" element={
              <AuthGuard>
                <Dashboard />
              </AuthGuard>
            } />
            <Route path="/merchant-audit" element={
              <AuthGuard>
                <MerchantAudit />
              </AuthGuard>
            } />
            <Route path="/vehicle-audit" element={
              <AuthGuard>
                <VehicleAudit />
              </AuthGuard>
            } />
            <Route path="/store-audit" element={
              <AuthGuard>
                <StoreAudit />
              </AuthGuard>
            } />
            <Route path="/car-models" element={
              <AuthGuard>
                <CarModelManagement />
              </AuthGuard>
            } />
            
            {/* 默认路由 */}
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </div>
      </Router>
    </ConfigProvider>
  )
}

export default App
import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { Layout } from 'antd'
import AuthGuard from './components/AuthGuard'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import StoreManagement from './pages/StoreManagement'
import VehicleManagement from './pages/VehicleManagement'
import OrderManagement from './pages/OrderManagement'
import MerchantRegister from './pages/MerchantRegister'
import RegisterSuccess from './pages/RegisterSuccess'
import MerchantAudit from './pages/MerchantAudit'
import DebugPage from './pages/Debug'
import DebugServiceArea from './pages/DebugServiceArea'
import DebugServiceAreaV2 from './pages/DebugServiceAreaV2'

import './App.css'

const { Content } = Layout

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          {/* 公开路由 - 不需要登录 */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<MerchantRegister />} />
          <Route path="/register-success" element={<RegisterSuccess />} />
          <Route path="/debug" element={<DebugPage />} />

          
          {/* 受保护路由 - 需要登录 */}
          <Route path="/dashboard" element={
            <AuthGuard>
              <Dashboard />
            </AuthGuard>
          } />
          <Route path="/stores" element={
            <AuthGuard>
              <StoreManagement />
            </AuthGuard>
          } />
          <Route path="/vehicles" element={
            <AuthGuard>
              <VehicleManagement />
            </AuthGuard>
          } />
          <Route path="/orders" element={
            <AuthGuard>
              <OrderManagement />
            </AuthGuard>
          } />
          <Route path="/merchant-audit" element={
            <AuthGuard>
              <MerchantAudit />
            </AuthGuard>
          } />
          <Route path="/debug-service-area" element={<DebugServiceArea />} />
          <Route path="/debug-service-area-v2" element={<DebugServiceAreaV2 />} />
          
          {/* 默认路由 */}
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </div>
    </Router>
  )
}

export default App
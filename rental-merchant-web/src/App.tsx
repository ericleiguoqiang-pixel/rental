import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { Layout } from 'antd'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import StoreManagement from './pages/StoreManagement'
import VehicleManagement from './pages/VehicleManagement'
import OrderManagement from './pages/OrderManagement'
import './App.css'

const { Content } = Layout

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/stores" element={<StoreManagement />} />
          <Route path="/vehicles" element={<VehicleManagement />} />
          <Route path="/orders" element={<OrderManagement />} />
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </div>
    </Router>
  )
}

export default App
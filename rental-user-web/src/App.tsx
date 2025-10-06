import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Home from './pages/Home';
import QuoteList from './pages/QuoteList';
import QuoteDetail from './pages/QuoteDetail';
import OrderDetail from './pages/OrderDetail';
import MyOrders from './pages/MyOrders';
import Login from './pages/Login';
import AuthRoute from './components/AuthRoute';
import './App.css';

const App: React.FC = () => {
  return (
    <div className="app">
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={
          <AuthRoute>
            <Home />
          </AuthRoute>
        } />
        <Route path="/quotes" element={
          <AuthRoute>
            <QuoteList />
          </AuthRoute>
        } />
        <Route path="/quote/:id" element={
          <AuthRoute>
            <QuoteDetail />
          </AuthRoute>
        } />
        <Route path="/order/:id" element={
          <AuthRoute>
            <OrderDetail />
          </AuthRoute>
        } />
        <Route path="/my-orders" element={
          <AuthRoute>
            <MyOrders />
          </AuthRoute>
        } />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  );
};

export default App;
import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import QuoteList from './pages/QuoteList';
import QuoteDetail from './pages/QuoteDetail';
import Login from './pages/Login';
import './App.css';

const App: React.FC = () => {
  return (
    <div className="app">
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/quotes" element={<QuoteList />} />
        <Route path="/quote/:id" element={<QuoteDetail />} />
      </Routes>
    </div>
  );
};

export default App;
import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Auth/Login';
import Dashboard from './components/Dashboard/Dashboard';
import Books from './components/Books/Books';
import Members from './components/Members/Members';
import Loans from './components/Loans/Loans';
import Users from './components/Users/Users';
import Layout from './components/Layout/Layout';
import authService from './services/authService';
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userRole, setUserRole] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuthentication();
  }, []);

  const checkAuthentication = async () => {
    const authenticated = authService.isAuthenticated();
    if (authenticated) {
      try {
        const user = authService.getCurrentUser();
        if (user) {
          setUserRole(user.role);
          setIsAuthenticated(true);
        } else {
          authService.logout();
        }
      } catch (error) {
        console.error('Auth check error:', error);
        authService.logout();
      }
    }
    setLoading(false);
  };

  const handleLogin = (userData) => {
    setIsAuthenticated(true);
    setUserRole(userData.role);
  };

  const handleLogout = () => {
    authService.logout();
    setIsAuthenticated(false);
    setUserRole(null);
  };

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Loading Library System...</p>
        </div>
      </div>
    );
  }

  return (
    <Router>
      <div className="App">
        <Routes>
          <Route 
            path="/login" 
            element={
              isAuthenticated ? 
              <Navigate to="/dashboard" /> : 
              <Login onLogin={handleLogin} />
            } 
          />
          <Route 
            path="/" 
            element={
              isAuthenticated ? 
              <Layout onLogout={handleLogout} userRole={userRole} /> : 
              <Navigate to="/login" />
            }
          >
            <Route index element={<Navigate to="/dashboard" />} />
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="books" element={<Books />} />
            <Route path="members" element={<Members />} />
            <Route path="loans" element={<Loans />} />
            {authService.isAdmin() && (
              <Route path="users" element={<Users />} />
            )}
          </Route>
          <Route path="*" element={<Navigate to="/dashboard" />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
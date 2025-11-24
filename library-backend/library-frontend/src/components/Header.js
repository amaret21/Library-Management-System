import React from 'react';
import { useNavigate } from 'react-router-dom';
import authService from '../services/authService';

const Header = () => {
  const navigate = useNavigate();
  const username = authService.getCurrentUser();

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  return (
    <header className="app-header">
      <div className="header-content">
        <div className="header-title">
          <h1>📚 Library Management System</h1>
          <span className="welcome-message">
            Welcome, {username}!
          </span>
        </div>
        <button 
          className="logout-button"
          onClick={handleLogout}
        >
          Logout
        </button>
      </div>
    </header>
  );
};

export default Header;
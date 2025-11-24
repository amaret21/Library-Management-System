import React from 'react';
import { Link, Outlet, useLocation } from 'react-router-dom';
import authService from '../../services/authService';

const Layout = ({ onLogout, userRole }) => {
  const location = useLocation();
  const currentUser = authService.getCurrentUser();

  const isActive = (path) => {
    return location.pathname === path ? 'active' : '';
  };

  const handleLogout = () => {
    onLogout();
  };

  return (
    <div className="layout">
      <nav className="sidebar">
        <div className="sidebar-header">
          <h2>📚 Library System</h2>
        </div>
        <ul className="sidebar-nav">
          <li>
            <Link to="/dashboard" className={isActive('/dashboard')}>
              📊 Dashboard
            </Link>
          </li>
          <li>
            <Link to="/books" className={isActive('/books')}>
              📚 Books
            </Link>
          </li>
          <li>
            <Link to="/members" className={isActive('/members')}>
              👥 Members
            </Link>
          </li>
          <li>
            <Link to="/loans" className={isActive('/loans')}>
              🔄 Loans
            </Link>
          </li>
          {authService.isAdmin() && (
            <li>
              <Link to="/users" className={isActive('/users')}>
                👤 Users
              </Link>
            </li>
          )}
        </ul>
      </nav>

      <main className="main-content">
        <header className="header">
          <h1>Library Management System</h1>
          <div className="user-info">
            <span>Welcome, <strong>{currentUser?.fullName}</strong> ({currentUser?.role})</span>
            <button className="logout-btn" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </header>
        
        <Outlet />
      </main>
    </div>
  );
};

export default Layout;
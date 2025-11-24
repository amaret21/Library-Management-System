import React, { useState, useEffect } from 'react';
import API from '../../api';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalBooks: 0,
    availableBooks: 0,
    borrowedBooks: 0,
    totalMembers: 0,
    totalUsers: 0,
    activeLoans: 0,
    overdueLoans: 0,
    totalFines: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardStats();
  }, []);

  const fetchDashboardStats = async () => {
    try {
      const response = await API.get('/dashboard/stats');
      if (response.data.success) {
        setStats(response.data.data);
      }
    } catch (error) {
      console.error('Error fetching dashboard stats:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div>Loading dashboard...</div>;
  }

  return (
    <div>
      <h2>Dashboard Overview</h2>
      
      <div className="stats-grid">
        <div className="stat-card books">
          <h3>Total Books</h3>
          <div className="number">{stats.totalBooks}</div>
        </div>
        
        <div className="stat-card books">
          <h3>Available Books</h3>
          <div className="number">{stats.availableBooks}</div>
        </div>
        
        <div className="stat-card loans">
          <h3>Borrowed Books</h3>
          <div className="number">{stats.borrowedBooks}</div>
        </div>
        
        <div className="stat-card members">
          <h3>Total Members</h3>
          <div className="number">{stats.totalMembers}</div>
        </div>
        
        <div className="stat-card members">
          <h3>Total Users</h3>
          <div className="number">{stats.totalUsers}</div>
        </div>
        
        <div className="stat-card loans">
          <h3>Active Loans</h3>
          <div className="number">{stats.activeLoans}</div>
        </div>
        
        <div className="stat-card fines">
          <h3>Overdue Loans</h3>
          <div className="number">{stats.overdueLoans}</div>
        </div>
        
        <div className="stat-card fines">
          <h3>Total Fines</h3>
          <div className="number">${stats.totalFines?.toFixed(2) || '0.00'}</div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
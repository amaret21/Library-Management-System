import React from 'react';
import Header from './Header';
import BookList from './BookList';
import MemberList from './MemberList';
import LoanList from './LoanList';

const Dashboard = () => {
  return (
    <div className="app-layout">
      <Header />
      <div className="app-content">
        <div className="section">
          <h2>📖 Books Management</h2>
          <BookList />
        </div>
        <div className="section">
          <h2>👥 Members Management</h2>
          <MemberList />
        </div>
        <div className="section">
          <h2>🔄 Loans Management</h2>
          <LoanList />
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
import React, { useEffect, useState } from 'react';
import API from '../api';

export default function LoanList() {
  const [loans, setLoans] = useState([]);
  const [books, setBooks] = useState([]);
  const [members, setMembers] = useState([]);
  const [form, setForm] = useState({ bookId: '', memberId: '', dueDate: '' });
  const [activeTab, setActiveTab] = useState('all');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchAllData();
  }, []);

  async function fetchAllData() {
    setLoading(true);
    try {
      const [loansRes, booksRes, membersRes] = await Promise.all([
        API.get('/loans'),
        API.get('/books'),
        API.get('/members')
      ]);
      setLoans(loansRes.data);
      setBooks(booksRes.data);
      setMembers(membersRes.data);
    } catch (error) {
      console.error('Error fetching data:', error);
      alert('Error fetching data');
    } finally {
      setLoading(false);
    }
  }

  async function createLoan(e) {
    e.preventDefault();
    try {
      const loanData = {
        bookId: Number(form.bookId),
        memberId: Number(form.memberId),
        dueDate: form.dueDate || null
      };
      
      await API.post('/loans', loanData);
      await fetchAllData();
      setForm({ bookId: '', memberId: '', dueDate: '' });
    } catch (error) {
      console.error('Error creating loan:', error);
      alert(error.response?.data?.error || 'Error creating loan');
    }
  }

  async function returnLoan(id) {
    try {
      await API.post(`/loans/return/${id}`);
      await fetchAllData();
    } catch (error) {
      console.error('Error returning loan:', error);
      alert(error.response?.data?.error || 'Error returning loan');
    }
  }

  async function removeLoan(id) {
    if (window.confirm('Are you sure you want to delete this loan record?')) {
      try {
        await API.delete(`/loans/${id}`);
        await fetchAllData();
      } catch (error) {
        console.error('Error deleting loan:', error);
        alert('Error deleting loan');
      }
    }
  }

  const availableBooks = books.filter(book => book.copies > 0);
  const displayedLoans = activeTab === 'active' 
    ? loans.filter(loan => !loan.returned)
    : loans;

  return (
    <div>
      <form onSubmit={createLoan} className="form-container">
        <h4>Create New Loan</h4>
        <select 
          value={form.bookId} 
          onChange={e => setForm({...form, bookId: e.target.value})} 
          required
        >
          <option value="">Select Book *</option>
          {availableBooks.map(book => (
            <option key={book.id} value={book.id}>
              {book.title} ({book.copies} copies available)
            </option>
          ))}
        </select>
        
        <select 
          value={form.memberId} 
          onChange={e => setForm({...form, memberId: e.target.value})} 
          required
        >
          <option value="">Select Member *</option>
          {members.map(member => (
            <option key={member.id} value={member.id}>
              {member.firstName} {member.lastName}
            </option>
          ))}
        </select>
        
        <input 
          type="date" 
          value={form.dueDate} 
          onChange={e => setForm({...form, dueDate: e.target.value})}
          min={new Date().toISOString().split('T')[0]}
        />
        <small>Due Date (optional - defaults to 14 days from now)</small>
        
        <div>
          <button type="submit">Create Loan</button>
        </div>
      </form>

      <div style={{ marginBottom: '20px' }}>
        <button 
          onClick={() => setActiveTab('all')}
          style={{ 
            backgroundColor: activeTab === 'all' ? '#3498db' : '#95a5a6',
            marginRight: '10px'
          }}
        >
          All Loans
        </button>
        <button 
          onClick={() => setActiveTab('active')}
          style={{ 
            backgroundColor: activeTab === 'active' ? '#3498db' : '#95a5a6'
          }}
        >
          Active Loans Only
        </button>
      </div>

      {loading ? (
        <p>Loading loans...</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Book</th>
              <th>Member</th>
              <th>Loan Date</th>
              <th>Due Date</th>
              <th>Returned</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {displayedLoans.map(loan => (
              <tr key={loan.id} style={{
                backgroundColor: loan.returned ? '#f8f9fa' : 'white'
              }}>
                <td>{loan.id}</td>
                <td>{loan.book?.title}</td>
                <td>{loan.member?.firstName} {loan.member?.lastName}</td>
                <td>{loan.loanDate}</td>
                <td style={{
                  color: !loan.returned && new Date(loan.dueDate) < new Date() ? 'red' : 'inherit'
                }}>
                  {loan.dueDate}
                </td>
                <td>{loan.returned ? 'Yes' : 'No'}</td>
                <td>
                  {!loan.returned && (
                    <button 
                      className="action-button return"
                      onClick={() => returnLoan(loan.id)}
                    >
                      Return
                    </button>
                  )}
                  <button 
                    className="action-button"
                    onClick={() => removeLoan(loan.id)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
            {displayedLoans.length === 0 && (
              <tr>
                <td colSpan="7" style={{ textAlign: 'center' }}>
                  No {activeTab === 'active' ? 'active' : ''} loans found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  );
}
import React, { useEffect, useState } from 'react';
import API from '../../api';

const Users = () => {
  const [users, setUsers] = useState([]);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [showGenerateForm, setShowGenerateForm] = useState(false);
  const [form, setForm] = useState({ 
    username: '', 
    password: '', 
    email: '', 
    fullName: '', 
    role: 'ROLE_USER' 
  });
  const [generateForm, setGenerateForm] = useState({ 
    email: '', 
    fullName: '', 
    role: 'ROLE_USER' 
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchUsers();
  }, []);

  async function fetchUsers() {
    setLoading(true);
    try {
      const response = await API.get('/users');
      setUsers(response.data.data || []);
    } catch (error) {
      console.error('Error fetching users:', error);
      alert('Error fetching users');
    } finally {
      setLoading(false);
    }
  }

  async function createUser(e) {
    e.preventDefault();
    try {
      await API.post('/users', form);
      await fetchUsers();
      resetForms();
      alert('User created successfully!');
    } catch (error) {
      console.error('Error creating user:', error);
      alert(error.response?.data?.message || 'Error creating user');
    }
  }

  async function generateUser(e) {
    e.preventDefault();
    try {
      await API.post('/users/generate', generateForm);
      await fetchUsers();
      resetForms();
      alert('User generated successfully!');
    } catch (error) {
      console.error('Error generating user:', error);
      alert(error.response?.data?.message || 'Error generating user');
    }
  }

  async function resetPassword(userId) {
    const newPassword = prompt('Enter new password:');
    if (newPassword) {
      try {
        await API.put(`/users/${userId}/password`, { newPassword });
        alert('Password reset successfully!');
      } catch (error) {
        console.error('Error resetting password:', error);
        alert('Error resetting password');
      }
    }
  }

  async function removeUser(id) {
    if (window.confirm('Are you sure you want to delete this user?')) {
      try {
        await API.delete(`/users/${id}`);
        await fetchUsers();
      } catch (error) {
        console.error('Error deleting user:', error);
        alert('Error deleting user');
      }
    }
  }

  function resetForms() {
    setForm({ username: '', password: '', email: '', fullName: '', role: 'ROLE_USER' });
    setGenerateForm({ email: '', fullName: '', role: 'ROLE_USER' });
    setShowCreateForm(false);
    setShowGenerateForm(false);
  }

  return (
    <div>
      <h2>User Management</h2>

      <div style={{ marginBottom: '20px' }}>
        <button 
          onClick={() => setShowCreateForm(!showCreateForm)}
          style={{ marginRight: '10px' }}
        >
          {showCreateForm ? 'Cancel Create' : 'Create User'}
        </button>
        <button 
          onClick={() => setShowGenerateForm(!showGenerateForm)}
        >
          {showGenerateForm ? 'Cancel Generate' : 'Generate User'}
        </button>
      </div>

      {showCreateForm && (
        <form onSubmit={createUser} className="form-container">
          <h4>Create New User</h4>
          <input
            placeholder="Username *"
            value={form.username}
            onChange={e => setForm({...form, username: e.target.value})}
            required
          />
          <input
            type="password"
            placeholder="Password *"
            value={form.password}
            onChange={e => setForm({...form, password: e.target.value})}
            required
          />
          <input
            type="email"
            placeholder="Email *"
            value={form.email}
            onChange={e => setForm({...form, email: e.target.value})}
            required
          />
          <input
            placeholder="Full Name *"
            value={form.fullName}
            onChange={e => setForm({...form, fullName: e.target.value})}
            required
          />
          <select
            value={form.role}
            onChange={e => setForm({...form, role: e.target.value})}
          >
            <option value="ROLE_USER">User</option>
            <option value="ROLE_LIBRARIAN">Librarian</option>
            <option value="ROLE_ADMIN">Admin</option>
          </select>
          <div>
            <button type="submit">Create User</button>
            <button type="button" onClick={resetForms}>Cancel</button>
          </div>
        </form>
      )}

      {showGenerateForm && (
        <form onSubmit={generateUser} className="form-container">
          <h4>Generate System User</h4>
          <input
            type="email"
            placeholder="Email *"
            value={generateForm.email}
            onChange={e => setGenerateForm({...generateForm, email: e.target.value})}
            required
          />
          <input
            placeholder="Full Name *"
            value={generateForm.fullName}
            onChange={e => setGenerateForm({...generateForm, fullName: e.target.value})}
            required
          />
          <select
            value={generateForm.role}
            onChange={e => setGenerateForm({...generateForm, role: e.target.value})}
          >
            <option value="ROLE_USER">User</option>
            <option value="ROLE_LIBRARIAN">Librarian</option>
            <option value="ROLE_ADMIN">Admin</option>
          </select>
          <div>
            <button type="submit">Generate User</button>
            <button type="button" onClick={resetForms}>Cancel</button>
          </div>
        </form>
      )}

      {loading ? (
        <p>Loading users...</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Username</th>
              <th>Full Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>System Generated</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.username}</td>
                <td>{user.fullName}</td>
                <td>{user.email}</td>
                <td>{user.role}</td>
                <td>{user.systemGenerated ? 'Yes' : 'No'}</td>
                <td>
                  <button 
                    className="action-button edit"
                    onClick={() => resetPassword(user.id)}
                  >
                    Reset Password
                  </button>
                  <button 
                    className="action-button"
                    onClick={() => removeUser(user.id)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
            {users.length === 0 && (
              <tr>
                <td colSpan="7" style={{ textAlign: 'center' }}>
                  No users found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default Users;
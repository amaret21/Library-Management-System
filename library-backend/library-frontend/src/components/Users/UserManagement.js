import React, { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Key, UserPlus } from 'lucide-react';
import userService from '../../services/userService';
import authService from '../../services/authService';
import './UserManagement.css';

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showGenerateModal, setShowGenerateModal] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [createForm, setCreateForm] = useState({
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

  useEffect(() => {
    if (authService.isAdmin()) {
      loadUsers();
    }
  }, []);

  const loadUsers = async () => {
    setLoading(true);
    try {
      const response = await userService.getAllUsers();
      setUsers(response.data);
    } catch (error) {
      setError('Failed to load users');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateUser = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await userService.createUser(createForm);
      setSuccess('User created successfully');
      setShowCreateModal(false);
      setCreateForm({
        username: '', password: '', email: '', fullName: '', role: 'ROLE_USER'
      });
      loadUsers();
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to create user');
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateUser = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await userService.generateUser(generateForm);
      setSuccess(`User generated successfully! Username: ${response.data.username}, Password: ${response.data.password}`);
      setShowGenerateModal(false);
      setGenerateForm({ email: '', fullName: '', role: 'ROLE_USER' });
      loadUsers();
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to generate user');
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (userId) => {
    if (!window.confirm('Are you sure you want to reset this user\'s password?')) return;
    
    const newPassword = prompt('Enter new password:');
    if (!newPassword) return;

    try {
      await userService.resetPassword(userId, { newPassword });
      setSuccess('Password reset successfully');
      loadUsers();
    } catch (error) {
      setError('Failed to reset password');
    }
  };

  const clearMessages = () => {
    setError('');
    setSuccess('');
  };

  if (!authService.isAdmin()) {
    return (
      <div className="user-management">
        <div className="error-message">
          Access denied. Admin privileges required.
        </div>
      </div>
    );
  }

  return (
    <div className="user-management">
      <div className="page-header">
        <h1>User Management</h1>
        <div className="header-actions">
          <button 
            className="btn btn-primary"
            onClick={() => setShowGenerateModal(true)}
          >
            <UserPlus size={18} />
            Generate User
          </button>
          <button 
            className="btn btn-secondary"
            onClick={() => setShowCreateModal(true)}
          >
            <Plus size={18} />
            Create User
          </button>
        </div>
      </div>

      {error && (
        <div className="alert alert-error" onClick={clearMessages}>
          {error}
        </div>
      )}

      {success && (
        <div className="alert alert-success" onClick={clearMessages}>
          {success}
        </div>
      )}

      <div className="users-table-container">
        {loading ? (
          <div className="loading">Loading users...</div>
        ) : (
          <table className="users-table">
            <thead>
              <tr>
                <th>Username</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map(user => (
                <tr key={user.id}>
                  <td>
                    <div className="user-info">
                      {user.username}
                      {user.systemGenerated && <span className="badge system">System</span>}
                    </div>
                  </td>
                  <td>{user.fullName}</td>
                  <td>{user.email}</td>
                  <td>
                    <span className={`role-badge ${user.role.toLowerCase().replace('role_', '')}`}>
                      {user.role.replace('ROLE_', '')}
                    </span>
                  </td>
                  <td>
                    <span className={`status-badge ${user.active ? 'active' : 'inactive'}`}>
                      {user.active ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td>
                    <div className="action-buttons">
                      <button
                        className="btn-icon btn-warning"
                        onClick={() => handleResetPassword(user.id)}
                        title="Reset Password"
                      >
                        <Key size={16} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Create User Modal */}
      {showCreateModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h2>Create New User</h2>
            <form onSubmit={handleCreateUser}>
              <div className="form-group">
                <label>Username</label>
                <input
                  type="text"
                  value={createForm.username}
                  onChange={(e) => setCreateForm({...createForm, username: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label>Password</label>
                <input
                  type="password"
                  value={createForm.password}
                  onChange={(e) => setCreateForm({...createForm, password: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label>Email</label>
                <input
                  type="email"
                  value={createForm.email}
                  onChange={(e) => setCreateForm({...createForm, email: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label>Full Name</label>
                <input
                  type="text"
                  value={createForm.fullName}
                  onChange={(e) => setCreateForm({...createForm, fullName: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label>Role</label>
                <select
                  value={createForm.role}
                  onChange={(e) => setCreateForm({...createForm, role: e.target.value})}
                >
                  <option value="ROLE_USER">User</option>
                  <option value="ROLE_LIBRARIAN">Librarian</option>
                  <option value="ROLE_ADMIN">Admin</option>
                </select>
              </div>
              <div className="modal-actions">
                <button type="button" onClick={() => setShowCreateModal(false)}>
                  Cancel
                </button>
                <button type="submit" disabled={loading}>
                  {loading ? 'Creating...' : 'Create User'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Generate User Modal */}
      {showGenerateModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h2>Generate System User</h2>
            <p className="modal-description">
              System will generate username and password automatically based on full name.
            </p>
            <form onSubmit={handleGenerateUser}>
              <div className="form-group">
                <label>Full Name</label>
                <input
                  type="text"
                  value={generateForm.fullName}
                  onChange={(e) => setGenerateForm({...generateForm, fullName: e.target.value})}
                  placeholder="e.g., John Michael Smith"
                  required
                />
              </div>
              <div className="form-group">
                <label>Email</label>
                <input
                  type="email"
                  value={generateForm.email}
                  onChange={(e) => setGenerateForm({...generateForm, email: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label>Role</label>
                <select
                  value={generateForm.role}
                  onChange={(e) => setGenerateForm({...generateForm, role: e.target.value})}
                >
                  <option value="ROLE_USER">User</option>
                  <option value="ROLE_LIBRARIAN">Librarian</option>
                </select>
              </div>
              <div className="modal-actions">
                <button type="button" onClick={() => setShowGenerateModal(false)}>
                  Cancel
                </button>
                <button type="submit" disabled={loading}>
                  {loading ? 'Generating...' : 'Generate User'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default UserManagement;
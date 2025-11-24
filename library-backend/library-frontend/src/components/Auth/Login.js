import React, { useState } from 'react';
import authService from '../../services/authService';

const Login = ({ onLogin }) => {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      console.log('Attempting login with username:', formData.username);
      
      // Directly get user data from auth service
      const userData = await authService.login(formData.username, formData.password);
      console.log('Login successful, user data:', userData);
      
      // Call onLogin with the user data
      onLogin(userData);
    } catch (err) {
      console.error('Login error:', err);
      console.error('Error response:', err.response);
      
      // Enhanced error handling for different error formats
      const errorMessage = err.response?.data?.message || 
                          err.response?.data?.error ||
                          err.message || 
                          'Login failed. Please check your credentials and try again.';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <form className="login-form" onSubmit={handleSubmit}>
        <h2>Library Management System</h2>
        
        {error && (
          <div style={{ 
            color: '#e74c3c', 
            backgroundColor: '#fadbd8', 
            padding: '10px', 
            borderRadius: '4px', 
            marginBottom: '15px',
            textAlign: 'center',
            fontSize: '14px'
          }}>
            {error}
          </div>
        )}

        <div>
          <input
            type="text"
            name="username"
            placeholder="Username"
            value={formData.username}
            onChange={handleChange}
            required
            disabled={loading}
            style={{ width: '100%', padding: '10px', margin: '5px 0' }}
          />
        </div>

        <div>
          <input
            type="password"
            name="password"
            placeholder="Password"
            value={formData.password}
            onChange={handleChange}
            required
            disabled={loading}
            style={{ width: '100%', padding: '10px', margin: '5px 0' }}
          />
        </div>

        <button 
          type="submit" 
          disabled={loading}
          style={{
            width: '100%',
            padding: '10px',
            backgroundColor: loading ? '#95a5a6' : '#3498db',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer',
            marginTop: '10px'
          }}
        >
          {loading ? 'Logging in...' : 'Login'}
        </button>

        <div style={{ 
          marginTop: '20px', 
          textAlign: 'center', 
          color: '#7f8c8d',
          fontSize: '14px',
          border: '1px solid #bdc3c7',
          padding: '15px',
          borderRadius: '4px',
          backgroundColor: '#f8f9fa'
        }}>
          <p style={{ margin: '0 0 10px 0', fontWeight: 'bold' }}>Demo Credentials:</p>
          <p style={{ margin: '5px 0' }}>Admin: <strong>admin</strong> / <strong>admin123</strong></p>
          <p style={{ margin: '5px 0' }}>Librarian: <strong>librarian</strong> / <strong>lib123</strong></p>
        </div>
      </form>
    </div>
  );
};

export default Login;
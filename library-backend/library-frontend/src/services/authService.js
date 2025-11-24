import API from '../api';

class AuthService {
  async login(username, password) {
    try {
      console.log('=== LOGIN REQUEST START ===');
      console.log('Sending POST login request for user:', username);
      
      // Make POST request with explicit configuration
      const response = await API.post('/auth/login', 
        { 
          username: username.trim(),
          password: password 
        }, 
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          timeout: 10000
        }
      );
      
      console.log('=== LOGIN RESPONSE ===');
      console.log('Response status:', response.status);
      console.log('Response headers:', response.headers);
      console.log('Response data:', response.data);

      const responseData = response.data;

      // Check if login was successful
      if (!responseData.success) {
        console.log('Login failed - success flag is false');
        throw new Error(responseData.message || 'Login failed');
      }

      // Validate that we have a token
      if (!responseData.token) {
        console.log('Login failed - no token received');
        throw new Error('No authentication token received from server');
      }

      // Store authentication data
      localStorage.setItem('token', responseData.token);
      
      // Store user data
      const userData = {
        username: responseData.username,
        role: responseData.role,
        fullName: responseData.fullName,
        email: responseData.email,
        id: responseData.id
      };
      localStorage.setItem('user', JSON.stringify(userData));

      // Set default authorization header for future requests
      API.defaults.headers.common['Authorization'] = `Bearer ${responseData.token}`;

      console.log('=== LOGIN SUCCESS ===');
      console.log('User stored:', userData);
      console.log('Token stored:', responseData.token ? 'Yes' : 'No');
      
      return responseData;
      
    } catch (error) {
      console.error('=== LOGIN ERROR ===');
      console.error('Error name:', error.name);
      console.error('Error message:', error.message);
      console.error('Error code:', error.code);
      
      if (error.response) {
        // Server responded with error status
        console.error('Error response status:', error.response.status);
        console.error('Error response data:', error.response.data);
        console.error('Error response headers:', error.response.headers);
        
        const serverError = error.response.data;
        let errorMessage = 'Login failed. Please try again.';
        
        if (serverError && serverError.message) {
          errorMessage = serverError.message;
        } else if (serverError && typeof serverError === 'string') {
          errorMessage = serverError;
        } else if (error.response.status === 401) {
          errorMessage = 'Invalid username or password';
        } else if (error.response.status === 403) {
          errorMessage = 'Access denied';
        } else if (error.response.status === 404) {
          errorMessage = 'Login service not found';
        } else if (error.response.status === 405) {
          errorMessage = 'Method not allowed - please contact administrator';
        } else if (error.response.status >= 500) {
          errorMessage = 'Server error. Please try again later.';
        }
        
        throw new Error(errorMessage);
        
      } else if (error.request) {
        // Request made but no response received
        console.error('No response received - request details:', error.request);
        throw new Error('No response from server. Please check if the backend is running on port 8080.');
      } else {
        // Something else happened
        console.error('Request configuration error:', error.config);
        throw new Error(error.message || 'Login request failed');
      }
    }
  }

  // Test backend connection with multiple endpoints
  async testConnection() {
    console.log('=== TESTING BACKEND CONNECTION ===');
    
    const testEndpoints = [
      '/',
      '/api/',
      '/api/auth/'
    ];
    
    for (const endpoint of testEndpoints) {
      try {
        console.log(`Testing endpoint: ${endpoint}`);
        const response = await API.get(endpoint, { timeout: 5000 });
        console.log(`✅ ${endpoint} - Status: ${response.status}`);
        return true;
      } catch (error) {
        console.log(`❌ ${endpoint} - Error: ${error.message}`);
      }
    }
    
    console.log('All connection tests failed');
    return false;
  }

  // Test login with direct fetch (bypass axios)
  async testLoginDirect(username, password) {
    try {
      console.log('=== TESTING LOGIN WITH DIRECT FETCH ===');
      
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: username,
          password: password
        })
      });
      
      console.log('Fetch response status:', response.status);
      console.log('Fetch response ok:', response.ok);
      
      const data = await response.json();
      console.log('Fetch response data:', data);
      
      return data;
    } catch (error) {
      console.error('Fetch test error:', error);
      throw error;
    }
  }

  logout() {
    console.log('Logging out user');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    delete API.defaults.headers.common['Authorization'];
    
    // Optional: Call logout endpoint if available
    try {
      return API.post('/auth/logout');
    } catch (error) {
      console.log('Logout API call failed, but local session cleared');
      return Promise.resolve();
    }
  }

  getCurrentUser() {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        console.log('Retrieved current user:', user);
        return user;
      } catch (error) {
        console.error('Error parsing user data:', error);
        this.logout();
        return null;
      }
    }
    return null;
  }

  getToken() {
    return localStorage.getItem('token');
  }

  isAuthenticated() {
    const token = this.getToken();
    if (!token) {
      console.log('No token found - user not authenticated');
      return false;
    }
    
    // Check JWT expiration if it's a JWT token
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const isExpired = payload.exp * 1000 < Date.now();
      if (isExpired) {
        console.log('Token expired - logging out');
        this.logout();
        return false;
      }
      return true;
    } catch (error) {
      console.log('Token validation skipped (non-JWT or parse error)');
      return true;
    }
  }

  isAdmin() {
    const user = this.getCurrentUser();
    const isAdmin = user && (user.role === 'ROLE_ADMIN' || user.role === 'ADMIN');
    console.log('isAdmin check:', isAdmin, 'User role:', user?.role);
    return isAdmin;
  }

  isLibrarian() {
    const user = this.getCurrentUser();
    const isLibrarian = user && (user.role === 'ROLE_LIBRARIAN' || user.role === 'LIBRARIAN');
    console.log('isLibrarian check:', isLibrarian, 'User role:', user?.role);
    return isLibrarian;
  }

  getUserRole() {
    const user = this.getCurrentUser();
    return user ? user.role : null;
  }

  async validateToken() {
    if (!this.isAuthenticated()) {
      console.log('Token validation failed: not authenticated');
      return false;
    }

    try {
      console.log('Validating token with server...');
      const response = await API.get('/auth/me');
      
      if (response.data && response.data.success !== undefined) {
        const isValid = response.data.success;
        console.log('Token validation result:', isValid);
        return isValid;
      }
      
      const isValid = response.status >= 200 && response.status < 300;
      console.log('Token validation result (fallback):', isValid);
      return isValid;
      
    } catch (error) {
      console.error('Token validation error:', error);
      
      if (error.response && (error.response.status === 401 || error.response.status === 403)) {
        console.log('Token invalid - clearing auth data');
        this.logout();
      }
      return false;
    }
  }

  // Helper method to update auth header after token refresh
  updateAuthHeader(token) {
    if (token) {
      API.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      localStorage.setItem('token', token);
      console.log('Auth header updated with new token');
    }
  }

  // Debug method to check stored auth data
  debugAuth() {
    const token = this.getToken();
    const user = this.getCurrentUser();
    console.log('=== AUTH DEBUG ===');
    console.log('Token:', token ? 'Present' : 'Missing');
    console.log('User:', user);
    console.log('Authenticated:', this.isAuthenticated());
    console.log('User Role:', this.getUserRole());
    return { token, user, isAuthenticated: this.isAuthenticated() };
  }

  // Clear all auth data (for debugging)
  clearAuth() {
    console.log('Clearing all auth data');
    this.logout();
  }

  // Get API base URL for debugging
  getApiBaseUrl() {
    return API.defaults.baseURL;
  }
}

export default new AuthService();
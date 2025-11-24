import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
});

// Add comprehensive request interceptor for debugging
API.interceptors.request.use(
  (config) => {
    console.group(`🔄 API Request: ${config.method?.toUpperCase()} ${config.url}`);
    console.log('Request Config:', {
      method: config.method,
      url: config.url,
      baseURL: config.baseURL,
      data: config.data,
      headers: config.headers
    });
    
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('✅ Authorization header added with token');
    } else {
      console.log('❌ No token found in localStorage');
    }
    
    console.groupEnd();
    return config;
  },
  (error) => {
    console.error('🚨 Request Interceptor Error:', error);
    return Promise.reject(error);
  }
);

// Add comprehensive response interceptor for debugging
API.interceptors.response.use(
  (response) => {
    console.group(`✅ API Response Success: ${response.config.method?.toUpperCase()} ${response.config.url}`);
    console.log('Response Status:', response.status);
    console.log('Response Data:', response.data);
    console.log('Response Headers:', response.headers);
    console.groupEnd();
    return response;
  },
  (error) => {
    console.group(`❌ API Response Error: ${error.config?.method?.toUpperCase()} ${error.config?.url}`);
    console.log('Error Details:', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message,
      config: {
        method: error.config?.method,
        url: error.config?.url,
        data: error.config?.data
      }
    });
    
    // Handle specific error cases
    if (error.response) {
      // Server responded with error status
      console.log('📡 Server responded with error:', error.response.status);
      
      if (error.response.status === 401) {
        console.log('🔐 401 Unauthorized - Clearing auth data');
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        
        // Only redirect if not already on login page
        if (!window.location.pathname.includes('/login')) {
          console.log('🔄 Redirecting to login page');
          window.location.href = '/login';
        }
      } else if (error.response.status === 403) {
        console.log('🚫 403 Forbidden - Access denied');
      } else if (error.response.status === 404) {
        console.log('🔍 404 Not Found - Endpoint does not exist');
      } else if (error.response.status >= 500) {
        console.log('💥 Server Error - Backend issue');
      }
    } else if (error.request) {
      // Request made but no response received
      console.log('🌐 Network Error - No response received from server');
      console.log('Check if:');
      console.log('1. Backend server is running on localhost:8080');
      console.log('2. CORS is properly configured');
      console.log('3. Network connection is stable');
    } else {
      // Something else happened
      console.log('⚡ Request Setup Error:', error.message);
    }
    
    console.groupEnd();
    return Promise.reject(error);
  }
);

// Add a test method to verify backend connection
API.testConnection = async () => {
  try {
    console.log('🧪 Testing backend connection...');
    const response = await API.get('/');
    console.log('✅ Backend connection test successful:', response.status);
    return true;
  } catch (error) {
    console.error('❌ Backend connection test failed:', error.message);
    return false;
  }
};

// Add a method to check if backend is reachable
API.isBackendAlive = async () => {
  try {
    const response = await axios.get('http://localhost:8080/actuator/health', { timeout: 5000 });
    return response.status === 200;
  } catch (error) {
    try {
      // Try the root endpoint
      const response = await axios.get('http://localhost:8080/', { timeout: 5000 });
      return response.status === 200;
    } catch (e) {
      return false;
    }
  }
};

// Export the enhanced API instance
export default API;
import API from './api';

class UserService {
  getAllUsers() {
    return API.get('/users');
  }

  getUserById(id) {
    return API.get(`/users/${id}`);
  }

  createUser(userData) {
    return API.post('/users', userData);
  }

  updateUser(id, userData) {
    return API.put(`/users/${id}`, userData);
  }

  deleteUser(id) {
    return API.delete(`/users/${id}`);
  }

  generateUser(userData) {
    return API.post('/users/generate', userData);
  }

  resetPassword(id, passwordData) {
    return API.put(`/users/${id}/password`, passwordData);
  }

  getUsersByRole(role) {
    return API.get(`/users/role/${role}`);
  }
}

export default new UserService();
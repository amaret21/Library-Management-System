import API from './api';

class DashboardService {
  async getDashboardStats() {
    const response = await API.get('/dashboard/stats');
    return response.data;
  }
}

export default new DashboardService();
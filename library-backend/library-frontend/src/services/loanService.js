import API from './api';

class LoanService {
  async getAllLoans() {
    try {
      const response = await API.get('/loans');
      return response.data.data || [];
    } catch (error) {
      console.error('Error fetching loans:', error);
      throw error;
    }
  }

  async getActiveLoans() {
    try {
      const response = await API.get('/loans/active');
      return response.data.data || [];
    } catch (error) {
      console.error('Error fetching active loans:', error);
      throw error;
    }
  }

  async createLoan(loanData) {
    try {
      const response = await API.post('/loans', loanData);
      return response.data;
    } catch (error) {
      console.error('Error creating loan:', error);
      throw error;
    }
  }

  async returnLoan(loanId) {
    try {
      const response = await API.put(`/loans/${loanId}/return`);
      return response.data;
    } catch (error) {
      console.error('Error returning loan:', error);
      throw error;
    }
  }

  async renewLoan(loanId) {
    try {
      const response = await API.put(`/loans/${loanId}/renew`);
      return response.data;
    } catch (error) {
      console.error('Error renewing loan:', error);
      throw error;
    }
  }

  async getOverdueLoans() {
    try {
      const response = await API.get('/loans/overdue');
      return response.data.data || [];
    } catch (error) {
      console.error('Error fetching overdue loans:', error);
      throw error;
    }
  }

  async getUserLoans(userId) {
    try {
      const response = await API.get(`/loans/user/${userId}`);
      return response.data.data || [];
    } catch (error) {
      console.error('Error fetching user loans:', error);
      throw error;
    }
  }
}

export default new LoanService();
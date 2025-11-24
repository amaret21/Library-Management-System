import API from './api';

class MemberService {
  async getAllMembers(params = {}) {
    const response = await API.get('/members', { params });
    return response.data;
  }

  async getMemberById(id) {
    const response = await API.get(`/members/${id}`);
    return response.data;
  }

  async createMember(memberData) {
    const response = await API.post('/members', memberData);
    return response.data;
  }

  async updateMember(id, memberData) {
    const response = await API.put(`/members/${id}`, memberData);
    return response.data;
  }

  async deleteMember(id) {
    const response = await API.delete(`/members/${id}`);
    return response.data;
  }

  async searchMembers(keyword) {
    const response = await API.get('/members/search', { params: { keyword } });
    return response.data;
  }

  async getMemberCount() {
    const response = await API.get('/members/count');
    return response.data;
  }
}

export default new MemberService();
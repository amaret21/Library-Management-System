import React, { useEffect, useState } from 'react';
import API from '../../api';

const Members = () => {
  const [members, setMembers] = useState([]);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({ 
    firstName: '', 
    lastName: '', 
    email: '', 
    phone: '',
    address: '',
    dateOfBirth: ''
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchMembers();
  }, []);

  async function fetchMembers() {
    setLoading(true);
    try {
      const response = await API.get('/members');
      setMembers(response.data.data || []);
    } catch (error) {
      console.error('Error fetching members:', error);
      alert('Error fetching members');
    } finally {
      setLoading(false);
    }
  }

  async function save(e) {
    e.preventDefault();
    try {
      if (editing) {
        await API.put(`/members/${editing.id}`, form);
      } else {
        await API.post('/members', form);
      }
      await fetchMembers();
      reset();
    } catch (error) {
      console.error('Error saving member:', error);
      alert('Error saving member');
    }
  }

  function edit(member) {
    setEditing(member);
    setForm({
      firstName: member.firstName,
      lastName: member.lastName,
      email: member.email || '',
      phone: member.phone || '',
      address: member.address || '',
      dateOfBirth: member.dateOfBirth || ''
    });
  }

  async function remove(id) {
    if (window.confirm('Are you sure you want to delete this member?')) {
      try {
        await API.delete(`/members/${id}`);
        await fetchMembers();
      } catch (error) {
        console.error('Error deleting member:', error);
        alert('Error deleting member');
      }
    }
  }

  function reset() {
    setEditing(null);
    setForm({ 
      firstName: '', 
      lastName: '', 
      email: '', 
      phone: '',
      address: '',
      dateOfBirth: ''
    });
  }

  return (
    <div>
      <h2>Member Management</h2>

      <form onSubmit={save} className="form-container">
        <h4>{editing ? 'Edit Member' : 'Add New Member'}</h4>
        <input
          placeholder="First Name *"
          value={form.firstName}
          onChange={e => setForm({...form, firstName: e.target.value})}
          required
        />
        <input
          placeholder="Last Name *"
          value={form.lastName}
          onChange={e => setForm({...form, lastName: e.target.value})}
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={e => setForm({...form, email: e.target.value})}
        />
        <input
          placeholder="Phone"
          value={form.phone}
          onChange={e => setForm({...form, phone: e.target.value})}
        />
        <textarea
          placeholder="Address"
          value={form.address}
          onChange={e => setForm({...form, address: e.target.value})}
          style={{ width: '100%', padding: '12px', marginBottom: '15px', border: '1px solid #ddd', borderRadius: '4px', minHeight: '80px' }}
        />
        <input
          type="date"
          placeholder="Date of Birth"
          value={form.dateOfBirth}
          onChange={e => setForm({...form, dateOfBirth: e.target.value})}
        />
        <div>
          <button type="submit">{editing ? 'Update' : 'Add'} Member</button>
          {editing && (
            <button type="button" onClick={reset}>Cancel</button>
          )}
        </div>
      </form>

      {loading ? (
        <p>Loading members...</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Membership ID</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {members.map(member => (
              <tr key={member.id}>
                <td>{member.id}</td>
                <td>{member.firstName} {member.lastName}</td>
                <td>{member.email || 'N/A'}</td>
                <td>{member.phone || 'N/A'}</td>
                <td>{member.membershipId || 'N/A'}</td>
                <td>
                  <button 
                    className="action-button edit"
                    onClick={() => edit(member)}
                  >
                    Edit
                  </button>
                  <button 
                    className="action-button"
                    onClick={() => remove(member.id)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
            {members.length === 0 && (
              <tr>
                <td colSpan="6" style={{ textAlign: 'center' }}>
                  No members found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default Members;
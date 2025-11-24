import React, { useEffect, useState } from 'react';
import API from '../api';

export default function BookList() {
  const [books, setBooks] = useState([]);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({ title: '', author: '', isbn: '', copies: 1 });
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchBooks();
  }, []);

  async function fetchBooks() {
    setLoading(true);
    try {
      const url = search ? `/books?search=${encodeURIComponent(search)}` : '/books';
      const response = await API.get(url);
      setBooks(response.data);
    } catch (error) {
      console.error('Error fetching books:', error);
      alert('Error fetching books');
    } finally {
      setLoading(false);
    }
  }

  async function save(e) {
    e.preventDefault();
    try {
      if (editing) {
        await API.put(`/books/${editing.id}`, form);
      } else {
        await API.post('/books', form);
      }
      await fetchBooks();
      reset();
    } catch (error) {
      console.error('Error saving book:', error);
      alert('Error saving book');
    }
  }

  function edit(book) {
    setEditing(book);
    setForm({
      title: book.title,
      author: book.author,
      isbn: book.isbn || '',
      copies: book.copies
    });
  }

  async function remove(id) {
    if (window.confirm('Are you sure you want to delete this book?')) {
      try {
        await API.delete(`/books/${id}`);
        await fetchBooks();
      } catch (error) {
        console.error('Error deleting book:', error);
        alert('Error deleting book');
      }
    }
  }

  function reset() {
    setEditing(null);
    setForm({ title: '', author: '', isbn: '', copies: 1 });
  }

  return (
    <div>
      <div className="form-container">
        <input
          type="text"
          placeholder="Search books by title..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          style={{ width: '200px', marginRight: '10px' }}
        />
        <button onClick={fetchBooks}>Search</button>
        <button onClick={() => { setSearch(''); fetchBooks(); }}>Clear</button>
      </div>

      <form onSubmit={save} className="form-container">
        <h4>{editing ? 'Edit Book' : 'Add New Book'}</h4>
        <input
          placeholder="Title *"
          value={form.title}
          onChange={e => setForm({...form, title: e.target.value})}
          required
        />
        <input
          placeholder="Author *"
          value={form.author}
          onChange={e => setForm({...form, author: e.target.value})}
          required
        />
        <input
          placeholder="ISBN"
          value={form.isbn}
          onChange={e => setForm({...form, isbn: e.target.value})}
        />
        <input
          type="number"
          placeholder="Copies *"
          value={form.copies}
          onChange={e => setForm({...form, copies: Number(e.target.value)})}
          min={0}
          required
        />
        <div>
          <button type="submit">{editing ? 'Update' : 'Add'} Book</button>
          {editing && (
            <button type="button" onClick={reset}>Cancel</button>
          )}
        </div>
      </form>

      {loading ? (
        <p>Loading books...</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Author</th>
              <th>ISBN</th>
              <th>Copies</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {books.map(book => (
              <tr key={book.id}>
                <td>{book.id}</td>
                <td>{book.title}</td>
                <td>{book.author}</td>
                <td>{book.isbn || 'N/A'}</td>
                <td>{book.copies}</td>
                <td>
                  <span style={{ 
                    color: book.copies > 0 ? 'green' : 'red',
                    fontWeight: 'bold'
                  }}>
                    {book.copies > 0 ? 'Available' : 'Out of Stock'}
                  </span>
                </td>
                <td>
                  <button 
                    className="action-button edit"
                    onClick={() => edit(book)}
                  >
                    Edit
                  </button>
                  <button 
                    className="action-button"
                    onClick={() => remove(book.id)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
            {books.length === 0 && (
              <tr>
                <td colSpan="7" style={{ textAlign: 'center' }}>
                  No books found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  );
}
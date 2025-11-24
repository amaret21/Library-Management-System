import React, { useEffect, useState } from 'react';
import API from '../../api';

const Books = () => {
  const [books, setBooks] = useState([]);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({ 
    title: '', 
    author: '', 
    isbn: '', 
    publisher: '',
    publicationYear: '',
    genre: '',
    description: '',
    totalCopies: 1 
  });
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
      setBooks(response.data.data || []);
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
      publisher: book.publisher || '',
      publicationYear: book.publicationYear || '',
      genre: book.genre || '',
      description: book.description || '',
      totalCopies: book.totalCopies
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
    setForm({ 
      title: '', 
      author: '', 
      isbn: '', 
      publisher: '',
      publicationYear: '',
      genre: '',
      description: '',
      totalCopies: 1 
    });
  }

  return (
    <div>
      <h2>Book Management</h2>
      
      <div className="form-container">
        <input
          type="text"
          placeholder="Search books by title, author, genre..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          style={{ width: '300px', marginRight: '10px', padding: '10px' }}
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
          placeholder="Publisher"
          value={form.publisher}
          onChange={e => setForm({...form, publisher: e.target.value})}
        />
        <input
          type="number"
          placeholder="Publication Year"
          value={form.publicationYear}
          onChange={e => setForm({...form, publicationYear: e.target.value})}
        />
        <input
          placeholder="Genre"
          value={form.genre}
          onChange={e => setForm({...form, genre: e.target.value})}
        />
        <textarea
          placeholder="Description"
          value={form.description}
          onChange={e => setForm({...form, description: e.target.value})}
          style={{ width: '100%', padding: '12px', marginBottom: '15px', border: '1px solid #ddd', borderRadius: '4px', minHeight: '80px' }}
        />
        <input
          type="number"
          placeholder="Total Copies *"
          value={form.totalCopies}
          onChange={e => setForm({...form, totalCopies: Number(e.target.value)})}
          min={1}
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
              <th>Genre</th>
              <th>Total Copies</th>
              <th>Available</th>
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
                <td>{book.genre || 'N/A'}</td>
                <td>{book.totalCopies}</td>
                <td>{book.availableCopies}</td>
                <td>
                  <span style={{ 
                    color: book.availableCopies > 0 ? 'green' : 'red',
                    fontWeight: 'bold'
                  }}>
                    {book.availableCopies > 0 ? 'Available' : 'Out of Stock'}
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
                <td colSpan="9" style={{ textAlign: 'center' }}>
                  No books found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default Books;
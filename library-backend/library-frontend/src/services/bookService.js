import API from './api';

class BookService {
  getAllBooks() {
    return API.get('/books');
  }

  getBookById(id) {
    return API.get(`/books/${id}`);
  }

  createBook(bookData) {
    return API.post('/books', bookData);
  }

  updateBook(id, bookData) {
    return API.put(`/books/${id}`, bookData);
  }

  deleteBook(id) {
    return API.delete(`/books/${id}`);
  }

  searchBooks(keyword) {
    return API.get(`/books/search?keyword=${encodeURIComponent(keyword)}`);
  }

  getAvailableBooks() {
    return API.get('/books/available');
  }

  updateBookCopies(id, totalCopies) {
    return API.patch(`/books/${id}/copies`, { totalCopies });
  }
}

export default new BookService();
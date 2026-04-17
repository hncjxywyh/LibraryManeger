package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.BookRequest;
import com.library.dto.PageRequest;
import com.library.entity.Book;

public interface BookService {
    Page<Book> getBooks(PageRequest request);
    Book getBookById(Long id);
    void addBook(BookRequest request);
    void updateBook(BookRequest request);
    void deleteBook(Long id);
}
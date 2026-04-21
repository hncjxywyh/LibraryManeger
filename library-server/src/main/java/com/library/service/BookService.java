package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.BookRequest;
import com.library.dto.PageRequest;
import com.library.entity.Book;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface BookService {
    Page<Book> getBooks(PageRequest request, boolean isAdmin);
    Book getBookById(Long id);
    void addBook(BookRequest request);
    void updateBook(BookRequest request);
    void deleteBook(Long id);
    Map<String, Object> importBooks(MultipartFile file) throws Exception;
    void exportBooks(HttpServletResponse response) throws Exception;
    byte[] getImportTemplate();
}
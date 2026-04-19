package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Constants;
import com.library.common.Result;
import com.library.dto.BookRequest;
import com.library.dto.PageRequest;
import com.library.entity.Book;
import com.library.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public Result<Page<Book>> getBooks(PageRequest request) {
        Page<Book> page = bookService.getBooks(request);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        return Result.success(book);
    }

    @PostMapping
    public Result<Void> addBook(@Valid @RequestBody BookRequest request, HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        bookService.addBook(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateBook(@PathVariable Long id, @RequestBody BookRequest request, HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        request.setId(id);
        bookService.updateBook(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBook(@PathVariable Long id, HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        bookService.deleteBook(id);
        return Result.success();
    }

    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws Exception {
        byte[] template = bookService.getImportTemplate();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=book_import_template.xlsx");
        response.getOutputStream().write(template);
    }

    @PostMapping("/import")
    public Result<Map<String, Object>> importBooks(@RequestParam("file") MultipartFile file, HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        try {
            Map<String, Object> result = bookService.importBooks(file);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public void exportBooks(HttpServletResponse response, HttpServletRequest httpRequest) {
        try {
            bookService.exportBooks(response);
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

    private void checkAdmin(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || !role.equals(Constants.ROLE_ADMIN)) {
            throw new RuntimeException("无权限操作");
        }
    }
}

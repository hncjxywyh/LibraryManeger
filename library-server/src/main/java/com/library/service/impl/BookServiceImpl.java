package com.library.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Constants;
import com.library.dto.BookExportDTO;
import com.library.dto.BookImportDTO;
import com.library.dto.BookRequest;
import com.library.dto.PageRequest;
import com.library.entity.Book;
import com.library.entity.BookCategory;
import com.library.entity.BorrowRecord;
import com.library.mapper.BookCategoryMapper;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BorrowRecordMapper borrowRecordMapper;
    private final BookCategoryMapper bookCategoryMapper;

    @Override
    public Page<Book> getBooks(PageRequest request) {
        Page<Book> page = new Page<>(request.getPageNum(), request.getPageSize());

        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getStatus, Constants.BOOK_STATUS_ON);

        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w.like(Book::getTitle, request.getKeyword())
                    .or().like(Book::getAuthor, request.getKeyword())
                    .or().like(Book::getIsbn, request.getKeyword()));
        }

        if (request.getCategoryId() != null) {
            wrapper.eq(Book::getCategoryId, request.getCategoryId());
        }

        wrapper.orderByDesc(Book::getCreateTime);
        Page<Book> result = bookMapper.selectPage(page, wrapper);

        return result;
    }

    @Override
    public Book getBookById(Long id) {
        Book book = bookMapper.selectById(id);
        return book;
    }

    @Override
    public void addBook(BookRequest request) {
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setPublishDate(request.getPublishDate());
        book.setCategoryId(request.getCategoryId());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
        book.setDescription(request.getDescription());
        book.setCoverUrl(request.getCoverUrl());
        book.setStatus(request.getStatus() != null ? request.getStatus() : Constants.BOOK_STATUS_ON);

        bookMapper.insert(book);
    }

    @Override
    public void updateBook(BookRequest request) {
        Book book = bookMapper.selectById(request.getId());
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setPublishDate(request.getPublishDate());
        book.setCategoryId(request.getCategoryId());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
        book.setDescription(request.getDescription());
        book.setCoverUrl(request.getCoverUrl());
        if (request.getStatus() != null) {
            book.setStatus(request.getStatus());
        }

        bookMapper.updateById(book);
    }

    @Override
    public void deleteBook(Long id) {
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getBookId, id)
                .in(BorrowRecord::getStatus, Constants.BORROW_STATUS_BORROWING, Constants.BORROW_STATUS_OVERDUE);
        long count = borrowRecordMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("图书被借阅中，无法删除");
        }
        bookMapper.deleteById(id);
    }

    @Override
    public Map<String, Object> importBooks(MultipartFile file) throws Exception {
        List<BookImportDTO> imports = EasyExcel.read(file.getInputStream())
                .head(BookImportDTO.class)
                .sheet()
                .doReadSync();

        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < imports.size(); i++) {
            BookImportDTO dto = imports.get(i);
            try {
                // 校验必填字段
                if (!StringUtils.hasText(dto.getIsbn()) || !StringUtils.hasText(dto.getTitle())) {
                    errors.add(String.format("第%d行: ISBN和书名不能为空", i + 2));
                    failCount++;
                    continue;
                }

                // 查询分类
                LambdaQueryWrapper<BookCategory> categoryWrapper = new LambdaQueryWrapper<>();
                categoryWrapper.eq(BookCategory::getName, dto.getCategoryName());
                BookCategory category = bookCategoryMapper.selectOne(categoryWrapper);
                if (category == null) {
                    errors.add(String.format("第%d行: 分类【%s】不存在", i + 2, dto.getCategoryName()));
                    failCount++;
                    continue;
                }

                // 检查是否已存在
                Book existingBook = bookMapper.selectByIsbn(dto.getIsbn());
                Book book = new Book();
                book.setIsbn(dto.getIsbn());
                book.setTitle(dto.getTitle());
                book.setAuthor(dto.getAuthor());
                book.setPublisher(dto.getPublisher());
                if (StringUtils.hasText(dto.getPublishDate())) {
                    book.setPublishDate(LocalDate.parse(dto.getPublishDate()));
                }
                book.setCategoryId(category.getId());
                if (StringUtils.hasText(dto.getPrice())) {
                    book.setPrice(new BigDecimal(dto.getPrice()));
                }
                if (StringUtils.hasText(dto.getStock())) {
                    book.setStock(Integer.parseInt(dto.getStock()));
                }
                book.setDescription(dto.getDescription());
                book.setCoverUrl(dto.getCoverUrl());
                book.setStatus(1);

                if (existingBook != null) {
                    book.setId(existingBook.getId());
                    bookMapper.updateById(book);
                } else {
                    bookMapper.insert(book);
                }
                successCount++;
            } catch (Exception e) {
                errors.add(String.format("第%d行: %s", i + 2, e.getMessage()));
                failCount++;
            }
        }

        result.put("total", imports.size());
        result.put("success", successCount);
        result.put("fail", failCount);
        result.put("errors", errors);
        return result;
    }

    @Override
    public void exportBooks(HttpServletResponse response) throws Exception {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getStatus, 1);
        List<Book> books = bookMapper.selectList(wrapper);

        List<BookExportDTO> exports = books.stream().map(book -> {
            BookExportDTO dto = new BookExportDTO();
            dto.setIsbn(book.getIsbn());
            dto.setTitle(book.getTitle());
            dto.setAuthor(book.getAuthor());
            dto.setPublisher(book.getPublisher());
            dto.setPublishDate(book.getPublishDate() != null ? book.getPublishDate().toString() : "");
            dto.setCategoryName(getCategoryName(book.getCategoryId()));
            dto.setPrice(book.getPrice() != null ? book.getPrice().toString() : "");
            dto.setStock(book.getStock());
            dto.setDescription(book.getDescription());
            dto.setStatus(BookExportDTO.getStatusName(book.getStatus()));
            return dto;
        }).collect(Collectors.toList());

        EasyExcel.write(response.getOutputStream(), BookExportDTO.class)
                .sheet("图书列表")
                .doWrite(exports);
    }

    @Override
    public byte[] getImportTemplate() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EasyExcel.write(out, BookImportDTO.class)
                .sheet("图书导入模板")
                .doWrite(List.of(new BookImportDTO()));
        return out.toByteArray();
    }

    private String getCategoryName(Long categoryId) {
        if (categoryId == null) return "";
        BookCategory category = bookCategoryMapper.selectById(categoryId);
        return category != null ? category.getName() : "";
    }
}

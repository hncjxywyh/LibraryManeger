package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Constants;
import com.library.dto.BookRequest;
import com.library.dto.PageRequest;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BorrowRecordMapper borrowRecordMapper;

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
}
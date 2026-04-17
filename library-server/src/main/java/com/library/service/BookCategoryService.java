package com.library.service;

import com.library.dto.CategoryRequest;
import com.library.entity.BookCategory;
import java.util.List;

public interface BookCategoryService {
    List<BookCategory> getCategories();
    void addCategory(CategoryRequest request);
    void updateCategory(CategoryRequest request);
    void deleteCategory(Long id);
}
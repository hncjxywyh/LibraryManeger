package com.library.controller;

import com.library.common.Constants;
import com.library.common.Result;
import com.library.dto.CategoryRequest;
import com.library.entity.BookCategory;
import com.library.service.BookCategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final BookCategoryService categoryService;

    @GetMapping
    public Result<List<BookCategory>> getCategories() {
        List<BookCategory> categories = categoryService.getCategories();
        return Result.success(categories);
    }

    @PostMapping
    public Result<Void> addCategory(@RequestBody CategoryRequest request, HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        categoryService.addCategory(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request, HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        request.setId(id);
        categoryService.updateCategory(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id, HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        categoryService.deleteCategory(id);
        return Result.success();
    }

    private void checkAdmin(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || !role.equals(Constants.ROLE_ADMIN)) {
            throw new RuntimeException("无权限操作");
        }
    }
}
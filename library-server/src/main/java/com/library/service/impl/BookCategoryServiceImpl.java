package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.dto.CategoryRequest;
import com.library.entity.BookCategory;
import com.library.mapper.BookCategoryMapper;
import com.library.service.BookCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookCategoryServiceImpl implements BookCategoryService {

    private final BookCategoryMapper categoryMapper;

    @Override
    public List<BookCategory> getCategories() {
        List<BookCategory> allCategories = categoryMapper.selectList(null);

        Map<Long, List<BookCategory>> childrenMap = allCategories.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() != 0)
                .collect(Collectors.groupingBy(BookCategory::getParentId));

        List<BookCategory> rootCategories = allCategories.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .peek(c -> c.setChildren(getChildren(c.getId(), childrenMap)))
                .collect(Collectors.toList());

        return rootCategories;
    }

    private List<BookCategory> getChildren(Long parentId, Map<Long, List<BookCategory>> childrenMap) {
        List<BookCategory> children = childrenMap.get(parentId);
        if (children == null) {
            return new ArrayList<>();
        }
        return children.stream()
                .peek(c -> c.setChildren(getChildren(c.getId(), childrenMap)))
                .collect(Collectors.toList());
    }

    @Override
    public void addCategory(CategoryRequest request) {
        BookCategory category = new BookCategory();
        category.setName(request.getName());
        category.setParentId(request.getParentId() != null ? request.getParentId() : 0);
        category.setSort(request.getSort() != null ? request.getSort() : 0);

        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(CategoryRequest request) {
        BookCategory category = categoryMapper.selectById(request.getId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        category.setName(request.getName());
        if (request.getParentId() != null) {
            category.setParentId(request.getParentId());
        }
        if (request.getSort() != null) {
            category.setSort(request.getSort());
        }

        categoryMapper.updateById(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        List<Long> idsToDelete = new ArrayList<>();
        idsToDelete.add(id);

        findChildrenIds(id, idsToDelete);

        categoryMapper.deleteBatchIds(idsToDelete);
    }

    private void findChildrenIds(Long parentId, List<Long> ids) {
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookCategory::getParentId, parentId);
        List<BookCategory> children = categoryMapper.selectList(wrapper);

        for (BookCategory child : children) {
            ids.add(child.getId());
            findChildrenIds(child.getId(), ids);
        }
    }
}
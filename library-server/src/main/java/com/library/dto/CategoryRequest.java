package com.library.dto;

import lombok.Data;

@Data
public class CategoryRequest {
    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;
}
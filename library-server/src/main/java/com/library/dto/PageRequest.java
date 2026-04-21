package com.library.dto;

import lombok.Data;

@Data
public class PageRequest {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Long categoryId;
    private Integer status;  // dedicated status filter field
}
package com.library.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookRequest {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishDate;
    private Long categoryId;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private String coverUrl;
    private Integer status;
}
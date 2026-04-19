package com.library.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BookImportDTO {

    @ExcelProperty("ISBN")
    private String isbn;

    @ExcelProperty("书名")
    private String title;

    @ExcelProperty("作者")
    private String author;

    @ExcelProperty("出版社")
    private String publisher;

    @ExcelProperty("出版日期")
    private String publishDate;

    @ExcelProperty("分类名称")
    private String categoryName;

    @ExcelProperty("价格")
    private String price;

    @ExcelProperty("库存")
    private String stock;

    @ExcelProperty("简介")
    private String description;

    @ExcelProperty("封面URL")
    private String coverUrl;
}

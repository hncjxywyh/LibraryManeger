package com.library.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BookExportDTO {

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

    @ExcelProperty("分类")
    private String categoryName;

    @ExcelProperty("价格")
    private String price;

    @ExcelProperty("库存")
    private Integer stock;

    @ExcelProperty("简介")
    private String description;

    @ExcelProperty("状态")
    private String status;

    public static String getStatusName(Integer status) {
        return status == 1 ? "上架" : "下架";
    }
}

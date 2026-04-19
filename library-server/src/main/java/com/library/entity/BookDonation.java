package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("book_donation")
public class BookDonation {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("donor_name")
    @JsonProperty("donorName")
    private String donorName;

    @TableField("donor_phone")
    @JsonProperty("contact")
    private String donorPhone;

    @TableField("donor_message")
    @JsonProperty("message")
    private String donorMessage;

    @TableField("book_title")
    @JsonProperty("bookName")
    private String bookTitle;

    @TableField("book_author")
    @JsonProperty("author")
    private String bookAuthor;

    @TableField("book_publisher")
    @JsonProperty("publisher")
    private String bookPublisher;

    private Integer quantity;

    private Integer status; // 1=待审核 2=已通过 3=已拒绝

    @TableField("review_comment")
    private String reviewComment;

    @TableField("review_time")
    private LocalDateTime reviewTime;

    @TableField("user_id")
    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

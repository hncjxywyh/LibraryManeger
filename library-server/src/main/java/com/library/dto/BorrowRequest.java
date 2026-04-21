package com.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowRequest {
    @NotNull(message = "图书ID不能为空")
    private Long bookId;
}

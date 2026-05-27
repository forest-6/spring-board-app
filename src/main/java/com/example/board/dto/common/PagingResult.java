package com.example.board.dto.common;

import java.util.List;

public record PagingResult<T>(
        int totalCount,
        int pageSize,
        int pageIndex,
        List<T> rows
) {
    public static <T> PagingResult<T> of(int totalCount, int pageSize, int pageIndex, List<T> rows) {
        return new PagingResult<>(totalCount, pageSize, pageIndex, rows);
    }
}

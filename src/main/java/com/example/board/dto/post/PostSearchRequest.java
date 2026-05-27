package com.example.board.dto.post;

public record PostSearchRequest(
        Integer pageSize,
        Integer pageIndex,
        String searchKeyword
) {
    public PostSearchRequest {
        if (pageSize == null) pageSize = 10;
        if (pageIndex == null) pageIndex = 1;
        if (searchKeyword == null) searchKeyword = "";
    }
}

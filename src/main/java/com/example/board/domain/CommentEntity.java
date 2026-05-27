package com.example.board.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentEntity {
    private Long id;
    private Long post_id;
    private Long parent_id;
    private String content;
    private int deleted_yn;
    private Long creator_id;
    private String created_at;
    private String updated_at;
}

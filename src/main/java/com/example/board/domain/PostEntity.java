package com.example.board.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostEntity {
    private Long rn;
    private Long id;
    private String title;
    private String content;
    private Long creator_id;
    private String created_at;
    private String updated_at;
}
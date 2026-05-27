package com.example.board.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class FileEntity {
    private Long id;
    private Long post_id;
    private String origin_name;
    private String stored_name;
    private String file_path;
    private Long file_size;
}
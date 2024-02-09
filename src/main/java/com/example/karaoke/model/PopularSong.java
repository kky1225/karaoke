package com.example.karaoke.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PopularSong {
    private String rank; // 순위
    private String no; // 곡 번호
    private String title; // 제목
    private String singer; // 가수
}

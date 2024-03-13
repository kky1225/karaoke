package com.example.karaoke.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PopularSong {
    private String ranking; // 순위
    private String no; // 곡 번호
    private String title; // 제목
    private String singer; // 가수
    private String category; // 카테고리
    private String year; // 년
    private String month; // 월
}

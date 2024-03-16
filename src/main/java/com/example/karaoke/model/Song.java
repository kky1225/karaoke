package com.example.karaoke.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Song {
    private String no; // 곡 번호
    private String title; // 곡 제목
    private String singer; // 가수
    private String lyrics; // 작사가
    private String music; // 작곡가
}

package com.example.karaoke.dto;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class SongDTO {
    public String no; // 곡 번호
    public String title; // 곡 제목
    public String singer; // 가수
    public String lyrics; // 작사
    public String music; // 작곡
}

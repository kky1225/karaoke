package com.example.karaoke.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PopularSongRes {
    private String rank;
    private String no;
    private String title;
    private String singer;
}

package com.example.karaoke.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class TJMedia {

    @Getter
    @Setter
    @ToString
    public static class SearchSong {
        @NotBlank
        private String category;
        @NotBlank
        private String keyword;
        private Integer page;
    }

    @Getter
    @Builder
    @ToString
    public static class SearchPopularSong {
        @NotBlank
        private String category;
        @NotBlank
        private String year;
        @NotBlank
        private String month;
    }
}

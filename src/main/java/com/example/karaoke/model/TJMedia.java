package com.example.karaoke.model;

import jakarta.validation.constraints.NotBlank;
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
    @Setter
    @ToString
    public static class PopularSong {
        private String category;
        private String startYear;
        private String startMonth;
        private String endYear;
        private String endMonth;
    }
}

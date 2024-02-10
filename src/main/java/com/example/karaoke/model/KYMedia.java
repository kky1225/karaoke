package com.example.karaoke.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class KYMedia {

    @Getter
    @Setter
    @ToString
    public static class searchSong {
        @NotBlank
        public String category;
        @NotBlank
        public String keyword;
        public Integer page;
    }
}

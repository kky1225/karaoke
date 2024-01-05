package com.example.karaoke.dto;

import jakarta.validation.constraints.NotBlank;

public class TJSearchDTO {
    @NotBlank
    public String category;
    @NotBlank
    public String keyword;
    public Integer page;
}

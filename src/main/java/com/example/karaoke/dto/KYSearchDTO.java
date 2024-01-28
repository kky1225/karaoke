package com.example.karaoke.dto;

import jakarta.validation.constraints.NotBlank;

public class KYSearchDTO {
    @NotBlank
    public String category;
    @NotBlank
    public String keyword;
    public Integer page;
}
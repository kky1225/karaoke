package com.example.karaoke.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/tj")
public class TJMediaController {

    @GetMapping("searchMusic")
    public String searchMusic() {
        return "hello";
    }

}

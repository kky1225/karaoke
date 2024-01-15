package com.example.karaoke.controller;

import com.example.karaoke.dto.SongDTO;
import com.example.karaoke.dto.TJSearchDTO;
import com.example.karaoke.service.TJMediaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("v1/api/tj")
public class TJMediaController {

    @Autowired
    TJMediaService tjMediaService;

    @GetMapping("searchSong")
    public List<SongDTO> searchSong(@RequestBody @Valid TJSearchDTO tjSearchDTO) {
        log.debug("tjSearchDTO : [{}]", tjSearchDTO);

        if(tjSearchDTO.page == null) {
            return tjMediaService.searchSong(tjSearchDTO.category, tjSearchDTO.keyword);
        }

        return tjMediaService.searchSong(tjSearchDTO.category, tjSearchDTO.keyword, tjSearchDTO.page);
    }

}

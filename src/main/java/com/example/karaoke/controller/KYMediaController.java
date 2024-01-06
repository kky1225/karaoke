package com.example.karaoke.controller;

import com.example.karaoke.dto.KYSearchDTO;
import com.example.karaoke.dto.SongDTO;
import com.example.karaoke.service.KYMediaService;
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
@RequestMapping("v1/api/ky")
public class KYMediaController {

    @Autowired
    KYMediaService kyMediaService;

    @GetMapping("searchSong")
    public List<SongDTO> searchSong(@RequestBody @Valid KYSearchDTO kySearchDTO) {
        log.debug("kySearchDTO : [{}]", kySearchDTO);

        return kyMediaService.searchSong(kySearchDTO.category, kySearchDTO.keyword, kySearchDTO.page);
    }
}

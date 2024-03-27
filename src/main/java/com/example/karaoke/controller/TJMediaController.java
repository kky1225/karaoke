package com.example.karaoke.controller;

import com.example.karaoke.model.PopularSong;
import com.example.karaoke.model.Song;
import com.example.karaoke.model.TJMedia;
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
    public List<Song> searchSong(@RequestBody @Valid TJMedia.SearchSong searchSong) {
        log.debug("searchSong : [{}]", searchSong);

        return tjMediaService.searchSong(searchSong);
    }

    @GetMapping("totalSong")
    public void totalSong() {
        tjMediaService.totalSong();
    }

    @GetMapping("popularSong")
    public List<PopularSong> popularSong(@RequestBody @Valid TJMedia.SearchPopularSong popularSong) {
        log.debug("popularSong : [{}]", popularSong);

        return tjMediaService.popularSong(popularSong);
    }

    @GetMapping("newSong")
    public List<Song> newSong() {
        return tjMediaService.newSong();
    }
}

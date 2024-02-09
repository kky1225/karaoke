package com.example.karaoke.controller;

import com.example.karaoke.model.PopularSong;
import com.example.karaoke.model.SearchSong;
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
    public List<SearchSong> searchSong(@RequestBody @Valid TJMedia.SearchSong searchSong) {
        log.debug("searchSong : [{}]", searchSong);

        if(searchSong.getPage() == null) {
            return tjMediaService.searchSong(searchSong.getCategory(), searchSong.getKeyword());
        }

        return tjMediaService.searchSong(searchSong.getCategory(), searchSong.getKeyword(), searchSong.getPage());
    }

    @GetMapping("popularSong")
    public List<PopularSong> popularSong(@RequestBody @Valid TJMedia.PopularSong popularSong) {
        log.debug("popularSong : [{}]", popularSong);

        return tjMediaService.popularSong(popularSong);
    }

    @GetMapping("newSong")
    public List<SearchSong> newSong() {
        return tjMediaService.newSong();
    }
}

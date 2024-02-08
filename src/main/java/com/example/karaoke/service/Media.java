package com.example.karaoke.service;

import com.example.karaoke.model.SearchSongRes;

import java.util.List;

public interface Media {

    List<SearchSongRes> searchSong(String category, String keyword, Integer page);

    List<SearchSongRes> searchSong(String category, String keyword);

}

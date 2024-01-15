package com.example.karaoke.service;

import com.example.karaoke.dto.SongDTO;

import java.util.List;

public interface Media {

    List<SongDTO> searchSong(String category, String keyword, Integer page);

    List<SongDTO> searchSong(String category, String keyword);

}

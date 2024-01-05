package com.example.karaoke.service;

import com.example.karaoke.dto.SongDTO;
import com.example.karaoke.dto.TJSearchDTO;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TJMediaService {

    final static String TJ_MEDIA_URL = "https://www.tjmedia.com/tjsong/song_search_list.asp"; // strCond=0&strType=1&strText=노을&intPage=4

    public List<SongDTO> searchSong(TJSearchDTO tjSearchDTO) {
        List<SongDTO> list = new ArrayList<>();

        try {
            StringBuilder option = new StringBuilder("?strCond=0");

            if(tjSearchDTO.category.equals("title")) {
                option.append("&strType=1");
            }else if(tjSearchDTO.category.equals("singer")) {
                option.append("&strType=2");
            }

            option.append("&strText=").append(tjSearchDTO.keyword).append("&intPage=").append(tjSearchDTO.page);

            Document doc = Jsoup.connect(TJ_MEDIA_URL + option).get();

            Elements elements = doc.select("form > div#BoardType1 > table.board_type1 > tbody > tr");

            if(!elements.isEmpty()) {
                elements.remove(0);
            }

            for(Element e : elements) {
                SongDTO song = new SongDTO();
                song.no = Integer.valueOf(e.child(0).text());
                song.title = e.child(1).text();
                song.singer = e.child(2).text();
                song.lyrics = e.child(3).text();
                song.music = e.child(4).text();

                list.add(song);
            }
        }catch (Exception e) {
            System.out.println("파싱 중 오류 발생!");
        }

        return list;
    }

}

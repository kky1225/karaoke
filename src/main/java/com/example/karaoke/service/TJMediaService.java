package com.example.karaoke.service;

import com.example.karaoke.dto.SongDTO;
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
public class TJMediaService implements Media {

    final static String TJ_MEDIA_URL = "https://www.tjmedia.com/tjsong/song_search_list.asp";

    @Override
    public List<SongDTO> searchSong(String category, String keyword, Integer page) {
        List<SongDTO> list = new ArrayList<>();

        try {
            StringBuilder option = new StringBuilder("?strCond=0");

            if(category.equals("title")) {
                option.append("&strType=1");
            }else if(category.equals("singer")) {
                option.append("&strType=2");
            }

            option.append("&strText=").append(keyword).append("&intPage=").append(page);

            Document doc = Jsoup.connect(TJ_MEDIA_URL + option).get();

            Elements elements = doc.select("table.board_type1 > tbody > tr");

            if(!elements.isEmpty()) {
                elements.remove(0);
            }

            for(Element e : elements) {
                list.add(
                        SongDTO.builder()
                                .no(e.child(0).html())
                                .title(e.child(1).text())
                                .singer(e.child(2).text())
                                .lyrics(e.child(3).text())
                                .music(e.child(4).text())
                                .build()
                );
            }
        }catch (Exception e) {
            System.out.println("파싱 중 오류 발생!");
        }

        return list;
    }

    //TODO 시간이 너무 오래 걸리는 문제 (가수, 먼데이키즈 기준 평균 12초)
    @Override
    public List<SongDTO> searchSong(String category, String keyword) {
        List<SongDTO> list = new ArrayList<>();

        try {
            StringBuilder option = getUrlParam(category, keyword);

            int pageNo = 1;

            while (true) {
                Document doc = Jsoup.connect(TJ_MEDIA_URL + option + pageNo++).get();

                Elements elements = doc.select("table.board_type1 > tbody > tr:not(:first-child)");

                if(elements.get(0).childrenSize() == 1) {
                    break;
                }

                for(Element e : elements) {
                    list.add(SongDTO.builder()
                                    .no(e.child(0).html())
                                    .title(e.child(1).text())
                                    .singer(e.child(2).text())
                                    .lyrics(e.child(3).html())
                                    .music(e.child(4).html())
                                    .build()
                    );
                }

                if(elements.size() < 5000) {
                    break;
                }
            }
        }catch (Exception e) {
            System.out.println("파싱 중 오류 발생!");
        }

        return list;
    }

    private StringBuilder getUrlParam(String category, String keyword) {
        StringBuilder option = new StringBuilder("?strType=");

        String type = "";

        if(category.equals("title")) {
            type = "1";
        }else if(category.equals("singer")) {
            type = "2";
        }

        option.append(type).append("&strSize0").append(type).append("=5000&natType=").append("&strText=").append(keyword).append("&strCond=0&searchOrderType=up&searchOrderItem=index_title&intPage=");

        return option;
    }

}

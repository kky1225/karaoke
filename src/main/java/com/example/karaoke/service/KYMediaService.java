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
public class KYMediaService implements Media {

    final static String KY_MEDIA_URL = "https://kysing.kr/search"; // /?category=2&keyword=노을&s_page=4

    @Override
    public List<SongDTO> searchSong(String category, String keyword, Integer page) {
        List<SongDTO> list = new ArrayList<>();

        try {
            StringBuilder option = new StringBuilder();

            if(category.equals("title")) {
                option.append("/?category=2");
            }else if(category.equals("singer")) {
                option.append("/?category=7");
            }

            option.append("&keyword=").append(keyword).append("&s_page=").append(page);

            Document doc = Jsoup.connect(KY_MEDIA_URL + option).get();

            Elements elements = doc.select("div.search_daily_chart_wrap > ul.search_chart_list.clear");

            if(!elements.isEmpty()) {
                elements.remove(0);
            }

            for(Element e : elements) {
                SongDTO song = new SongDTO();
                song.no = Integer.valueOf(e.child(1).text());
                song.title = e.child(2).select("span").get(0).text();
                song.singer = e.child(3).text();
                song.music = e.child(4).text();
                song.lyrics = e.child(6).text();

                list.add(song);
            }
        }catch (Exception e) {
            System.out.println("파싱 중 오류 발생!");
        }

        return list;
    }

    //TODO 시간이 너무 오래 걸리는 문제 (가수, 먼데이키즈 기준 평균 45초)
    @Override
    public List<SongDTO> searchSong(String category, String keyword) {
        List<SongDTO> list = new ArrayList<>();

        try {
            StringBuilder option = new StringBuilder();

            if(category.equals("title")) {
                option.append("/?category=2");
            }else if(category.equals("singer")) {
                option.append("/?category=7");
            }

            int pageNo = 1;

            option.append("&keyword=").append(keyword).append("&s_page=");

            while (true) {
                Document doc = Jsoup.connect(KY_MEDIA_URL + option + pageNo).get();

                Elements elements = doc.select("div.search_daily_chart_wrap > ul.search_chart_list.clear");

                if(elements.isEmpty()) {
                    break;
                }

                elements.remove(0);

                for(Element e : elements) {
                    SongDTO song = new SongDTO();
                    song.no = Integer.valueOf(e.child(1).text());
                    song.title = e.child(2).select("span").get(0).text();
                    song.singer = e.child(3).text();
                    song.music = e.child(4).text();
                    song.lyrics = e.child(6).text();

                    log.debug("song : [{}]", song);

                    list.add(song);
                }

                pageNo++;
            }
        }catch (Exception e) {
            System.out.println("파싱 중 오류 발생!");
        }

        return list;
    }

}

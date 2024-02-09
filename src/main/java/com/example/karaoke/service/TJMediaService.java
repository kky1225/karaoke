package com.example.karaoke.service;

import com.example.karaoke.model.PopularSong;
import com.example.karaoke.model.SearchSong;
import com.example.karaoke.model.TJMedia;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TJMediaService {
    @Value("${tj.base-url}")
    private String TJ_MEDIA_URL;

    public List<SearchSong> searchSong(String category, String keyword, Integer page) {
        List<SearchSong> list = new ArrayList<>();

        try {
            StringBuilder option = new StringBuilder("?strCond=0");

            if(category.equals("title")) {
                option.append("&strType=1");
            }else if(category.equals("singer")) {
                option.append("&strType=2");
            }

            option.append("&strText=").append(keyword)
                    .append("&intPage=").append(page);

            Document doc = Jsoup.connect(TJ_MEDIA_URL + "/tjsong/song_search_list.asp" + option).get();

            Elements elements = doc.select("table.board_type1 > tbody > tr");

            if(!elements.isEmpty()) {
                elements.remove(0);
            }

            for(Element e : elements) {
                list.add(
                        SearchSong.builder()
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

    public List<SearchSong> searchSong(String category, String keyword) {
        List<SearchSong> list = new ArrayList<>();

        try {
            StringBuilder option = getUrlParam(category, keyword);

            int pageNo = 1;

            while (true) {
                Document doc = Jsoup.connect(TJ_MEDIA_URL + "/tjsong/song_search_list.asp" + option + pageNo++).get();

                Elements elements = doc.select("table.board_type1 > tbody > tr:not(:first-child)");

                if(elements.get(0).childrenSize() == 1) {
                    break;
                }

                for(Element e : elements) {
                    list.add(SearchSong.builder()
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

    public List<PopularSong> popularSong(TJMedia.PopularSong popularSong) {
        List<PopularSong> list = new ArrayList<>();

        try {
            StringBuilder option = getPopularParam(popularSong);

            Document doc = Jsoup.connect(TJ_MEDIA_URL + "/tjsong/song_monthPopular.asp" + option).get();

            Elements elements = doc.select("table.board_type1 > tbody > tr:not(:first-child)");

            for(Element e : elements) {
                list.add(PopularSong.builder()
                        .rank(e.child(0).html())
                        .no(e.child(1).html())
                        .title(e.child(2).text())
                        .singer(e.child(3).html())
                        .build()
                );
            }
        }catch (Exception e) {
            System.out.println("파싱 중 오류 발생!");
        }

        return list;
    }

    public List<SearchSong> newSong() {
        List<SearchSong> list = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(TJ_MEDIA_URL + "/tjsong/song_monthNew.asp").get();

            Elements elements = doc.select("table.board_type1 > tbody > tr:not(:first-child)");

            for(Element e : elements) {
                list.add(
                  SearchSong.builder()
                          .no(e.child(0).html())
                          .title(e.child(1).html())
                          .singer(e.child(2).html())
                          .lyrics(e.child(3).html())
                          .music(e.child(4).html())
                          .build()
                );
            }
        }catch (Exception e) {
            System.out.println("파싱 중 오류 발생!");
        }

        return list;
    }

    private StringBuilder getUrlParam(String category, String keyword) {
        StringBuilder option = new StringBuilder();

        String type = "";

        if(category.equals("title")) {
            type = "1";
        }else if(category.equals("singer")) {
            type = "2";
        }

        return option.append("?strType=").append(type)
                .append("&strSize0").append(type).append("=5000")
                .append("&natType=")
                .append("&strText=").append(keyword)
                .append("&strCond=0&searchOrderType=up&searchOrderItem=index_title&intPage=");
    }

    private StringBuilder getPopularParam(TJMedia.PopularSong popularSong) {
        StringBuilder option = new StringBuilder();

        return option.append("?strType=").append(popularSong.getCategory())
                .append("&SYY=").append(popularSong.getStartYear())
                .append("&SMM=").append(popularSong.getStartMonth())
                .append("&EYY=").append(popularSong.getEndYear())
                .append("&EMM=").append(popularSong.getEndMonth());
    }

}

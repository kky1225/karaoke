package com.example.karaoke.service;

import com.example.karaoke.mapper.TJMediaMapper;
import com.example.karaoke.model.PopularSong;
import com.example.karaoke.model.SearchSong;
import com.example.karaoke.model.TJMedia;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TJMediaService {
    @Value("${tj.base-url}")
    private String TJ_MEDIA_URL;

    @Autowired
    TJMediaMapper tjMediaMapper;

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

    public List<SearchSong> newSong() {
        return tjMediaMapper.getNewSong();
    }

    public List<PopularSong> popularSong(TJMedia.SearchPopularSong searchPopularSong) {
        return tjMediaMapper.getPopularSong(searchPopularSong);
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

    @Scheduled(cron = "10 0 0 * * *" )
    public void newSongScheduling() {
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

            List<String> songNoList = tjMediaMapper.getNewSong().stream().map(SearchSong::getNo).toList();

            if(!songNoList.isEmpty()) {
                list = list.stream().filter(song -> !songNoList.contains(song.getNo())).toList();
            }

            for(SearchSong song : list) {
                tjMediaMapper.insertNewSong(song);
            }
        }catch (Exception e) {
            System.out.println("TJ Media new song Error");
        }
    }

    @Scheduled(cron = "0 0 0 1 * *" )
    public void SchedulingMonth() {
        newSongClear();
        popularSongClear();
    }

    public void newSongClear() {
        List<SearchSong> list = tjMediaMapper.getNewSong();

        for(SearchSong song : list) {
            tjMediaMapper.insertSong(song);
        }

        tjMediaMapper.deleteNewSong();
    }

    public void popularSongClear() {
        deleteOldPopularSong();
        insertNewPopularSong();
    }

    public void deleteOldPopularSong() {
        LocalDate now = LocalDate.now();

        int year = now.getYear();
        int month = now.getMonthValue();

        tjMediaMapper.deletePopularSong(String.valueOf(year - 2), month < 10 ? "0" + month : String.valueOf(month));
    }

    public void insertNewPopularSong() {
        LocalDate now = LocalDate.now();

        String year = String.valueOf(now.getYear());
        String month = now.getMonthValue() < 10 ? "0" + now.getMonthValue() : String.valueOf(now.getMonthValue());

        List<PopularSong> popularList = new ArrayList<>();

        List<String> categoryList = new ArrayList();
        categoryList.add("가요");
        categoryList.add("POP");
        categoryList.add("J-POP");

        try {
            for(String category : categoryList) {
                StringBuilder option = getPopularParam(category, year, month);

                Document doc = Jsoup.connect(TJ_MEDIA_URL + "/tjsong/song_monthPopular.asp" + option).get();

                Elements elements = doc.select("table.board_type1 > tbody > tr:not(:first-child)");

                for(Element e : elements) {
                    popularList.add(PopularSong.builder()
                            .ranking(e.child(0).html())
                            .no(e.child(1).html())
                            .title(e.child(2).text())
                            .singer(e.child(3).html())
                            .category(category)
                            .year(year)
                            .month(month)
                            .build()
                    );
                }
            }
        }catch (Exception e) {
            System.out.println("파싱 중 오류 발생!");
        }

        for(PopularSong song : popularList) {
            tjMediaMapper.insertPopularSong(song);
        }
    }

    private StringBuilder getPopularParam(String category, String year, String month) {
        StringBuilder option = new StringBuilder();

        switch (category) {
            case "가요":
                option.append("?strType=1");
                break;
            case "POP":
                option.append("?strType=2");
                break;
            case "J-POP":
                option.append("?strType=3");
                break;
        }

        return option.append("&SYY=").append(year)
                .append("&SMM=").append(month)
                .append("&EYY=").append(year)
                .append("&EMM=").append(month);
    }
}

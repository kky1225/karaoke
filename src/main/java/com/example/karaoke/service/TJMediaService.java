package com.example.karaoke.service;

import com.example.karaoke.mapper.TJMediaMapper;
import com.example.karaoke.model.PopularSong;
import com.example.karaoke.model.Song;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@Transactional
public class TJMediaService {
    @Value("${tj.base-url}")
    private String TJ_MEDIA_URL;

    @Autowired
    TJMediaMapper tjMediaMapper;

    public List<Song> searchSong(String category, String keyword, Integer page) {
        List<Song> list = new ArrayList<>();

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
                        Song.builder()
                                .no(e.child(0).html())
                                .title(e.child(1).text())
                                .singer(e.child(2).text())
                                .lyrics(e.child(3).text())
                                .music(e.child(4).text())
                                .build()
                );
            }
        }catch (Exception e) {
            log.debug("파싱 중 오류 발생!");
        }

        return list;
    }

    public List<Song> searchSong(String category, String keyword) {
        List<Song> list = new ArrayList<>();

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
                    list.add(Song.builder()
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
            log.debug("파싱 중 오류 발생!");
        }

        return list;
    }

    public void totalSong() {
        StringBuilder option = new StringBuilder("?strType=16&natType=&strCond=0&strSize05=100000&strText=");

        List<Song> list = new ArrayList<>();
        List<String> noList = new ArrayList<>();

        for(int i=1;i<=9;i++) {
            log.debug("{}", TJ_MEDIA_URL + "/tjsong/song_search_list.asp" + option + i);

            try {
                Document doc = Jsoup.connect(TJ_MEDIA_URL + "/tjsong/song_search_list.asp" + option + i).timeout(300000).maxBodySize(0).get();

                Elements elements = doc.select("table.board_type1 > tbody > tr:not(:first-child)");

                for(Element e : elements) {
                    log.debug("{}", e);

                    if(!noList.contains(e.child(0).text())) {
                        noList.add(e.child(0).text());

                        list.add(Song.builder()
                                .no(e.child(0).text())
                                .title(e.child(1).text())
                                .singer(e.child(2).text())
                                .lyrics(e.child(3).text())
                                .music(e.child(4).text())
                                .build()
                        );
                    }
                }

            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        log.debug("list의 총 개수 : {}", list.size());

        list = list.stream().sorted(Comparator.comparing(Song::getNo)).toList();

        for(Song song : list) {
            tjMediaMapper.insertSong(song);
        }
    }

    public List<Song> newSong() {
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
    public void daySchedule() {
        List<Song> list = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(TJ_MEDIA_URL + "/tjsong/song_monthNew.asp").get();

            Elements elements = doc.select("table.board_type1 > tbody > tr:not(:first-child)");

            for(Element e : elements) {
                list.add(
                        Song.builder()
                                .no(e.child(0).html())
                                .title(e.child(1).html())
                                .singer(e.child(2).html())
                                .lyrics(e.child(3).html())
                                .music(e.child(4).html())
                                .build()
                );
            }

            // 기존 신곡 목록에서 곡 번호를 추출
            List<String> songNoList = tjMediaMapper.getNewSong().stream().map(Song::getNo).toList();

            // 기존에 가지고 있지 않은 곡 정보만 추출
            if(!songNoList.isEmpty()) {
                list = list.stream().filter(song -> !songNoList.contains(song.getNo())).toList();
            }

            for(Song song : list) {
                // 이달의 신곡 DB에 저장
                tjMediaMapper.insertNewSong(song);
                // 전체 곡 DB에 저장
                tjMediaMapper.insertSong(song);
            }
        }catch (Exception e) {
            log.debug("TJ Media daySchedule Error");

            throw new RuntimeException(e);
        }
    }

    @Scheduled(cron = "0 0 0 1 * *" )
    public void monthSchedule() {
        try {
            // 이달의 신곡 DB에서 삭제
            tjMediaMapper.deleteNewSong();

            popularSongClear();
        }catch (Exception e) {
            log.debug("TJ Media monthSchedule Error");

            throw new RuntimeException(e);
        }
    }

    public void popularSongClear() {
        deleteOldPopularSong();
        insertNewPopularSong();
    }

    public void deleteOldPopularSong() {
        LocalDate now = LocalDate.now();

        int year = now.getYear();
        int month = now.getMonthValue();

        // 현재 날짜 기준 2년 전의 노래방 인기곡 삭제
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

            // 노래방 인기곡 DB에 저장
            for(PopularSong song : popularList) {
                tjMediaMapper.insertPopularSong(song);
            }
        }catch (Exception e) {
            log.debug("TJ Media insertNewPopularSong Error");

            throw new RuntimeException(e);
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

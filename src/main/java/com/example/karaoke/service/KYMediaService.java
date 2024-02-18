package com.example.karaoke.service;

import com.example.karaoke.model.SearchSong;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class KYMediaService {

    @Value("${ky.base-url}")
    private String KY_MEDIA_URL;

    public List<SearchSong> searchSong(String category, String keyword, Integer page) {
        List<SearchSong> list = new ArrayList<>();

        try {
            StringBuilder option = getUrlParam(category, keyword);
            option.append("&s_page=").append(page);

            Document doc = Jsoup.connect(KY_MEDIA_URL + option).get();

            Elements elements = doc.select("div.search_daily_chart_wrap > ul.search_chart_list.clear:not(:first-child)");

            for(Element e : elements) {
                list.add(
                        SearchSong.builder()
                                .no(e.child(1).html())
                                .title(e.child(2).select("span").get(0).text())
                                .singer(e.child(3).html())
                                .lyrics(e.child(4).html())
                                .music(e.child(5).html())
                                .build()
                );
            }
        }catch (Exception e) {
            System.out.println("파싱 중 오류 발생!");
        }

        return list;
    }

    //TODO 시간이 너무 오래 걸리는 문제 (가수, 먼데이키즈 기준 평균 9초)
    public List<SearchSong> searchSong(String category, String keyword) {
        List<SearchSong> list = new ArrayList<>();

        try {
            StringBuilder option = getUrlParam(category, keyword);
            option.append("&s_page=");

            int totalPage = getTotalPage(option);

            log.debug("totalPage : {}", totalPage);

            List<String> urlList = new ArrayList<>();

            for(int i=1;i<=totalPage;i++) {
                urlList.add(KY_MEDIA_URL + option + i);
            }

            urlList.stream().parallel().forEach(url -> {
                try {
                    Document doc2 = Jsoup.connect(url).get();

                    Elements elements = doc2.select("div.search_daily_chart_wrap > ul.search_chart_list.clear:not(:first-child)");

                    if(elements.get(0).childrenSize() > 0) {
                        for(Element e : elements) {
                            list.add(
                                    SearchSong.builder()
                                            .no(e.select("li.search_chart_num").html())
                                            .title(e.select("span.tit").get(0).html())
                                            .singer(e.select("li.search_chart_sng").html())
                                            .lyrics(e.select("li.search_chart_cmp").html())
                                            .music(e.select("li.search_chart_wrt").html())
                                            .build()
                            );
                        }
                    }
                } catch (Exception e) {
                    System.out.println("파싱 중 오류 발생!");
                }
            });

            /*
            while (true) {
                Document doc = Jsoup.connect(KY_MEDIA_URL + option + pageNo++).get();

                Elements elements = doc.select("div.search_daily_chart_wrap > ul.search_chart_list.clear:not(:first-child)");

                if(elements.get(0).childrenSize() == 1) {
                    break;
                }

                for(Element e : elements) {
                    list.add(
                            SearchSong.builder()
                                    .no(e.select("li.search_chart_num").html())
                                    .title(e.select("span.tit").get(0).html())
                                    .singer(e.select("li.search_chart_sng").html())
                                    .lyrics(e.select("li.search_chart_cmp").html())
                                    .music(e.select("li.search_chart_wrt").html())
                                    .build()
                    );
                }

                if(elements.size() < 15) {
                    break;
                }
            }
             */
        }catch (Exception e) {
            System.out.println("파싱 중 오류 발생!");
        }

        return list.stream().sorted(new Comparator<SearchSong>() {
            @Override
            public int compare(SearchSong o1, SearchSong o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        }).toList();
    }

    private StringBuilder getUrlParam(String category, String keyword) {
        StringBuilder option = new StringBuilder();

        String type = "";

        if(category.equals("title")) {
            type = "2";
        }else if(category.equals("singer")) {
            type = "7";
        }

        return option.append("/?category=").append(type)
                .append("&keyword=").append(keyword);
    }

    private int getTotalPage(StringBuilder option) {
        int totalPage = 0;

        try {
            int pageNo = 1;

            Document doc = Jsoup.connect(KY_MEDIA_URL + option + pageNo++).get();

            int total = Integer.parseInt(doc.select("p.search_tit > span").text());

            log.debug("total : {}", total);

            totalPage = total / 15;

            if(total % 15 > 0) {
                totalPage++;
            }
        }catch (Exception e) {
            log.debug("totalPage Error");
        }

        return totalPage;
    }
}


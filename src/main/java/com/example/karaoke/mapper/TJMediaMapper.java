package com.example.karaoke.mapper;

import com.example.karaoke.model.PopularSong;
import com.example.karaoke.model.Song;
import com.example.karaoke.model.TJMedia;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TJMediaMapper {

    @Select({
            "SELECT *",
            "FROM tj_new_song"
    })
    List<Song> getNewSong();

    @Insert({
            "INSERT INTO tj_new_song(no, title, singer, lyrics, music)",
            "VALUES (#{no}, #{title}, #{singer}, #{lyrics}, #{music})"
    })
    void insertNewSong(Song song);

    @Delete({
            "DELETE FROM tj_new_song"
    })
    void deleteNewSong();

    @Insert({
            "INSERT INTO tj_song(no, title, singer, lyrics, music)",
            "VALUES (#{no}, #{title}, #{singer}, #{lyrics}, #{music})"
    })
    void insertSong(Song song);

    @Select({
            "SELECT *",
            "FROM tj_popular_song",
            "WHERE category = #{category} AND year = #{year} AND month = #{month}"
    })
    List<PopularSong> getPopularSong(TJMedia.SearchPopularSong popularSong);

    @Select({
            "INSERT INTO tj_popular_song(ranking, no, title, singer, category, year, month)",
            "VALUES (#{ranking}, #{no}, #{title}, #{singer}, #{category}, #{year}, #{month})"
    })
    List<PopularSong> insertPopularSong(PopularSong popularSong);

    @Delete({
            "DELETE FROM tj_popular_song",
            "WHERE year = #{year} AND month = #{month}"
    })
    void deletePopularSong(String year, String month);

}

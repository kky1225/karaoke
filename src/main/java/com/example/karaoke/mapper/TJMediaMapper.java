package com.example.karaoke.mapper;

import com.example.karaoke.model.SearchSong;
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
    List<SearchSong> getNewSong();

    @Insert({
            "INSERT INTO tj_new_song(no, title, singer, lyrics, music)",
            "VALUES (#{no}, #{title}, #{singer}, #{lyrics}, #{music})"
    })
    void insertNewSong(SearchSong song);

    @Delete({
            "DELETE FROM tj_new_song"
    })
    void deleteNewSong();

    @Insert({
            "INSERT INTO tj_song(no, title, singer, lyrics, music)",
            "VALUES (#{no}, #{title}, #{singer}, #{lyrics}, #{music})"
    })
    void insertSong(SearchSong song);

}

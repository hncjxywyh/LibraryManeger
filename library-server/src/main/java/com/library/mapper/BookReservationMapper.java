package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BookReservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BookReservationMapper extends BaseMapper<BookReservation> {

    @Select("SELECT COALESCE(MAX(br.position), 0) + 1 FROM book_reservation br WHERE br.book_id = #{bookId} AND br.status = #{status} FOR UPDATE")
    Integer getNextPositionForUpdate(Long bookId, Integer status);
}
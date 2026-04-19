package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BookReservation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookReservationMapper extends BaseMapper<BookReservation> {
}
package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

    @Select("SELECT * FROM book WHERE id = #{id} FOR UPDATE")
    Book selectForUpdate(Long id);

    @Select("SELECT * FROM book WHERE isbn = #{isbn}")
    Book selectByIsbn(@Param("isbn") String isbn);
}

package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {

    @Select("""
        SELECT bc.name as categoryName, COUNT(*) as count
        FROM borrow_record br
        JOIN book b ON br.book_id = b.id
        JOIN book_category bc ON b.category_id = bc.id
        WHERE br.user_id = #{userId}
        GROUP BY bc.name
        ORDER BY count DESC
        """)
    List<Map<String, Object>> getCategoryStats(Long userId);

    @Select("""
        SELECT DATE_FORMAT(br.borrow_date, '%Y-%m') as month, COUNT(*) as count
        FROM borrow_record br
        WHERE br.user_id = #{userId}
        GROUP BY DATE_FORMAT(br.borrow_date, '%Y-%m')
        ORDER BY month DESC
        LIMIT 12
        """)
    List<Map<String, Object>> getMonthlyStats(Long userId);

    @Select("""
        SELECT b.title, b.author, COUNT(*) as borrowCount
        FROM borrow_record br
        JOIN book b ON br.book_id = b.id
        WHERE br.user_id = #{userId}
        GROUP BY b.id
        ORDER BY borrowCount DESC
        LIMIT 10
        """)
    List<Map<String, Object>> getTopBooks(Long userId);
}

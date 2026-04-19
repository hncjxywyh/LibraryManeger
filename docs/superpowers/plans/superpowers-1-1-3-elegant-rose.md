# 图书馆管理系统 - 第一阶段功能扩展实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为图书馆管理系统实现三个第一阶段功能：消息通知（站内信+定时提醒）、批量导入图书（Excel）、用户借阅统计

**Architecture:**
- 后端采用 Spring Boot 定时任务 + MyBatis-Plus 实现站内消息和借阅提醒
- 前端在现有 Vue3 + Element Plus 基础上添加消息中心页面和借阅统计图表
- 批量导入使用阿里巴巴 EasyExcel 处理 Excel 文件上传

**Tech Stack:**
- Backend: Spring Boot 3.2, MyBatis-Plus 3.5.5, EasyExcel 3.x, Spring Scheduling
- Frontend: Vue 3, Element Plus 2.5, ECharts 5, vue-echarts

---

## 文件结构概览

### 后端 (library-server)

**新增文件：**
- `src/main/java/com/library/entity/UserMessage.java` - 站内消息实体
- `src/main/java/com/library/entity/BorrowRemindLog.java` - 提醒日志实体
- `src/main/java/com/library/mapper/UserMessageMapper.java` - 消息 Mapper
- `src/main/java/com/library/mapper/BorrowRemindLogMapper.java` - 提醒日志 Mapper
- `src/main/java/com/library/service/UserMessageService.java` - 消息服务接口
- `src/main/java/com/library/service/impl/UserMessageServiceImpl.java` - 消息服务实现
- `src/main/java/com/library/service/BorrowRemindService.java` - 提醒服务接口
- `src/main/java/com/library/service/impl/BorrowRemindServiceImpl.java` - 提醒服务实现
- `src/main/java/com/library/task/BorrowRemindTask.java` - 定时提醒任务
- `src/main/java/com/library/dto/UserMessageDTO.java` - 消息 DTO
- `src/main/java/com/library/controller/MessageController.java` - 消息控制器
- `src/main/java/com/library/controller/UserStatsController.java` - 用户统计控制器
- `src/main/java/com/library/dto/UserStatsVO.java` - 统计 VO
- `src/main/java/com/library/dto/BookImportDTO.java` - 图书导入 DTO
- `src/main/java/com/library/dto/BookExportDTO.java` - 图书导出 DTO

**修改文件：**
- `src/main/java/com/library/LibraryApplication.java` - 添加 @EnableScheduling
- `src/main/java/com/library/entity/BorrowRecord.java` - 添加提醒字段
- `src/main/java/com/library/common/Constants.java` - 添加新常量
- `src/main/java/com/library/controller/BookController.java` - 添加导入导出端点
- `src/main/java/com/library/service/BookService.java` - 添加批量导入方法
- `src/main/java/com/library/service/impl/BookServiceImpl.java` - 实现批量导入
- `src/main/java/com/library/mapper/BookMapper.java` - 添加按ISBN查询方法
- `src/main/java/com/library/mapper/BorrowRecordMapper.java` - 添加统计查询方法
- `src/main/resources/sql/library.sql` - 添加新表 DDL
- `pom.xml` - 添加 EasyExcel 依赖

### 前端 (library-vue)

**新增文件：**
- `src/views/messages/index.vue` - 消息中心页面
- `src/views/profile/components/StatsCard.vue` - 统计卡片组件
- `src/components/echarts/index.js` - ECharts 初始化配置

**修改文件：**
- `src/router/index.js` - 添加 /messages 路由
- `src/stores/user.js` - 添加消息数量状态和获取方法
- `src/api/index.js` - 添加 message 和 userStats API
- `src/layouts/Navbar.vue` - 添加消息通知铃铛图标
- `src/views/profile/index.vue` - 添加阅读统计 Tab
- `package.json` - 添加 echarts 和 vue-echarts 依赖

---

## 任务分解

### 阶段一：消息通知功能（站内信 + 定时提醒）

#### Task 1: 后端 - 数据库表和实体类

**Files:**
- Create: `library-server/src/main/java/com/library/entity/UserMessage.java`
- Create: `library-server/src/main/java/com/library/entity/BorrowRemindLog.java`
- Modify: `library-server/src/main/resources/sql/library.sql`

- [ ] **Step 1: 创建 UserMessage 实体类**

```java
package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_message")
public class UserMessage {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String content;

    private Integer type; // 1=借阅提醒 2=系统通知 3=预约到货

    private Integer isRead; // 0=未读 1=已读

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

- [ ] **Step 2: 创建 BorrowRemindLog 实体类**

```java
package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("borrow_remind_log")
public class BorrowRemindLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long borrowId;

    private Long userId;

    private Integer remindType; // 1=即将到期 2=已逾期

    private Integer remindDays; // 提前几天提醒/逾期几天

    private Integer remindStatus; // 0=待发送 1=已发送 2=发送失败

    private LocalDateTime sendTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

- [ ] **Step 3: 修改 BorrowRecord 实体，添加提醒字段**

在 `BorrowRecord.java` 中添加：
```java
private Integer remindSent; // 是否已发送到期提醒 0=未发 1=已发
private Integer overdueRemindCount; // 逾期提醒次数
```

- [ ] **Step 4: 添加 SQL DDL 到 library.sql**

```sql
-- 站内消息表
CREATE TABLE `user_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(100) NOT NULL,
  `content` varchar(500) NOT NULL,
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '1=借阅提醒 2=系统通知 3=预约到货',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '0=未读 1=已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id`(`user_id`),
  INDEX `idx_is_read`(`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 借阅提醒日志表
CREATE TABLE `borrow_remind_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `borrow_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `remind_type` tinyint NOT NULL COMMENT '1=即将到期 2=已逾期',
  `remind_days` int NOT NULL COMMENT '提前几天提醒/逾期几天',
  `remind_status` tinyint NOT NULL DEFAULT 0 COMMENT '0=待发送 1=已发送 2=发送失败',
  `send_time` datetime NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_borrow_id`(`borrow_id`),
  INDEX `idx_user_id`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 修改 borrow_record 表添加提醒字段
ALTER TABLE `borrow_record` ADD COLUMN `remind_sent` tinyint NOT NULL DEFAULT 0 COMMENT '是否已发送到期提醒 0=未发 1=已发';
ALTER TABLE `borrow_record` ADD COLUMN `overdue_remind_count` int NOT NULL DEFAULT 0 COMMENT '逾期提醒次数';
```

- [ ] **Step 5: 在 Constants.java 添加新常量**

```java
// 消息类型
public static final int MSG_TYPE_BORROW_REMIND = 1;
public static final int MSG_TYPE_SYSTEM = 2;
public static final int MSG_TYPE_BOOK_AVAILABLE = 3;

// 提醒类型
public static final int REMIND_TYPE_COMING_DUE = 1;
public static final int REMIND_TYPE_OVERDUE = 2;

// 提醒状态
public static final int REMIND_STATUS_PENDING = 0;
public static final int REMIND_STATUS_SENT = 1;
public static final int REMIND_STATUS_FAILED = 2;

// 提醒配置
public static final int REMIND_BEFORE_DAYS = 3; // 提前3天提醒
public static final int OVERDUE_RESEND_DAYS = 1; // 逾期后每天重复提醒
```

---

#### Task 2: 后端 - Mapper 层

**Files:**
- Create: `library-server/src/main/java/com/library/mapper/UserMessageMapper.java`
- Create: `library-server/src/main/java/com/library/mapper/BorrowRemindLogMapper.java`
- Modify: `library-server/src/main/java/com/library/mapper/BorrowRecordMapper.java`

- [ ] **Step 1: 创建 UserMessageMapper**

```java
package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.UserMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMessageMapper extends BaseMapper<UserMessage> {
}
```

- [ ] **Step 2: 创建 BorrowRemindLogMapper**

```java
package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BorrowRemindLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BorrowRemindLogMapper extends BaseMapper<BorrowRemindLog> {
}
```

- [ ] **Step 3: 修改 BorrowRecordMapper 添加统计查询方法**

```java
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
```

---

#### Task 3: 后端 - Service 层

**Files:**
- Create: `library-server/src/main/java/com/library/service/UserMessageService.java`
- Create: `library-server/src/main/java/com/library/service/impl/UserMessageServiceImpl.java`
- Create: `library-server/src/main/java/com/library/service/BorrowRemindService.java`
- Create: `library-server/src/main/java/com/library/service/impl/BorrowRemindServiceImpl.java`

- [ ] **Step 1: 创建 UserMessageService 接口**

```java
package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.UserMessage;

public interface UserMessageService {

    Page<UserMessage> getMessagePage(Long userId, Integer pageNum, Integer pageSize);

    void sendMessage(Long userId, String title, String content, Integer type);

    void markAsRead(Long messageId, Long userId);

    void markAllAsRead(Long userId);

    Integer getUnreadCount(Long userId);
}
```

- [ ] **Step 2: 创建 UserMessageServiceImpl 实现**

```java
package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.UserMessage;
import com.library.mapper.UserMessageMapper;
import com.library.service.UserMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMessageServiceImpl implements UserMessageService {

    private final UserMessageMapper userMessageMapper;

    @Override
    public Page<UserMessage> getMessagePage(Long userId, Integer pageNum, Integer pageSize) {
        Page<UserMessage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessage::getUserId, userId)
               .orderByDesc(UserMessage::getCreateTime);
        return userMessageMapper.selectPage(page, wrapper);
    }

    @Override
    public void sendMessage(Long userId, String title, String content, Integer type) {
        UserMessage message = new UserMessage();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setIsRead(0);
        userMessageMapper.insert(message);
    }

    @Override
    public void markAsRead(Long messageId, Long userId) {
        LambdaQueryWrapper<UserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessage::getId, messageId)
               .eq(UserMessage::getUserId, userId);
        UserMessage message = userMessageMapper.selectOne(wrapper);
        if (message != null) {
            message.setIsRead(1);
            userMessageMapper.updateById(message);
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        LambdaQueryWrapper<UserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessage::getUserId, userId)
               .eq(UserMessage::getIsRead, 0);
        UserMessage update = new UserMessage();
        update.setIsRead(1);
        userMessageMapper.update(update, wrapper);
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        LambdaQueryWrapper<UserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessage::getUserId, userId)
               .eq(UserMessage::getIsRead, 0);
        return Math.toIntExact(userMessageMapper.selectCount(wrapper));
    }
}
```

- [ ] **Step 3: 创建 BorrowRemindService 接口**

```java
package com.library.service;

import java.util.Map;
import java.util.List;

public interface BorrowRemindService {

    void processRemindTask();

    Map<String, Object> getUserBorrowStats(Long userId);
}
```

- [ ] **Step 4: 创建 BorrowRemindServiceImpl 实现**

```java
package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.library.common.Constants;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.BorrowRemindLog;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.BorrowRemindLogMapper;
import com.library.mapper.BookMapper;
import com.library.service.BorrowRemindService;
import com.library.service.UserMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BorrowRemindServiceImpl implements BorrowRemindService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final BorrowRemindLogMapper borrowRemindLogMapper;
    private final BookMapper bookMapper;
    private final UserMessageService userMessageService;

    @Override
    @Transactional
    public void processRemindTask() {
        LocalDate today = LocalDate.now();

        // 1. 处理即将到期的借阅（提前3天）
        LocalDate dueDateThreshold = today.plusDays(Constants.REMIND_BEFORE_DAYS);
        LambdaQueryWrapper<BorrowRecord> dueWrapper = new LambdaQueryWrapper<>();
        dueWrapper.eq(BorrowRecord::getStatus, Constants.BORROW_STATUS_BORROWING)
                  .eq(BorrowRecord::getRemindSent, 0)
                  .le(BorrowRecord::getDueDate, dueDateThreshold.atStartOfDay());
        List<BorrowRecord> comingDueList = borrowRecordMapper.selectList(dueWrapper);

        for (BorrowRecord record : comingDueList) {
            sendComingDueRemind(record);
        }

        // 2. 处理已逾期的借阅
        LambdaQueryWrapper<BorrowRecord> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.eq(BorrowRecord::getStatus, Constants.BORROW_STATUS_BORROWING)
                      .lt(BorrowRecord::getDueDate, today.atStartOfDay());
        List<BorrowRecord> overdueList = borrowRecordMapper.selectList(overdueWrapper);

        for (BorrowRecord record : overdueList) {
            sendOverdueRemind(record, today);
        }
    }

    private void sendComingDueRemind(BorrowRecord record) {
        Book book = bookMapper.selectById(record.getBookId());
        long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), record.getDueDate().toLocalDate());

        String title = "图书到期提醒";
        String content = String.format("您借阅的《%s》还有%d天到期，请及时归还。", book.getTitle(), daysLeft);

        userMessageService.sendMessage(record.getUserId(), title, content, Constants.MSG_TYPE_BORROW_REMIND);

        // 记录日志
        saveRemindLog(record, Constants.REMIND_TYPE_COMING_DUE, (int) daysLeft);

        // 更新提醒状态
        LambdaUpdateWrapper<BorrowRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BorrowRecord::getId, record.getId())
                     .set(BorrowRecord::getRemindSent, 1);
        borrowRecordMapper.update(null, updateWrapper);
    }

    private void sendOverdueRemind(BorrowRecord record, LocalDate today) {
        Book book = bookMapper.selectById(record.getBookId());
        long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(record.getDueDate().toLocalDate(), today);

        // 检查今天是否已经发送过逾期提醒
        LocalDateTime todayStart = today.atStartOfDay();
        LambdaQueryWrapper<BorrowRemindLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(BorrowRemindLog::getBorrowId, record.getId())
                  .eq(BorrowRemindLog::getRemindType, Constants.REMIND_TYPE_OVERDUE)
                  .ge(BorrowRemindLog::getSendTime, todayStart);
        long sentToday = borrowRemindLogMapper.selectCount(logWrapper);

        if (sentToday == 0) {
            String title = "图书逾期提醒";
            String content = String.format("您借阅的《%s》已逾期%d天，请尽快归还！", book.getTitle(), overdueDays);

            userMessageService.sendMessage(record.getUserId(), title, content, Constants.MSG_TYPE_BORROW_REMIND);

            // 更新逾期次数
            LambdaUpdateWrapper<BorrowRecord> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(BorrowRecord::getId, record.getId())
                         .set(BorrowRecord::getOverdueRemindCount, record.getOverdueRemindCount() + 1);
            borrowRecordMapper.update(null, updateWrapper);

            // 记录日志
            saveRemindLog(record, Constants.REMIND_TYPE_OVERDUE, (int) overdueDays);
        }
    }

    private void saveRemindLog(BorrowRecord record, Integer remindType, Integer remindDays) {
        BorrowRemindLog log = new BorrowRemindLog();
        log.setBorrowId(record.getId());
        log.setUserId(record.getUserId());
        log.setRemindType(remindType);
        log.setRemindDays(remindDays);
        log.setRemindStatus(Constants.REMIND_STATUS_SENT);
        log.setSendTime(LocalDateTime.now());
        borrowRemindLogMapper.insert(log);
    }

    @Override
    public Map<String, Object> getUserBorrowStats(Long userId) {
        Map<String, Object> stats = new java.util.HashMap<>();

        // 累计借阅
        LambdaQueryWrapper<BorrowRecord> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(BorrowRecord::getUserId, userId);
        long totalBorrow = borrowRecordMapper.selectCount(totalWrapper);
        stats.put("totalBorrowCount", totalBorrow);

        // 当前借阅
        LambdaQueryWrapper<BorrowRecord> currentWrapper = new LambdaQueryWrapper<>();
        currentWrapper.eq(BorrowRecord::getUserId, userId)
                      .eq(BorrowRecord::getStatus, Constants.BORROW_STATUS_BORROWING);
        long currentBorrow = borrowRecordMapper.selectCount(currentWrapper);
        stats.put("currentBorrowCount", currentBorrow);

        // 已归还
        LambdaQueryWrapper<BorrowRecord> returnedWrapper = new LambdaQueryWrapper<>();
        returnedWrapper.eq(BorrowRecord::getUserId, userId)
                       .eq(BorrowRecord::getStatus, Constants.BORROW_STATUS_RETURNED);
        long returnedCount = borrowRecordMapper.selectCount(returnedWrapper);
        stats.put("returnedCount", returnedCount);

        // 逾期次数
        LambdaQueryWrapper<BorrowRecord> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.eq(BorrowRecord::getUserId, userId)
                      .gt(BorrowRecord::getOverdueRemindCount, 0);
        long overdueCount = borrowRecordMapper.selectCount(overdueWrapper);
        stats.put("overdueCount", overdueCount);

        // 分类统计
        stats.put("categoryStats", borrowRecordMapper.getCategoryStats(userId));

        // 月度趋势
        stats.put("monthlyStats", borrowRecordMapper.getMonthlyStats(userId));

        // 借阅排行
        stats.put("topBooks", borrowRecordMapper.getTopBooks(userId));

        return stats;
    }
}
```

---

#### Task 4: 后端 - Controller 层

**Files:**
- Create: `library-server/src/main/java/com/library/controller/MessageController.java`
- Create: `library-server/src/main/java/com/library/controller/UserStatsController.java`
- Modify: `library-server/src/main/java/com/library/LibraryApplication.java`

- [ ] **Step 1: 创建 MessageController**

```java
package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.UserMessage;
import com.library.service.UserMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final UserMessageService userMessageService;

    @GetMapping
    public Result<Page<UserMessage>> getMessages(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<UserMessage> page = userMessageService.getMessagePage(userId, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer count = userMessageService.getUnreadCount(userId);
        return Result.success(count);
    }

    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userMessageService.markAsRead(id, userId);
        return Result.success();
    }

    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userMessageService.markAllAsRead(userId);
        return Result.success();
    }
}
```

- [ ] **Step 2: 创建 UserStatsController**

```java
package com.library.controller;

import com.library.common.Result;
import com.library.service.BorrowRemindService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user-stats")
@RequiredArgsConstructor
public class UserStatsController {

    private final BorrowRemindService borrowRemindService;

    @GetMapping
    public Result<Map<String, Object>> getUserStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> stats = borrowRemindService.getUserBorrowStats(userId);
        return Result.success(stats);
    }
}
```

- [ ] **Step 3: 修改 LibraryApplication.java 添加 @EnableScheduling**

```java
package com.library;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.library.mapper")
@EnableScheduling
public class LibraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}
```

- [ ] **Step 4: 创建定时任务 BorrowRemindTask**

```java
package com.library.task;

import com.library.service.BorrowRemindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BorrowRemindTask {

    private final BorrowRemindService borrowRemindService;

    // 每天早上9点执行
    @Scheduled(cron = "0 0 9 * * ?")
    public void executeRemindTask() {
        log.info("开始执行借阅提醒任务");
        try {
            borrowRemindService.processRemindTask();
            log.info("借阅提醒任务执行完成");
        } catch (Exception e) {
            log.error("借阅提醒任务执行失败", e);
        }
    }
}
```

---

### 阶段二：批量导入/导出图书

#### Task 5: 后端 - 添加 EasyExcel 依赖和导入导出功能

**Files:**
- Modify: `library-server/pom.xml`
- Create: `library-server/src/main/java/com/library/dto/BookImportDTO.java`
- Create: `library-server/src/main/java/com/library/dto/BookExportDTO.java`
- Modify: `library-server/src/main/java/com/library/controller/BookController.java`
- Modify: `library-server/src/main/java/com/library/service/BookService.java`
- Modify: `library-server/src/main/java/com/library/service/impl/BookServiceImpl.java`
- Modify: `library-server/src/main/java/com/library/mapper/BookMapper.java`

- [ ] **Step 1: 在 pom.xml 添加 EasyExcel 依赖**

```xml
<!-- EasyExcel for Excel processing -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>3.3.2</version>
</dependency>
```

- [ ] **Step 2: 创建 BookImportDTO**

```java
package com.library.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BookImportDTO {

    @ExcelProperty("ISBN")
    private String isbn;

    @ExcelProperty("书名")
    private String title;

    @ExcelProperty("作者")
    private String author;

    @ExcelProperty("出版社")
    private String publisher;

    @ExcelProperty("出版日期")
    private String publishDate;

    @ExcelProperty("分类名称")
    private String categoryName;

    @ExcelProperty("价格")
    private String price;

    @ExcelProperty("库存")
    private String stock;

    @ExcelProperty("简介")
    private String description;

    @ExcelProperty("封面URL")
    private String coverUrl;
}
```

- [ ] **Step 3: 创建 BookExportDTO**

```java
package com.library.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BookExportDTO {

    @ExcelProperty("ISBN")
    private String isbn;

    @ExcelProperty("书名")
    private String title;

    @ExcelProperty("作者")
    private String author;

    @ExcelProperty("出版社")
    private String publisher;

    @ExcelProperty("出版日期")
    private String publishDate;

    @ExcelProperty("分类")
    private String categoryName;

    @ExcelProperty("价格")
    private String price;

    @ExcelProperty("库存")
    private Integer stock;

    @ExcelProperty("简介")
    private String description;

    @ExcelProperty("状态")
    private String status;

    public static String getStatusName(Integer status) {
        return status == 1 ? "上架" : "下架";
    }
}
```

- [ ] **Step 4: 修改 BookMapper 添加按 ISBN 查询**

```java
@Mapper
public interface BookMapper extends BaseMapper<Book> {

    Book selectByIsbn(@Param("isbn") String isbn);
}
```

在 BookMapper.xml 中添加：
```xml
<select id="selectByIsbn" resultType="com.library.entity.Book">
    SELECT * FROM book WHERE isbn = #{isbn}
</select>
```

- [ ] **Step 5: 修改 BookService 接口添加导入导出方法**

```java
Map<String, Object> importBooks(MultipartFile file) throws Exception;

void exportBooks(HttpServletResponse response) throws Exception;

byte[] getImportTemplate();
```

- [ ] **Step 6: 修改 BookServiceImpl 实现导入导出**

```java
@Override
public Map<String, Object> importBooks(MultipartFile file) throws Exception {
    List<BookImportDTO> imports = EasyExcel.read(file.getInputStream())
            .head(BookImportDTO.class)
            .sheet()
            .doReadSync();

    Map<String, Object> result = new HashMap<>();
    int successCount = 0;
    int failCount = 0;
    List<String> errors = new ArrayList<>();

    for (int i = 0; i < imports.size(); i++) {
        BookImportDTO dto = imports.get(i);
        try {
            // 校验必填字段
            if (dto.getIsbn() == null || dto.getTitle() == null) {
                errors.add(String.format("第%d行: ISBN和书名不能为空", i + 2));
                failCount++;
                continue;
            }

            // 查询分类
            LambdaQueryWrapper<BookCategory> categoryWrapper = new LambdaQueryWrapper<>();
            categoryWrapper.eq(BookCategory::getName, dto.getCategoryName());
            BookCategory category = bookCategoryMapper.selectOne(categoryWrapper);
            if (category == null) {
                errors.add(String.format("第%d行: 分类【%s】不存在", i + 2, dto.getCategoryName()));
                failCount++;
                continue;
            }

            // 检查是否已存在
            Book existingBook = bookMapper.selectByIsbn(dto.getIsbn());
            Book book = new Book();
            book.setIsbn(dto.getIsbn());
            book.setTitle(dto.getTitle());
            book.setAuthor(dto.getAuthor());
            book.setPublisher(dto.getPublisher());
            if (dto.getPublishDate() != null) {
                book.setPublishDate(LocalDate.parse(dto.getPublishDate()));
            }
            book.setCategoryId(category.getId());
            if (dto.getPrice() != null) {
                book.setPrice(new BigDecimal(dto.getPrice()));
            }
            if (dto.getStock() != null) {
                book.setStock(Integer.parseInt(dto.getStock()));
            }
            book.setDescription(dto.getDescription());
            book.setCoverUrl(dto.getCoverUrl());
            book.setStatus(1);

            if (existingBook != null) {
                book.setId(existingBook.getId());
                bookMapper.updateById(book);
            } else {
                bookMapper.insert(book);
            }
            successCount++;
        } catch (Exception e) {
            errors.add(String.format("第%d行: %s", i + 2, e.getMessage()));
            failCount++;
        }
    }

    result.put("total", imports.size());
    result.put("success", successCount);
    result.put("fail", failCount);
    result.put("errors", errors);
    return result;
}

@Override
public void exportBooks(HttpServletResponse response) throws Exception {
    LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Book::getStatus, 1);
    List<Book> books = bookMapper.selectList(wrapper);

    List<BookExportDTO> exports = books.stream().map(book -> {
        BookExportDTO dto = new BookExportDTO();
        dto.setIsbn(book.getIsbn());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPublisher(book.getPublisher());
        dto.setPublishDate(book.getPublishDate() != null ? book.getPublishDate().toString() : "");
        dto.setCategoryName(getCategoryName(book.getCategoryId()));
        dto.setPrice(book.getPrice() != null ? book.getPrice().toString() : "");
        dto.setStock(book.getStock());
        dto.setDescription(book.getDescription());
        dto.setStatus(BookExportDTO.getStatusName(book.getStatus()));
        return dto;
    }).collect(Collectors.toList());

    EasyExcel.write(response.getOutputStream(), BookExportDTO.class)
            .sheet("图书列表")
            .doWrite(exports);
}

@Override
public byte[] getImportTemplate() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    EasyExcel.write(out, BookImportDTO.class)
            .sheet("图书导入模板")
            .doWrite(List.of(new BookImportDTO()));
    return out.toByteArray();
}

private String getCategoryName(Long categoryId) {
    if (categoryId == null) return "";
    BookCategory category = bookCategoryMapper.selectById(categoryId);
    return category != null ? category.getName() : "";
}
```

- [ ] **Step 7: 修改 BookController 添加导入导出端点**

在 BookController 中添加：

```java
@GetMapping("/template")
public void downloadTemplate(HttpServletResponse response) throws Exception {
    byte[] template = bookService.getImportTemplate();
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename=book_import_template.xlsx");
    response.getOutputStream().write(template);
}

@PostMapping("/import")
public Result<Map<String, Object>> importBooks(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
    checkAdmin(request);
    try {
        Map<String, Object> result = bookService.importBooks(file);
        return Result.success(result);
    } catch (Exception e) {
        return Result.error("导入失败: " + e.getMessage());
    }
}

@GetMapping("/export")
public void exportBooks(HttpServletResponse response, HttpServletRequest request) {
    try {
        bookService.exportBooks(response);
    } catch (Exception e) {
        throw new RuntimeException("导出失败: " + e.getMessage());
    }
}
```

---

### 阶段三：前端 - 消息中心和借阅统计页面

#### Task 6: 前端 - 安装 ECharts 依赖

**Files:**
- Modify: `library-vue/package.json`

- [ ] **Step 1: 添加 echarts 和 vue-echarts 依赖**

运行命令：
```bash
cd library-vue && npm install echarts vue-echarts
```

或直接修改 package.json 添加：
```json
"echarts": "^5.5.0",
"vue-echarts": "^6.6.8"
```

---

#### Task 7: 前端 - API 和 Store

**Files:**
- Modify: `library-vue/src/api/index.js`
- Modify: `library-vue/src/stores/user.js`

- [ ] **Step 1: 修改 api/index.js 添加消息和统计 API**

```javascript
export const message = {
  list: (params) => request.get('/messages', { params }),
  getUnreadCount: () => request.get('/messages/unread-count'),
  markRead: (id) => request.put(`/messages/${id}/read`),
  markAllRead: () => request.put('/messages/read-all')
}

export const userStats = {
  get: () => request.get('/user-stats')
}
```

- [ ] **Step 2: 修改 stores/user.js 添加消息状态**

```javascript
state: () => ({
  token: localStorage.getItem('token') || '',
  userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
  unreadMessageCount: 0
}),

actions: {
  // ... existing actions

  async fetchUnreadMessageCount() {
    if (!this.token) return 0
    try {
      const res = await message.getUnreadCount()
      this.unreadMessageCount = res.data || 0
      return this.unreadMessageCount
    } catch (e) {
      return 0
    }
  }
}
```

---

#### Task 8: 前端 - 消息中心页面

**Files:**
- Create: `library-vue/src/views/messages/index.vue`
- Modify: `library-vue/src/router/index.js`
- Modify: `library-vue/src/layouts/Navbar.vue`

- [ ] **Step 1: 创建消息中心页面**

```vue
<template>
  <div class="messages-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的消息</span>
          <el-button type="primary" link @click="markAllRead" v-if="messages.length > 0">
            全部标为已读
          </el-button>
        </div>
      </template>

      <el-empty v-if="messages.length === 0" description="暂无消息" />

      <div v-else class="message-list">
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="message-item"
          :class="{ unread: msg.isRead === 0 }"
          @click="handleRead(msg)"
        >
          <div class="message-icon">
            <el-icon v-if="msg.type === 1" color="#E6A23C"><Bell /></el-icon>
            <el-icon v-else-if="msg.type === 2" color="#409EFF"><InfoFilled /></el-icon>
            <el-icon v-else color="#67C23A"><CircleCheck /></el-icon>
          </div>
          <div class="message-content">
            <div class="message-title">{{ msg.title }}</div>
            <div class="message-text">{{ msg.content }}</div>
            <div class="message-time">{{ formatTime(msg.createTime) }}</div>
          </div>
          <div class="message-action" v-if="msg.isRead === 0">
            <el-tag size="small" type="danger">未读</el-tag>
          </div>
        </div>
      </div>

      <el-pagination
        v-if="total > 0"
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        @current-change="loadMessages"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from '@/api'
import { ElMessage } from 'element-plus'
import { Bell, InfoFilled, CircleCheck } from '@element-plus/icons-vue'

const messages = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const loadMessages = async () => {
  const res = await message.list({ pageNum: pageNum.value, pageSize: pageSize.value })
  messages.value = res.data.records
  total.value = res.data.total
}

const handleRead = async (msg) => {
  if (msg.isRead === 0) {
    await message.markRead(msg.id)
    msg.isRead = 1
    // 更新 store 中的未读数
    const userStore = useUserStore()
    userStore.unreadMessageCount = Math.max(0, userStore.unreadMessageCount - 1)
  }
}

const markAllRead = async () => {
  await message.markAllRead()
  ElMessage.success('已全部标为已读')
  loadMessages()
  const userStore = useUserStore()
  userStore.unreadMessageCount = 0
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return date.toLocaleDateString()
}

onMounted(() => {
  loadMessages()
})
</script>

<style scoped>
.messages-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.message-item:hover {
  background: #ecf5ff;
}

.message-item.unread {
  background: #fef0f0;
  border-left: 3px solid #f56c6c;
}

.message-icon {
  margin-right: 12px;
  font-size: 20px;
}

.message-content {
  flex: 1;
}

.message-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.message-text {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.message-time {
  color: #999;
  font-size: 12px;
}

.message-action {
  margin-left: 12px;
}
</style>
```

- [ ] **Step 2: 修改 router/index.js 添加消息路由**

在路由配置中添加：
```javascript
{
  path: '/messages',
  name: 'Messages',
  component: () => import('@/views/messages/index.vue'),
  meta: { title: '我的消息', requiresAdmin: false }
}
```

- [ ] **Step 3: 修改 Navbar.vue 添加消息铃铛**

在 Navbar.vue 的 right div 中添加：
```xml
<el-badge :value="notificationCount" :hidden="notificationCount === 0" :max="99">
  <el-icon :size="22" style="margin-right: 20px; cursor: pointer" @click="$router.push('/messages')">
    <Bell />
  </el-icon>
</el-badge>
```

并在 script 中：
1. 引入 Bell 图标
2. 在 onMounted 中调用 userStore.fetchUnreadMessageCount()
3. 添加 computed 属性 notificationCount

---

#### Task 9: 前端 - 借阅统计页面

**Files:**
- Modify: `library-vue/src/views/profile/index.vue`
- Create: `library-vue/src/components/echarts/index.js`

- [ ] **Step 1: 创建 ECharts 配置文件**

```javascript
// src/components/echarts/index.js
import { use } from 'echarts/core'
import { PieChart, LineChart, BarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

use([PieChart, LineChart, BarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, CanvasRenderer])

export default use
```

- [ ] **Step 2: 修改 profile/index.vue 添加统计 Tab**

在现有 profile/index.vue 中添加 el-tabs 和统计内容：

```vue
<el-tabs v-model="activeTab" @tab-change="handleTabChange">
  <el-tab-pane label="个人信息" name="profile">
    <!-- 现有个人信息内容 -->
  </el-tab-pane>

  <el-tab-pane label="借阅记录" name="borrows">
    <!-- 现有借阅记录内容 -->
  </el-tab-pane>

  <el-tab-pane label="阅读统计" name="stats">
    <div class="stats-container">
      <!-- 统计卡片 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-value">{{ stats.totalBorrowCount }}</div>
              <div class="stat-label">累计借阅</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-value">{{ stats.currentBorrowCount }}</div>
              <div class="stat-label">当前借阅</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-value">{{ stats.returnedCount }}</div>
              <div class="stat-label">已归还</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-value">{{ stats.overdueCount }}</div>
              <div class="stat-label">逾期次数</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="12">
          <el-card>
            <template #header>分类分布</template>
            <v-chart :option="categoryChartOption" style="height: 300px" />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>月度借阅趋势</template>
            <v-chart :option="monthlyChartOption" style="height: 300px" />
          </el-card>
        </el-col>
      </el-row>

      <!-- 借阅排行 -->
      <el-card>
        <template #header>借阅排行 Top10</template>
        <el-table :data="stats.topBooks" stripe>
          <el-table-column prop="title" label="书名" />
          <el-table-column prop="author" label="作者" />
          <el-table-column prop="borrowCount" label="借阅次数" width="120" />
        </el-table>
      </el-card>
    </div>
  </el-tab-pane>
</el-tabs>
```

在 script 中添加：
```javascript
import { userStats } from '@/api'
import VChart from 'vue-echarts'
import '@/components/echarts'
import { use } from 'echarts/core'
import { PieChart, LineChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
use([PieChart, LineChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, CanvasRenderer])

const activeTab = ref('profile')
const stats = ref({
  totalBorrowCount: 0,
  currentBorrowCount: 0,
  returnedCount: 0,
  overdueCount: 0,
  categoryStats: [],
  monthlyStats: [],
  topBooks: []
})

const handleTabChange = async (tab) => {
  if (tab === 'stats' && stats.value.totalBorrowCount === 0) {
    await loadStats()
  }
}

const loadStats = async () => {
  const res = await userStats.get()
  stats.value = res.data
}

const categoryChartOption = computed(() => ({
  tooltip: { trigger: 'item' },
  legend: { bottom: 0 },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    data: stats.value.categoryStats?.map(item => ({
      name: item.categoryName,
      value: item.count
    })) || []
  }]
}))

const monthlyChartOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: stats.value.monthlyStats?.map(item => item.month).reverse() || []
  },
  yAxis: { type: 'value' },
  series: [{
    type: 'line',
    smooth: true,
    data: stats.value.monthlyStats?.map(item => item.count).reverse() || [],
    areaStyle: { opacity: 0.3 }
  }]
}))
```

---

## 验证方案

### 后端验证

1. **启动后端服务**
```bash
cd library-server && mvn spring-boot:run
```

2. **测试消息 API（使用 curl 或 Postman）**
```bash
# 登录获取 token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 获取未读消息数
curl http://localhost:8080/api/messages/unread-count \
  -H "Authorization: Bearer <token>"

# 获取消息列表
curl "http://localhost:8080/api/messages?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer <token>"
```

3. **测试定时任务**
- 借阅一本书，到期日前3天内观察是否收到提醒
- 或者手动调用 BorrowRemindTask 的 processRemindTask 方法

### 前端验证

1. **安装依赖并启动前端**
```bash
cd library-vue && npm install && npm run dev
```

2. **验证消息中心**
- 登录后查看导航栏是否有铃铛图标
- 点击铃铛是否跳转到消息中心
- 消息列表是否正确显示

3. **验证借阅统计**
- 进入个人中心
- 点击"阅读统计" Tab
- 查看统计数据和图表是否正确显示

### 批量导入导出验证

1. **下载导入模板**
```bash
curl http://localhost:8080/api/books/template \
  -H "Authorization: Bearer <admin-token>" \
  --output book_import_template.xlsx
```

2. **上传导入**
```bash
curl -X POST http://localhost:8080/api/books/import \
  -H "Authorization: Bearer <admin-token>" \
  -F "file=@books.xlsx"
```

3. **导出图书**
```bash
curl http://localhost:8080/api/books/export \
  -H "Authorization: Bearer <admin-token>" \
  --output books.xlsx
```

---

## 实施顺序建议

1. **Task 1-4**: 后端消息通知功能（实体、Mapper、Service、Controller、定时任务）
2. **Task 5**: 后端批量导入导出功能
3. **Task 6-7**: 前端基础（依赖、API、Store）
4. **Task 8**: 前端消息中心页面
5. **Task 9**: 前端借阅统计页面

每个 Task 完成后的 commit 建议：
- `feat: 添加站内消息功能和借阅提醒定时任务`
- `feat: 添加图书批量导入导出功能`
- `feat: 添加前端消息中心页面`
- `feat: 添加前端借阅统计页面`

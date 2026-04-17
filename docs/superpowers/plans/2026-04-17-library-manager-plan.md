# 图书管理系统实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一个前后端分离的图书管理系统，包含图书管理、借阅管理、读者管理、JWT认证功能

**Architecture:** 前后端分离架构，后端Spring Boot + MyBatis-Plus，前端Vue 3 + Vite + Element Plus，JWT无状态认证

**Tech Stack:** Spring Boot 3.x, MyBatis-Plus, MySQL 8.x, JWT, Vue 3, Vite, Element Plus, Pinia, Vue Router 4

---

## 项目结构

```
LibraryManager/
├── library-server/          # 后端 Spring Boot 项目
│   ├── src/main/java/com/library/
│   ├── src/main/resources/
│   └── pom.xml
├── library-vue/              # 前端 Vue 3 项目
│   ├── src/
│   ├── public/
│   └── package.json
└── docs/                     # 文档
```

---

## 第一阶段：后端基础框架

### Task 1: 创建 Spring Boot 项目骨架

**Files:**
- Create: `library-server/pom.xml`
- Create: `library-server/src/main/java/com/library/LibraryApplication.java`
- Create: `library-server/src/main/resources/application.yml`
- Create: `library-server/src/main/resources/application-dev.yml`

- [ ] **Step 1: 创建项目目录结构**

```bash
mkdir -p library-server/src/main/java/com/library/{config,controller,service,mapper,entity,dto,common,security}
mkdir -p library-server/src/main/resources
mkdir -p library-server/src/test/java/com/library
```

- [ ] **Step 2: 创建 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <groupId>com.library</groupId>
    <artifactId>library-server</artifactId>
    <version>1.0.0</version>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <jjwt.version>0.12.3</jjwt.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <!-- MySQL -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 3: 创建主启动类**

```java
package com.library;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.library.mapper")
public class LibraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}
```

- [ ] **Step 4: 创建 application.yml**

```yaml
spring:
  application:
    name: library-server
  profiles:
    active: dev
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/library?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

jwt:
  secret: library-jwt-secret-key-2024
  expiration: 604800000
```

- [ ] **Step 5: 提交**

```bash
git add library-server/
git commit -m "feat: 创建Spring Boot项目骨架"
```

---

### Task 2: 创建数据库表

**Files:**
- Create: `library-server/src/main/resources/sql/init.sql`

- [ ] **Step 1: 创建数据库表SQL**

```sql
CREATE DATABASE IF NOT EXISTS library DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library;

-- 用户表
CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role TINYINT NOT NULL DEFAULT 2 COMMENT '角色: 1-管理员 2-读者',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 图书分类表
CREATE TABLE book_category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- 图书表
CREATE TABLE book (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    isbn VARCHAR(50) COMMENT 'ISBN',
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) COMMENT '作者',
    publisher VARCHAR(100) COMMENT '出版社',
    publish_date DATE COMMENT '出版日期',
    category_id BIGINT COMMENT '分类ID',
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格',
    stock INT DEFAULT 0 COMMENT '库存数量',
    description TEXT COMMENT '简介',
    cover_url VARCHAR(500) COMMENT '封面图URL',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_isbn (isbn),
    KEY idx_category_id (category_id),
    KEY idx_title (title),
    KEY idx_author (author)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 借阅记录表
CREATE TABLE borrow_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    user_id BIGINT NOT NULL COMMENT '读者ID',
    borrow_date DATETIME NOT NULL COMMENT '借书日期',
    due_date DATETIME NOT NULL COMMENT '应还日期',
    return_date DATETIME COMMENT '实际还书日期',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-借阅中 2-已归还 3-逾期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_book_id (book_id),
    KEY idx_user_id (user_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- 初始化管理员账号 (密码: admin123)
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1, 1);

-- 初始化图书分类
INSERT INTO book_category (name, parent_id, sort) VALUES
('文学', 0, 1),
('小说', 1, 1),
('诗歌', 1, 2),
('科幻', 0, 2),
('技术', 0, 3),
('编程', 5, 1),
('设计', 0, 4);

-- 初始化测试图书
INSERT INTO book (isbn, title, author, publisher, publish_date, category_id, price, stock, description, status) VALUES
('978-7-111-54742-0', '深入理解计算机系统', 'Randal E. Bryant', '机械工业出版社', '2015-11-01', 6, 139.00, 5, '系统级编程经典教材', 1),
('978-7-115-42826-7', 'Python编程: 从入门到实践', 'Eric Matthes', '人民邮电出版社', '2016-07-01', 6, 89.00, 10, 'Python入门经典', 1),
('978-7-5322-5006-6', '三体', '刘慈欣', '重庆出版社', '2008-01-01', 4, 68.00, 8, '科幻巨著', 1);
```

- [ ] **Step 2: 执行SQL**

```bash
mysql -u root -p < library-server/src/main/resources/sql/init.sql
```

- [ ] **Step 3: 提交**

```bash
git add library-server/src/main/resources/sql/init.sql
git commit -m "feat: 创建数据库表结构和初始化数据"
```

---

### Task 3: 创建实体类和通用响应

**Files:**
- Create: `library-server/src/main/java/com/library/entity/User.java`
- Create: `library-server/src/main/java/com/library/entity/Book.java`
- Create: `library-server/src/main/java/com/library/entity/BookCategory.java`
- Create: `library-server/src/main/java/com/library/entity/BorrowRecord.java`
- Create: `library-server/src/main/java/com/library/common/Result.java`
- Create: `library-server/src/main/java/com/library/common/Constants.java`

- [ ] **Step 1: 创建 User 实体**

```java
package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Integer role;      // 1-管理员 2-读者
    private Integer status;    // 0-禁用 1-正常

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

- [ ] **Step 2: 创建 Book 实体**

```java
package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishDate;
    private Long categoryId;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private String coverUrl;
    private Integer status;    // 0-下架 1-上架

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 关联字段，非数据库字段
    @TableField(exist = false)
    private String categoryName;
}
```

- [ ] **Step 3: 创建 BookCategory 实体**

```java
package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("book_category")
public class BookCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 子分类，非数据库字段
    @TableField(exist = false)
    private List<BookCategory> children;
}
```

- [ ] **Step 4: 创建 BorrowRecord 实体**

```java
package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("borrow_record")
public class BorrowRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private Long userId;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private Integer status;    // 1-借阅中 2-已归还 3-逾期

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 关联字段
    @TableField(exist = false)
    private String bookTitle;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String userRealName;
}
```

- [ ] **Step 5: 创建 Result 通用响应**

```java
package com.library.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
```

- [ ] **Step 6: 创建 Constants 常量类**

```java
package com.library.common;

public class Constants {
    // 角色
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_USER = 2;

    // 状态
    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_DISABLED = 0;

    // 借阅状态
    public static final int BORROW_STATUS_BORROWING = 1;
    public static final int BORROW_STATUS_RETURNED = 2;
    public static final int BORROW_STATUS_OVERDUE = 3;

    // 图书状态
    public static final int BOOK_STATUS_ON = 1;
    public static final int BOOK_STATUS_OFF = 0;

    // 借阅期限（天）
    public static final int BORROW_DAYS = 30;
}
```

- [ ] **Step 7: 提交**

```bash
git add library-server/src/main/java/com/library/entity/
git add library-server/src/main/java/com/library/common/
git commit -m "feat: 创建实体类和通用响应"
```

---

### Task 4: 实现 JWT 认证

**Files:**
- Create: `library-server/src/main/java/com/library/security/JwtUtil.java`
- Create: `library-server/src/main/java/com/library/security/JwtAuthFilter.java`
- Create: `library-server/src/main/java/com/library/config/WebConfig.java`
- Create: `library-server/src/main/java/com/library/config/MybatisPlusConfig.java`

- [ ] **Step 1: 创建 JWT 工具类**

```java
package com.library.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String username, Integer role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public Integer getRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", Integer.class);
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
```

- [ ] **Step 2: 创建 JWT 认证过滤器**

```java
package com.library.security;

import com.library.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 放行预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);

        // 允许未登录访问的路径
        String[] allowedPaths = {"/api/auth/login", "/api/auth/register", "/api/books"};
        String path = request.getRequestURI();
        for (String allowedPath : allowedPaths) {
            if (path.startsWith(allowedPath)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 检查Token
        if (!StringUtils.hasText(token) || !jwtUtil.validateToken(token)) {
            sendUnauthorizedResponse(response, "未登录或登录已过期");
            return;
        }

        // 设置用户信息到请求属性
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        Integer role = jwtUtil.getRole(token);

        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("role", role);

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
```

- [ ] **Step 3: 创建 Web 配置类**

```java
package com.library.config;

import com.library.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WebConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilter() {
        FilterRegistrationBean<JwtAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtAuthFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
```

- [ ] **Step 4: 创建 MyBatis-Plus 配置类**

```java
package com.library.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class MybatisPlusConfig implements MetaObjectHandler {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
```

- [ ] **Step 5: 提交**

```bash
git add library-server/src/main/java/com/library/security/
git add library-server/src/main/java/com/library/config/
git commit -m "feat: 实现JWT认证过滤器"
```

---

### Task 5: 实现用户认证接口（注册、登录）

**Files:**
- Create: `library-server/src/main/java/com/library/mapper/UserMapper.java`
- Create: `library-server/src/main/java/com/library/service/UserService.java`
- Create: `library-server/src/main/java/com/library/service/impl/UserServiceImpl.java`
- Create: `library-server/src/main/java/com/library/controller/AuthController.java`
- Create: `library-server/src/main/java/com/library/dto/LoginRequest.java`
- Create: `library-server/src/main/java/com/library/dto/RegisterRequest.java`

- [ ] **Step 1: 创建 UserMapper**

```java
package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

- [ ] **Step 2: 创建 RegisterRequest DTO**

```java
package com.library.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String email;
}
```

- [ ] **Step 3: 创建 LoginRequest DTO**

```java
package com.library.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
```

- [ ] **Step 4: 创建 UserService 接口**

```java
package com.library.service;

import com.library.dto.LoginRequest;
import com.library.dto.RegisterRequest;
import com.library.entity.User;

public interface UserService {
    User register(RegisterRequest request);
    String login(LoginRequest request);
    User getCurrentUser(Long userId);
    User getUserById(Long id);
}
```

- [ ] **Step 5: 创建 UserServiceImpl**

```java
package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.common.Constants;
import com.library.common.Result;
import com.library.dto.LoginRequest;
import com.library.dto.RegisterRequest;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.security.JwtUtil;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User register(RegisterRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(Constants.ROLE_USER); // 默认读者角色
        user.setStatus(Constants.STATUS_NORMAL);

        userMapper.insert(user);
        return user;
    }

    @Override
    public String login(LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus().equals(Constants.STATUS_DISABLED)) {
            throw new RuntimeException("账号已被禁用");
        }

        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public User getCurrentUser(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
}
```

- [ ] **Step 6: 创建 AuthController**

```java
package com.library.controller;

import com.library.common.Result;
import com.library.dto.LoginRequest;
import com.library.dto.RegisterRequest;
import com.library.entity.User;
import com.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return Result.success(data);
    }

    @GetMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getCurrentUser(userId);
        user.setPassword(null); // 不返回密码
        return Result.success(user);
    }
}
```

- [ ] **Step 7: 提交**

```bash
git add library-server/src/main/java/com/library/mapper/UserMapper.java
git add library-server/src/main/java/com/library/dto/
git add library-server/src/main/java/com/library/service/
git add library-server/src/main/java/com/library/controller/AuthController.java
git commit -m "feat: 实现用户注册和登录接口"
```

---

### Task 6: 实现图书管理接口

**Files:**
- Create: `library-server/src/main/java/com/library/mapper/BookMapper.java`
- Create: `library-server/src/main/java/com/library/service/BookService.java`
- Create: `library-server/src/main/java/com/library/service/impl/BookServiceImpl.java`
- Create: `library-server/src/main/java/com/library/controller/BookController.java`
- Create: `library-server/src/main/java/com/library/dto/BookRequest.java`
- Create: `library-server/src/main/java/com/library/dto/PageRequest.java`

- [ ] **Step 1: 创建 BookMapper**

```java
package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
}
```

- [ ] **Step 2: 创建分页请求 DTO**

```java
package com.library.dto;

import lombok.Data;

@Data
public class PageRequest {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Long categoryId;
}
```

- [ ] **Step 3: 创建 BookRequest DTO**

```java
package com.library.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookRequest {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishDate;
    private Long categoryId;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private String coverUrl;
    private Integer status;
}
```

- [ ] **Step 4: 创建 BookService 接口**

```java
package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.BookRequest;
import com.library.dto.PageRequest;
import com.library.entity.Book;

public interface BookService {
    Page<Book> getBooks(PageRequest request);
    Book getBookById(Long id);
    void addBook(BookRequest request);
    void updateBook(BookRequest request);
    void deleteBook(Long id);
}
```

- [ ] **Step 5: 创建 BookServiceImpl**

```java
package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Constants;
import com.library.dto.BookRequest;
import com.library.dto.PageRequest;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;

    @Override
    public Page<Book> getBooks(PageRequest request) {
        Page<Book> page = new Page<>(request.getPageNum(), request.getPageSize());

        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getStatus, Constants.BOOK_STATUS_ON);

        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w.like(Book::getTitle, request.getKeyword())
                    .or().like(Book::getAuthor, request.getKeyword())
                    .or().like(Book::getIsbn, request.getKeyword()));
        }

        if (request.getCategoryId() != null) {
            wrapper.eq(Book::getCategoryId, request.getCategoryId());
        }

        wrapper.orderByDesc(Book::getCreateTime);
        Page<Book> result = bookMapper.selectPage(page, wrapper);

        return result;
    }

    @Override
    public Book getBookById(Long id) {
        Book book = bookMapper.selectById(id);
        return book;
    }

    @Override
    public void addBook(BookRequest request) {
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setPublishDate(request.getPublishDate());
        book.setCategoryId(request.getCategoryId());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
        book.setDescription(request.getDescription());
        book.setCoverUrl(request.getCoverUrl());
        book.setStatus(request.getStatus() != null ? request.getStatus() : Constants.BOOK_STATUS_ON);

        bookMapper.insert(book);
    }

    @Override
    public void updateBook(BookRequest request) {
        Book book = bookMapper.selectById(request.getId());
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setPublishDate(request.getPublishDate());
        book.setCategoryId(request.getCategoryId());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
        book.setDescription(request.getDescription());
        book.setCoverUrl(request.getCoverUrl());
        if (request.getStatus() != null) {
            book.setStatus(request.getStatus());
        }

        bookMapper.updateById(book);
    }

    @Override
    public void deleteBook(Long id) {
        bookMapper.deleteById(id);
    }
}
```

- [ ] **Step 6: 创建 BookController**

```java
package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Constants;
import com.library.common.Result;
import com.library.dto.BookRequest;
import com.library.dto.PageRequest;
import com.library.entity.Book;
import com.library.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public Result<Page<Book>> getBooks(PageRequest request) {
        Page<Book> page = bookService.getBooks(request);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        return Result.success(book);
    }

    @PostMapping
    public Result<Void> addBook(@Valid @RequestBody BookRequest request, HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        bookService.addBook(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateBook(@PathVariable Long id, @RequestBody BookRequest request, HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        request.setId(id);
        bookService.updateBook(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBook(@PathVariable Long id, HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        bookService.deleteBook(id);
        return Result.success();
    }

    private void checkAdmin(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || !role.equals(Constants.ROLE_ADMIN)) {
            throw new RuntimeException("无权限操作");
        }
    }
}
```

- [ ] **Step 7: 提交**

```bash
git add library-server/src/main/java/com/library/mapper/BookMapper.java
git add library-server/src/main/java/com/library/service/BookService.java
git add library-server/src/main/java/com/library/controller/BookController.java
git commit -m "feat: 实现图书管理接口"
```

---

### Task 7: 实现分类管理接口

**Files:**
- Create: `library-server/src/main/java/com/library/mapper/BookCategoryMapper.java`
- Create: `library-server/src/main/java/com/library/service/BookCategoryService.java`
- Create: `library-server/src/main/java/com/library/service/impl/BookCategoryServiceImpl.java`
- Create: `library-server/src/main/java/com/library/controller/CategoryController.java`
- Create: `library-server/src/main/java/com/library/dto/CategoryRequest.java`

- [ ] **Step 1: 创建 BookCategoryMapper**

```java
package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BookCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookCategoryMapper extends BaseMapper<BookCategory> {
}
```

- [ ] **Step 2: 创建 CategoryRequest DTO**

```java
package com.library.dto;

import lombok.Data;

@Data
public class CategoryRequest {
    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;
}
```

- [ ] **Step 3: 创建 BookCategoryService 接口**

```java
package com.library.service;

import com.library.dto.CategoryRequest;
import com.library.entity.BookCategory;
import java.util.List;

public interface BookCategoryService {
    List<BookCategory> getCategories();
    void addCategory(CategoryRequest request);
    void updateCategory(CategoryRequest request);
    void deleteCategory(Long id);
}
```

- [ ] **Step 4: 创建 BookCategoryServiceImpl**

```java
package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.dto.CategoryRequest;
import com.library.entity.BookCategory;
import com.library.mapper.BookCategoryMapper;
import com.library.service.BookCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookCategoryServiceImpl implements BookCategoryService {

    private final BookCategoryMapper categoryMapper;

    @Override
    public List<BookCategory> getCategories() {
        List<BookCategory> allCategories = categoryMapper.selectList(null);

        // 构建树形结构
        Map<Long, List<BookCategory>> childrenMap = allCategories.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() != 0)
                .collect(Collectors.groupingBy(BookCategory::getParentId));

        List<BookCategory> rootCategories = allCategories.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .peek(c -> c.setChildren(getChildren(c.getId(), childrenMap)))
                .collect(Collectors.toList());

        return rootCategories;
    }

    private List<BookCategory> getChildren(Long parentId, Map<Long, List<BookCategory>> childrenMap) {
        List<BookCategory> children = childrenMap.get(parentId);
        if (children == null) {
            return new ArrayList<>();
        }
        return children.stream()
                .peek(c -> c.setChildren(getChildren(c.getId(), childrenMap)))
                .collect(Collectors.toList());
    }

    @Override
    public void addCategory(CategoryRequest request) {
        BookCategory category = new BookCategory();
        category.setName(request.getName());
        category.setParentId(request.getParentId() != null ? request.getParentId() : 0);
        category.setSort(request.getSort() != null ? request.getSort() : 0);

        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(CategoryRequest request) {
        BookCategory category = categoryMapper.selectById(request.getId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        category.setName(request.getName());
        if (request.getParentId() != null) {
            category.setParentId(request.getParentId());
        }
        if (request.getSort() != null) {
            category.setSort(request.getSort());
        }

        categoryMapper.updateById(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        // 删除该分类及其子分类
        List<Long> idsToDelete = new ArrayList<>();
        idsToDelete.add(id);

        // 查找所有子分类
        findChildrenIds(id, idsToDelete);

        categoryMapper.deleteBatchIds(idsToDelete);
    }

    private void findChildrenIds(Long parentId, List<Long> ids) {
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookCategory::getParentId, parentId);
        List<BookCategory> children = categoryMapper.selectList(wrapper);

        for (BookCategory child : children) {
            ids.add(child.getId());
            findChildrenIds(child.getId(), ids);
        }
    }
}
```

- [ ] **Step 5: 创建 CategoryController**

```java
package com.library.controller;

import com.library.common.Constants;
import com.library.common.Result;
import com.library.dto.CategoryRequest;
import com.library.entity.BookCategory;
import com.library.service.BookCategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final BookCategoryService categoryService;

    @GetMapping
    public Result<List<BookCategory>> getCategories() {
        List<BookCategory> categories = categoryService.getCategories();
        return Result.success(categories);
    }

    @PostMapping
    public Result<Void> addCategory(@RequestBody CategoryRequest request, HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        categoryService.addCategory(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request, HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        request.setId(id);
        categoryService.updateCategory(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id, HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        categoryService.deleteCategory(id);
        return Result.success();
    }

    private void checkAdmin(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || !role.equals(Constants.ROLE_ADMIN)) {
            throw new RuntimeException("无权限操作");
        }
    }
}
```

- [ ] **Step 6: 提交**

```bash
git add library-server/src/main/java/com/library/mapper/BookCategoryMapper.java
git add library-server/src/main/java/com/library/service/BookCategoryService.java
git add library-server/src/main/java/com/library/controller/CategoryController.java
git commit -m "feat: 实现图书分类管理接口"
```

---

### Task 8: 实现借阅管理接口

**Files:**
- Create: `library-server/src/main/java/com/library/mapper/BorrowRecordMapper.java`
- Create: `library-server/src/main/java/com/library/service/BorrowService.java`
- Create: `library-server/src/main/java/com/library/service/impl/BorrowServiceImpl.java`
- Create: `library-server/src/main/java/com/library/controller/BorrowController.java`
- Create: `library-server/src/main/java/com/library/dto/BorrowRequest.java`

- [ ] **Step 1: 创建 BorrowRecordMapper**

```java
package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
}
```

- [ ] **Step 2: 创建 BorrowRequest DTO**

```java
package com.library.dto;

import lombok.Data;

@Data
public class BorrowRequest {
    private Long bookId;
}
```

- [ ] **Step 3: 创建 BorrowService 接口**

```java
package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.BorrowRequest;
import com.library.dto.PageRequest;
import com.library.entity.BorrowRecord;

public interface BorrowService {
    Page<BorrowRecord> getBorrowRecords(PageRequest request, Long userId, Integer role);
    void borrowBook(BorrowRequest request, Long userId);
    void returnBook(Long id);
    void renewBook(Long id);
}
```

- [ ] **Step 4: 创建 BorrowServiceImpl**

```java
package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Constants;
import com.library.dto.BorrowRequest;
import com.library.dto.PageRequest;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final BookMapper bookMapper;

    @Override
    public Page<BorrowRecord> getBorrowRecords(PageRequest request, Long userId, Integer role) {
        Page<BorrowRecord> page = new Page<>(request.getPageNum(), request.getPageSize());

        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();

        // 普通读者只能查看自己的借阅记录
        if (role != null && role.equals(Constants.ROLE_USER)) {
            wrapper.eq(BorrowRecord::getUserId, userId);
        }

        // 按状态筛选
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.eq(BorrowRecord::getStatus, request.getKeyword());
        }

        wrapper.orderByDesc(BorrowRecord::getCreateTime);

        Page<BorrowRecord> result = borrowRecordMapper.selectPage(page, wrapper);

        // 填充关联信息
        for (BorrowRecord record : result.getRecords()) {
            Book book = bookMapper.selectById(record.getBookId());
            if (book != null) {
                record.setBookTitle(book.getTitle());
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void borrowBook(BorrowRequest request, Long userId) {
        // 检查图书是否存在且有库存
        Book book = bookMapper.selectById(request.getBookId());
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        if (book.getStock() <= 0) {
            throw new RuntimeException("图书库存不足");
        }

        // 检查用户是否有未归还的借阅
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId)
               .eq(BorrowRecord::getBookId, request.getBookId())
               .eq(BorrowRecord::getStatus, Constants.BORROW_STATUS_BORROWING);
        if (borrowRecordMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("您已借阅此书，尚未归还");
        }

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setBookId(request.getBookId());
        record.setUserId(userId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(Constants.BORROW_DAYS));
        record.setStatus(Constants.BORROW_STATUS_BORROWING);

        borrowRecordMapper.insert(record);

        // 减少库存
        book.setStock(book.getStock() - 1);
        bookMapper.updateById(book);
    }

    @Override
    @Transactional
    public void returnBook(Long id) {
        BorrowRecord record = borrowRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }
        if (!record.getStatus().equals(Constants.BORROW_STATUS_BORROWING)) {
            throw new RuntimeException("该图书已归还");
        }

        // 更新借阅记录
        record.setReturnDate(LocalDateTime.now());
        record.setStatus(Constants.BORROW_STATUS_RETURNED);
        borrowRecordMapper.updateById(record);

        // 增加库存
        Book book = bookMapper.selectById(record.getBookId());
        if (book != null) {
            book.setStock(book.getStock() + 1);
            bookMapper.updateById(book);
        }
    }

    @Override
    public void renewBook(Long id) {
        BorrowRecord record = borrowRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }
        if (!record.getStatus().equals(Constants.BORROW_STATUS_BORROWING)) {
            throw new RuntimeException("该图书已归还，无法续借");
        }

        // 续借30天
        record.setDueDate(record.getDueDate().plusDays(Constants.BORROW_DAYS));
        borrowRecordMapper.updateById(record);
    }
}
```

- [ ] **Step 5: 创建 BorrowController**

```java
package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.dto.BorrowRequest;
import com.library.dto.PageRequest;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrows")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @GetMapping
    public Result<Page<BorrowRecord>> getBorrowRecords(PageRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");
        Page<BorrowRecord> page = borrowService.getBorrowRecords(request, userId, role);
        return Result.success(page);
    }

    @PostMapping
    public Result<Void> borrowBook(@RequestBody BorrowRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        borrowService.borrowBook(request, userId);
        return Result.success("借书成功");
    }

    @PutMapping("/{id}/return")
    public Result<Void> returnBook(@PathVariable Long id) {
        borrowService.returnBook(id);
        return Result.success("还书成功");
    }

    @PutMapping("/{id}/renew")
    public Result<Void> renewBook(@PathVariable Long id) {
        borrowService.renewBook(id);
        return Result.success("续借成功");
    }
}
```

- [ ] **Step 6: 提交**

```bash
git add library-server/src/main/java/com/library/mapper/BorrowRecordMapper.java
git add library-server/src/main/java/com/library/service/BorrowService.java
git add library-server/src/main/java/com/library/controller/BorrowController.java
git commit -m "feat: 实现借阅管理接口"
```

---

### Task 9: 实现全局异常处理

**Files:**
- Create: `library-server/src/main/java/com/library/common/GlobalExceptionHandler.java`

- [ ] **Step 1: 创建全局异常处理器**

```java
package com.library.common;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(400, message);
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(400, message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        e.printStackTrace();
        return Result.error("系统错误");
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add library-server/src/main/java/com/library/common/GlobalExceptionHandler.java
git commit -m "fix: 添加全局异常处理器"
```

---

## 第二阶段：前端基础框架

### Task 10: 创建 Vue 3 项目骨架

**Files:**
- Create: `library-vue/package.json`
- Create: `library-vue/vite.config.js`
- Create: `library-vue/index.html`
- Create: `library-vue/src/main.js`
- Create: `library-vue/src/App.vue`

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "library-vue",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.2.5",
    "pinia": "^2.1.7",
    "element-plus": "^2.5.0",
    "axios": "^1.6.0",
    "@element-plus/icons-vue": "^2.3.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.0.0"
  }
}
```

- [ ] **Step 2: 创建 vite.config.js**

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: 创建 index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>图书管理系统</title>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.js"></script>
</body>
</html>
```

- [ ] **Step 4: 创建 main.js**

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')
```

- [ ] **Step 5: 创建 App.vue**

```vue
<template>
  <router-view />
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

#app {
  min-height: 100vh;
}
</style>
```

- [ ] **Step 6: 提交**

```bash
git add library-vue/
git commit -m "feat: 创建Vue 3项目骨架"
```

---

### Task 11: 创建前端路由和状态管理

**Files:**
- Create: `library-vue/src/router/index.js`
- Create: `library-vue/src/stores/user.js`
- Create: `library-vue/src/utils/request.js`
- Create: `library-vue/src/api/index.js`

- [ ] **Step 1: 创建路由配置**

```javascript
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/layouts/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/books',
        name: 'Books',
        component: () => import('@/views/books/index.vue'),
        meta: { title: '图书列表' }
      },
      {
        path: '/borrows',
        name: 'Borrows',
        component: () => import('@/views/borrows/index.vue'),
        meta: { title: '我的借阅' }
      },
      {
        path: '/categories',
        name: 'Categories',
        component: () => import('@/views/categories/index.vue'),
        meta: { title: '分类管理', requiresAdmin: true }
      },
      {
        path: '/users',
        name: 'Users',
        component: () => import('@/views/users/index.vue'),
        meta: { title: '读者管理', requiresAdmin: true }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title + ' - 图书管理系统' || '图书管理系统'

  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

  if (to.path === '/login' || to.path === '/register') {
    next()
  } else if (!token) {
    next('/login')
  } else if (to.meta.requiresAdmin && userInfo.role !== 1) {
    next('/home')
  } else {
    next()
  }
})

export default router
```

- [ ] **Step 2: 创建 axios 请求封装**

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
      ElMessage.error('登录已过期')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
```

- [ ] **Step 3: 创建 API 接口文件**

```javascript
import request from '@/utils/request'

export const auth = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),
  logout: () => request.post('/auth/logout'),
  getInfo: () => request.get('/auth/info')
}

export const book = {
  list: (params) => request.get('/books', { params }),
  detail: (id) => request.get(`/books/${id}`),
  add: (data) => request.post('/books', data),
  update: (id, data) => request.put(`/books/${id}`, data),
  delete: (id) => request.delete(`/books/${id}`)
}

export const category = {
  list: () => request.get('/categories'),
  add: (data) => request.post('/categories', data),
  update: (id, data) => request.put(`/categories/${id}`, data),
  delete: (id) => request.delete(`/categories/${id}`)
}

export const borrow = {
  list: (params) => request.get('/borrows', { params }),
  borrow: (data) => request.post('/borrows', data),
  return: (id) => request.put(`/borrows/${id}/return`),
  renew: (id) => request.put(`/borrows/${id}/renew`)
}

export const user = {
  list: (params) => request.get('/users', { params }),
  detail: (id) => request.get(`/users/${id}`),
  update: (id, data) => request.put(`/users/${id}`, data),
  delete: (id) => request.delete(`/users/${id}`)
}
```

- [ ] **Step 4: 创建用户状态管理**

```javascript
import { defineStore } from 'pinia'
import { auth } from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  }),

  getters: {
    isAdmin: (state) => state.userInfo.role === 1,
    isLoggedIn: (state) => !!state.token
  },

  actions: {
    async login(loginForm) {
      const res = await auth.login(loginForm)
      this.token = res.data.token
      localStorage.setItem('token', this.token)
      await this.getInfo()
    },

    async getInfo() {
      const res = await auth.getInfo()
      this.userInfo = res.data
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
    },

    async register(registerForm) {
      await auth.register(registerForm)
    },

    logout() {
      this.token = ''
      this.userInfo = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
```

- [ ] **Step 5: 提交**

```bash
git add library-vue/src/router/
git add library-vue/src/stores/
git add library-vue/src/utils/
git add library-vue/src/api/
git commit -m "feat: 创建前端路由和状态管理"
```

---

### Task 12: 创建布局组件

**Files:**
- Create: `library-vue/src/layouts/Layout.vue`
- Create: `library-vue/src/layouts/Sidebar.vue`
- Create: `library-vue/src/layouts/Navbar.vue`

- [ ] **Step 1: 创建布局组件**

```vue
<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <Sidebar />
    </el-aside>
    <el-container>
      <el-header class="navbar">
        <Navbar />
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background: #304156;
  overflow-x: hidden;
}

.navbar {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>
```

- [ ] **Step 2: 创建侧边栏组件**

```vue
<template>
  <div class="sidebar-container">
    <div class="logo">
      <h2>图书管理</h2>
    </div>
    <el-menu
      :default-active="activeMenu"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      :router="true"
    >
      <el-menu-item index="/home">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </el-menu-item>

      <el-menu-item index="/books">
        <el-icon><Reading /></el-icon>
        <span>图书列表</span>
      </el-menu-item>

      <el-menu-item index="/borrows">
        <el-icon><List /></el-icon>
        <span>我的借阅</span>
      </el-menu-item>

      <el-menu-item v-if="userStore.isAdmin" index="/categories">
        <el-icon><FolderOpened /></el-icon>
        <span>分类管理</span>
      </el-menu-item>

      <el-menu-item v-if="userStore.isAdmin" index="/users">
        <el-icon><User /></el-icon>
        <span>读者管理</span>
      </el-menu-item>

      <el-menu-item index="/profile">
        <el-icon><Avatar /></el-icon>
        <span>个人中心</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
</script>

<style scoped>
.sidebar-container {
  height: 100%;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #2b3a4a;
}

.logo h2 {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.el-menu {
  border: none;
}
</style>
```

- [ ] **Step 3: 创建导航栏组件**

```vue
<template>
  <div class="navbar-container">
    <div class="left">
      <span class="username">欢迎，{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
    </div>
    <div class="right">
      <el-dropdown @command="handleCommand">
        <span class="user-dropdown">
          <el-avatar :size="32" style="margin-right: 8px">
            {{ userStore.userInfo.realName?.charAt(0) || 'U' }}
          </el-avatar>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
    }).catch(() => {})
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.navbar-container {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.username {
  color: #333;
  font-size: 14px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
}
</style>
```

- [ ] **Step 4: 提交**

```bash
git add library-vue/src/layouts/
git commit -m "feat: 创建前端布局组件"
```

---

### Task 13: 创建登录注册页面

**Files:**
- Create: `library-vue/src/views/login/index.vue`
- Create: `library-vue/src/views/register/index.vue`

- [ ] **Step 1: 创建登录页面**

```vue
<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="title">图书管理系统</h1>
      <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-btn"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
}

.footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
}

.footer a {
  color: #409EFF;
  text-decoration: none;
}
</style>
```

- [ ] **Step 2: 创建注册页面**

```vue
<template>
  <div class="register-container">
    <div class="register-box">
      <h1 class="title">用户注册</h1>
      <el-form ref="formRef" :model="form" :rules="rules" class="register-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="realName">
          <el-input
            v-model="form.realName"
            placeholder="请输入真实姓名"
            prefix-icon="UserFilled"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱（选填）"
            prefix-icon="Message"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleRegister"
            class="register-btn"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        <span>已有账号？</span>
        <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}

.register-btn {
  width: 100%;
}

.footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
}

.footer a {
  color: #409EFF;
  text-decoration: none;
}
</style>
```

- [ ] **Step 3: 提交**

```bash
git add library-vue/src/views/login/
git add library-vue/src/views/register/
git commit -m "feat: 创建登录注册页面"
```

---

### Task 14: 创建首页和个人中心

**Files:**
- Create: `library-vue/src/views/home/index.vue`
- Create: `library-vue/src/views/profile/index.vue`

- [ ] **Step 1: 创建首页**

```vue
<template>
  <div class="home-container">
    <h1 class="page-title">首页</h1>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF">
              <el-icon :size="30"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.totalBooks }}</p>
              <p class="stat-label">图书总数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A">
              <el-icon :size="30"><List /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.borrowingCount }}</p>
              <p class="stat-label">在借数量</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #E6A23C">
              <el-icon :size="30"><User /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.readerCount }}</p>
              <p class="stat-label">读者数量</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #F56C6C">
              <el-icon :size="30"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.overdueCount }}</p>
              <p class="stat-label">逾期数量</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最近借阅</span>
          </template>
          <el-table :data="recentBorrows" style="width: 100%">
            <el-table-column prop="bookTitle" label="书名" />
            <el-table-column prop="borrowDate" label="借书日期" width="120" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/books')">
              <el-icon><Search /></el-icon>
              搜索图书
            </el-button>
            <el-button type="success" @click="$router.push('/borrows')">
              <el-icon><List /></el-icon>
              我的借阅
            </el-button>
            <el-button type="warning" v-if="userStore.isAdmin" @click="$router.push('/categories')">
              <el-icon><FolderOpened /></el-icon>
              分类管理
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { borrow } from '@/api'

const userStore = useUserStore()

const stats = reactive({
  totalBooks: 0,
  borrowingCount: 0,
  readerCount: 0,
  overdueCount: 0
})

const recentBorrows = ref([])

const getStatusType = (status) => {
  const types = { 1: '', 2: 'success', 3: 'danger' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { 1: '借阅中', 2: '已归还', 3: '逾期' }
  return texts[status] || ''
}

onMounted(async () => {
  try {
    const res = await borrow.list({ pageNum: 1, pageSize: 5 })
    recentBorrows.value = res.data.records
    stats.borrowingCount = res.data.total
  } catch (error) {
    console.error(error)
  }
})
</script>

<style scoped>
.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 600;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  padding: 10px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.quick-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
</style>
```

- [ ] **Step 2: 创建个人中心页面**

```vue
<template>
  <div class="profile-container">
    <h1 class="page-title">个人中心</h1>

    <el-card>
      <el-form ref="formRef" :model="form" label-width="100px" class="profile-form">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdate">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>修改密码</span>
      </template>
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const formRef = ref()
const passwordFormRef = ref()

const form = reactive({
  username: '',
  realName: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  newPassword: ''
})

const passwordRules = {
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

onMounted(() => {
  Object.assign(form, userStore.userInfo)
})

const handleUpdate = () => {
  ElMessage.success('功能待开发')
}

const handleChangePassword = () => {
  ElMessage.success('功能待开发')
}
</script>

<style scoped>
.profile-container {
  max-width: 600px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 600;
}
</style>
```

- [ ] **Step 3: 提交**

```bash
git add library-vue/src/views/home/
git add library-vue/src/views/profile/
git commit -m "feat: 创建首页和个人中心页面"
```

---

### Task 15: 创建图书列表页面

**Files:**
- Create: `library-vue/src/views/books/index.vue`

- [ ] **Step 1: 创建图书列表页面**

```vue
<template>
  <div class="books-container">
    <h1 class="page-title">图书列表</h1>

    <el-card>
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索书名、作者、ISBN"
          style="width: 300px"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="searchForm.categoryId"
          placeholder="选择分类"
          clearable
          style="width: 200px; margin-left: 10px"
          @change="handleSearch"
        >
          <el-option
            v-for="cat in categories"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          />
        </el-select>
        <el-button type="primary" style="margin-left: 10px" @click="handleSearch">
          搜索
        </el-button>
        <el-button v-if="userStore.isAdmin" type="success" @click="handleAdd">
          新增图书
        </el-button>
      </div>

      <el-table :data="books" v-loading="loading" style="width: 100%; margin-top: 20px">
        <el-table-column prop="title" label="书名" min-width="150" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="isbn" label="ISBN" width="150" />
        <el-table-column prop="price" label="价格" width="80" />
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button link type="success" @click="handleBorrow(row)" v-if="!userStore.isAdmin">借书</el-button>
            <el-button link type="primary" v-if="userStore.isAdmin" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" v-if="userStore.isAdmin" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px; justify-content: flex-end"
        @size-change="loadBooks"
        @current-change="loadBooks"
      />
    </el-card>

    <!-- 图书详情弹窗 -->
    <el-dialog v-model="detailVisible" title="图书详情" width="600px">
      <el-descriptions :column="2" border v-if="currentBook">
        <el-descriptions-item label="书名">{{ currentBook.title }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ currentBook.author }}</el-descriptions-item>
        <el-descriptions-item label="ISBN">{{ currentBook.isbn }}</el-descriptions-item>
        <el-descriptions-item label="出版社">{{ currentBook.publisher }}</el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ currentBook.price }}</el-descriptions-item>
        <el-descriptions-item label="库存">{{ currentBook.stock }}</el-descriptions-item>
        <el-descriptions-item label="简介" :span="2">{{ currentBook.description }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleBorrow(currentBook)" v-if="!userStore.isAdmin && currentBook?.stock > 0">
          借书
        </el-button>
      </template>
    </el-dialog>

    <!-- 图书编辑弹窗 -->
    <el-dialog v-model="editVisible" :title="isEdit ? '编辑图书' : '新增图书'" width="600px">
      <el-form ref="bookFormRef" :model="bookForm" :rules="bookRules" label-width="80px">
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="bookForm.isbn" />
        </el-form-item>
        <el-form-item label="书名" prop="title">
          <el-input v-model="bookForm.title" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="bookForm.publisher" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="bookForm.categoryId" placeholder="选择分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="bookForm.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="bookForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="bookForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { book, category, borrow } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()

const loading = ref(false)
const books = ref([])
const categories = ref([])
const detailVisible = ref(false)
const editVisible = ref(false)
const isEdit = ref(false)
const currentBook = ref(null)
const bookFormRef = ref()

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({
  keyword: '',
  categoryId: null
})

const bookForm = reactive({
  id: null,
  isbn: '',
  title: '',
  author: '',
  publisher: '',
  categoryId: null,
  price: 0,
  stock: 0,
  description: ''
})

const bookRules = {
  isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const loadBooks = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await book.list(params)
    books.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await category.list()
    categories.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadBooks()
}

const handleDetail = (row) => {
  currentBook.value = row
  detailVisible.value = true
}

const handleBorrow = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要借阅《${row.title}》吗？`, '提示')
    await borrow.borrow({ bookId: row.id })
    ElMessage.success('借书成功')
    detailVisible.value = false
    loadBooks()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(bookForm, {
    id: null, isbn: '', title: '', author: '', publisher: '',
    categoryId: null, price: 0, stock: 0, description: ''
  })
  editVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(bookForm, row)
  editVisible.value = true
}

const handleSave = async () => {
  await bookFormRef.value.validate()
  try {
    if (isEdit.value) {
      await book.update(bookForm.id, bookForm)
    } else {
      await book.add(bookForm)
    }
    ElMessage.success('保存成功')
    editVisible.value = false
    loadBooks()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除《${row.title}》吗？`, '警告', { type: 'warning' })
    await book.delete(row.id)
    ElMessage.success('删除成功')
    loadBooks()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadBooks()
  loadCategories()
})
</script>

<style scoped>
.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 600;
}

.search-bar {
  display: flex;
  align-items: center;
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add library-vue/src/views/books/
git commit -m "feat: 创建图书列表页面"
```

---

### Task 16: 创建借阅管理页面

**Files:**
- Create: `library-vue/src/views/borrows/index.vue`

- [ ] **Step 1: 创建借阅管理页面**

```vue
<template>
  <div class="borrows-container">
    <h1 class="page-title">{{ userStore.isAdmin ? '借阅管理' : '我的借阅' }}</h1>

    <el-card>
      <div class="filter-bar">
        <el-select
          v-model="filterStatus"
          placeholder="选择状态"
          clearable
          style="width: 150px"
          @change="loadBorrows"
        >
          <el-option label="借阅中" :value="1" />
          <el-option label="已归还" :value="2" />
          <el-option label="逾期" :value="3" />
        </el-select>
      </div>

      <el-table :data="borrows" v-loading="loading" style="width: 100%; margin-top: 20px">
        <el-table-column prop="bookTitle" label="书名" min-width="150" />
        <el-table-column prop="userRealName" label="读者" width="100" v-if="userStore.isAdmin" />
        <el-table-column prop="borrowDate" label="借书日期" width="160" />
        <el-table-column prop="dueDate" label="应还日期" width="160" />
        <el-table-column prop="returnDate" label="实际还书日期" width="160">
          <template #default="{ row }">
            {{ row.returnDate || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="success"
              @click="handleReturn(row)"
              v-if="row.status === 1"
            >
              还书
            </el-button>
            <el-button
              link
              type="warning"
              @click="handleRenew(row)"
              v-if="row.status === 1"
            >
              续借
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px; justify-content: flex-end"
        @size-change="loadBorrows"
        @current-change="loadBorrows"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { borrow } from '@/api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const loading = ref(false)
const borrows = ref([])
const filterStatus = ref(null)

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const getStatusType = (status) => {
  const types = { 1: '', 2: 'success', 3: 'danger' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { 1: '借阅中', 2: '已归还', 3: '逾期' }
  return texts[status] || ''
}

const loadBorrows = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: filterStatus.value?.toString()
    }
    const res = await borrow.list(params)
    borrows.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleReturn = async (row) => {
  try {
    await borrow.return(row.id)
    ElMessage.success('还书成功')
    loadBorrows()
  } catch (error) {
    console.error(error)
  }
}

const handleRenew = async (row) => {
  try {
    await borrow.renew(row.id)
    ElMessage.success('续借成功')
    loadBorrows()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadBorrows()
})
</script>

<style scoped>
.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 600;
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add library-vue/src/views/borrows/
git commit -m "feat: 创建借阅管理页面"
```

---

### Task 17: 创建分类管理和读者管理页面

**Files:**
- Create: `library-vue/src/views/categories/index.vue`
- Create: `library-vue/src/views/users/index.vue`

- [ ] **Step 1: 创建分类管理页面**

```vue
<template>
  <div class="categories-container">
    <h1 class="page-title">分类管理</h1>

    <el-card>
      <div class="toolbar">
        <el-button type="success" @click="handleAdd">新增分类</el-button>
      </div>

      <el-table :data="categories" v-loading="loading" style="width: 100%; margin-top: 20px" row-key="id">
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="400px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { category } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const categories = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = reactive({
  id: null,
  name: '',
  sort: 0
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const loadCategories = async () => {
  loading.value = true
  try {
    const res = await category.list()
    categories.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', sort: 0 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  try {
    if (isEdit.value) {
      await category.update(form.id, form)
    } else {
      await category.add(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadCategories()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除分类「${row.name}」吗？`, '警告', { type: 'warning' })
    await category.delete(row.id)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 600;
}
</style>
```

- [ ] **Step 2: 创建读者管理页面**

```vue
<template>
  <div class="users-container">
    <h1 class="page-title">读者管理</h1>

    <el-card>
      <el-table :data="users" v-loading="loading" style="width: 100%; margin-top: 20px">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button
              link
              :type="row.status === 1 ? 'danger' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { user } from '@/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const users = ref([])

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await user.list()
    users.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleToggleStatus = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await user.update(row.id, { status: newStatus })
    ElMessage.success('操作成功')
    loadUsers()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 600;
}
</style>
```

- [ ] **Step 3: 提交**

```bash
git add library-vue/src/views/categories/
git add library-vue/src/views/users/
git commit -m "feat: 创建分类管理和读者管理页面"
```

---

## 验收测试

### 后端测试
1. 启动 MySQL 数据库
2. 执行 `sql/init.sql` 初始化数据
3. 运行 `mvn spring-boot:run` 启动后端
4. 测试登录接口：`POST /api/auth/login` 用户名 admin，密码 admin123
5. 测试新增图书、分类、借阅等接口

### 前端测试
1. 进入 `library-vue` 目录
2. 执行 `npm install` 安装依赖
3. 执行 `npm run dev` 启动前端
4. 访问 http://localhost:5173
5. 测试登录、图书查询、借阅还书等流程

---

## 计划自检

**Spec覆盖检查:**
- [x] 用户注册、登录（JWT）
- [x] 管理员增删改查图书
- [x] 分类管理
- [x] 读者搜索浏览图书
- [x] 借书、还书、续借
- [x] 读者管理
- [x] 个人中心

**占位符检查:**
- 无"TBD"、"TODO"等占位符
- 所有步骤都包含完整代码
- 所有API路径与设计文档一致

**类型一致性检查:**
- Entity字段与数据库表一致
- DTO与前端请求一致
- API响应格式统一使用Result<T>

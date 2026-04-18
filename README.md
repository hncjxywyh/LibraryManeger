# LibraryManager 图书馆管理系统

一个功能完整的全栈图书馆管理系统，基于 Spring Boot 3.2 + Vue 3 构建，支持图书管理、借阅管理、用户权限控制等功能。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2、Java 17、MyBatis-Plus 3.5、JWT (jjwt 0.12)、BCrypt |
| 前端 | Vue 3、Vite 5、Pinia、Vue Router 4、Element Plus、Axios |
| 数据库 | MySQL 8.0 |
| 构建 | Maven (后端)、npm (前端) |

## 项目结构

```
LibraryManager/
├── library-server/          # Spring Boot 后端
│   └── src/main/java/com/library/
│       ├── controller/       # REST API 控制器
│       ├── service/          # 业务逻辑层
│       ├── mapper/           # MyBatis-Plus 数据访问层
│       ├── entity/           # 实体类
│       ├── dto/              # 数据传输对象
│       ├── security/         # JWT 认证过滤器
│       ├── config/           # 配置类
│       └── common/           # 公共类（Result、Constants等）
├── library-vue/             # Vue 3 前端
│   └── src/
│       ├── api/              # API 接口封装
│       ├── views/            # 页面组件
│       ├── stores/            # Pinia 状态管理
│       ├── router/           # Vue Router 配置
│       ├── layouts/           # 布局组件
│       └── utils/             # 工具函数（Axios 实例等）
└── README.md
```

## 功能模块

### 用户认证
- 用户注册、登录、退出
- JWT Token 认证（7天有效期）
- BCrypt 密码加密存储
- 登录状态持久化（localStorage）

### 图书管理
- 图书列表（分页、关键词搜索、分类筛选）
- 图书详情查看
- 图书新增、编辑、删除
- ISBN 号唯一性校验
- 库存管理

### 借阅管理
- 借书（自动设置30天借期）
- 还书
- 续借
- 借阅记录查询
- 逾期状态自动标记

### 分类管理
- 图书分类的增删改查
- 层级分类结构

### 用户管理
- 用户列表（分页）
- 用户信息修改
- 密码修改
- 账户状态启用/禁用

### 个人中心
- 查看个人信息
- 修改个人资料（姓名、电话、邮箱）
- 修改登录密码

## 角色权限

系统内置两种角色：

| 角色 | Role 值 | 权限 |
|------|---------|------|
| 管理员 | 1 | 图书/分类/用户管理、借阅管理、修改任意用户信息 |
| 普通用户 | 2 | 借书/还书/续借、修改个人信息 |

### 前端路由权限
- `/login`、`/register` — 公开访问
- `/users`、`/categories` — 仅管理员可见
- 其他页面 — 需登录后访问

## API 接口

### 认证 `/api/auth`
| 方法 | 路径 | 说明 | 公开 |
|------|------|------|------|
| POST | /api/auth/login | 用户登录 | ✅ |
| POST | /api/auth/register | 用户注册 | ✅ |
| POST | /api/auth/logout | 退出登录 | |
| GET | /api/auth/getInfo | 获取当前用户信息 | |

### 图书 `/api/books`
| 方法 | 路径 | 说明 | 公开 |
|------|------|------|------|
| GET | /api/books | 分页获取图书列表 | ✅ |
| GET | /api/books/{id} | 获取图书详情 | ✅ |
| POST | /api/books | 新增图书 | |
| PUT | /api/books/{id} | 更新图书 | |
| DELETE | /api/books/{id} | 删除图书 | |

### 借阅 `/api/borrows`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/borrows | 分页获取借阅记录 |
| POST | /api/borrows | 借书 |
| PUT | /api/borrows/{id}/return | 还书 |
| PUT | /api/borrows/{id}/renew | 续借 |

### 分类 `/api/categories`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/categories | 获取分类列表 |
| POST | /api/categories | 新增分类 |
| PUT | /api/categories/{id} | 更新分类 |
| DELETE | /api/categories/{id} | 删除分类 |

### 用户 `/api/users`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/users | 分页获取用户列表 |
| PUT | /api/users/{id} | 更新用户信息（本人或管理员） |
| PUT | /api/users/{id}/password | 修改密码（本人或管理员） |

## 数据库模型

### sys_user（用户表）
```
id, username, password, real_name, phone, email, role, status, create_time, update_time
```

### book（图书表）
```
id, isbn, title, author, publisher, publish_date, category_id, price, stock, description, cover_url, status, create_time, update_time
```

### book_category（分类表）
```
id, name, parent_id, sort, create_time, update_time
```

### borrow_record（借阅记录表）
```
id, book_id, user_id, borrow_date, due_date, return_date, status, create_time, update_time
```

- `status`: 1=借阅中, 2=已归还, 3=逾期

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+

### 1. 数据库初始化

```bash
mysql -u root -p < library-server/src/main/resources/sql/init.sql
```

确保 MySQL 中已创建 `library` 数据库。

### 2. 启动后端

```bash
cd library-server
mvn spring-boot:run
```

后端服务运行在 http://localhost:8080

### 3. 启动前端

```bash
cd library-vue
npm install
npm run dev
```

前端服务运行在 http://localhost:5173

### 4. 访问系统

打开浏览器访问 http://localhost:5173

### 默认管理员账号
- 用户名: `admin`
- 密码: `admin123`

## 项目特点

- **前后端分离**：前端 Vite 开发服务器通过代理将 `/api` 请求转发到后端，解决跨域问题
- **JWT 无状态认证**：服务端不存储 session，每个请求携带 Token 验证身份
- **MyBatis-Plus 简化 CRUD**：继承 `BaseMapper` 自动生成增删改查，配合 `MetaObjectHandler` 自动填充创建/更新时间
- **细粒度权限控制**：后端每个接口独立校验权限，前端路由根据角色动态渲染菜单
- **统一响应格式**：所有 API 返回 `Result(code, message, data)` 结构
- **全局异常处理**：`@RestControllerAdvice` 统一捕获 `RuntimeException` 并返回友好错误信息

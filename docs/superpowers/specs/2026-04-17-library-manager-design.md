# 图书管理系统设计文档

## 1. 项目概述

### 1.1 项目名称
图书管理系统（Library Manager）

### 1.2 项目类型
前后端分离的全栈项目

### 1.3 核心功能
- 图书管理（增删改查、分类、搜索）
- 借阅管理（借书、还书、续借）
- 读者管理（注册、登录、个人中心）
- JWT 无状态认证

### 1.4 技术栈

| 层级 | 技术选型 |
|------|----------|
| 后端框架 | Spring Boot 3.x + MyBatis-Plus |
| 数据库 | MySQL 8.x |
| 认证方案 | JWT（无状态 Token） |
| 前端框架 | Vue 3 + Vite |
| UI 组件库 | Element Plus |
| 状态管理 | Pinia |
| 路由管理 | Vue Router 4 |

### 1.5 用户角色
- **管理员**：管理图书、图书分类、管理借阅、查看数据统计
- **读者**：注册登录、查询图书、借阅还书、个人借阅记录

---

## 2. 数据库设计

### 2.1 用户表（sys_user）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名（唯一） |
| password | VARCHAR(100) | 密码（加密存储） |
| real_name | VARCHAR(50) | 真实姓名 |
| phone | VARCHAR(20) | 手机号 |
| email | VARCHAR(100) | 邮箱 |
| role | TINYINT | 角色：1-管理员 2-读者 |
| status | TINYINT | 状态：0-禁用 1-正常 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 2.2 图书表（book）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| isbn | VARCHAR(50) | ISBN（唯一） |
| title | VARCHAR(200) | 书名 |
| author | VARCHAR(100) | 作者 |
| publisher | VARCHAR(100) | 出版社 |
| publish_date | DATE | 出版日期 |
| category_id | BIGINT | 分类ID |
| price | DECIMAL(10,2) | 价格 |
| stock | INT | 库存数量 |
| description | TEXT | 简介 |
| cover_url | VARCHAR(500) | 封面图URL |
| status | TINYINT | 状态：0-下架 1-上架 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 2.3 图书分类表（book_category）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(50) | 分类名称 |
| parent_id | BIGINT | 父分类ID（顶级为0） |
| sort | INT | 排序 |
| create_time | DATETIME | 创建时间 |

### 2.4 借阅记录表（borrow_record）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| book_id | BIGINT | 图书ID |
| user_id | BIGINT | 读者ID |
| borrow_date | DATETIME | 借书日期 |
| due_date | DATETIME | 应还日期 |
| return_date | DATETIME | 实际还书日期（可为NULL） |
| status | TINYINT | 状态：1-借阅中 2-已归还 3-逾期 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

---

## 3. 后端 API 设计

### 3.1 认证模块
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/auth/register | POST | 读者注册 |
| /api/auth/login | POST | 登录 |
| /api/auth/logout | POST | 登出 |
| /api/auth/info | GET | 获取当前用户信息 |

### 3.2 图书模块
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/books | GET | 分页查询图书（支持分类、搜索） |
| /api/books/{id} | GET | 获取图书详情 |
| /api/books | POST | 新增图书（管理员） |
| /api/books/{id} | PUT | 修改图书（管理员） |
| /api/books/{id} | DELETE | 删除图书（管理员） |

### 3.3 分类模块
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/categories | GET | 获取全部分类树 |
| /api/categories | POST | 新增分类（管理员） |
| /api/categories/{id} | PUT | 修改分类（管理员） |
| /api/categories/{id} | DELETE | 删除分类（管理员） |

### 3.4 借阅模块
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/borrows | GET | 查询借阅记录（分页） |
| /api/borrows | POST | 借书 |
| /api/borrows/{id}/return | PUT | 还书 |
| /api/borrows/{id}/renew | PUT | 续借 |

### 3.5 读者管理模块
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/users | GET | 查询读者列表（管理员） |
| /api/users/{id} | GET | 读者详情（管理员） |
| /api/users/{id} | PUT | 修改读者（管理员） |
| /api/users/{id} | DELETE | 删除读者（管理员） |

---

## 4. 前端页面设计

### 4.1 登录/注册页
- 登录表单：用户名、密码、登录按钮
- 注册链接：跳转到注册页
- 注册表单：用户名、密码、确认密码、真实姓名、手机号

### 4.2 首页/Dashboard
- 管理员视角：借阅统计、图书统计、待处理借阅、热门图书
- 读者视角：我的借阅、推荐图书、图书搜索

### 4.3 图书管理页（管理员）
- 图书列表：表格展示，支持分页、搜索、筛选
- 操作：新增图书、编辑、删除、上架/下架
- 图书表单弹窗：ISBN、书名、作者、出版社、分类、价格、库存、封面

### 4.4 图书查询页（读者）
- 搜索栏：书名、作者、ISBN
- 分类筛选：左侧分类树
- 图书列表：卡片/列表切换展示
- 图书详情弹窗：显示完整信息、借阅按钮

### 4.5 借阅管理页
- 管理员：所有借阅记录，支持按状态筛选
- 读者：我的借阅记录

### 4.6 分类管理页（管理员）
- 分类树形列表
- 新增/编辑/删除分类

### 4.7 读者管理页（管理员）
- 读者列表
- 查看详情、禁用/启用

### 4.8 个人中心
- 个人信息展示与修改
- 密码修改
- 借阅历史

---

## 5. 项目结构

### 5.1 后端结构（Spring Boot）
```
library-manager/
├── src/main/java/com/library/
│   ├── LibraryApplication.java
│   ├── config/           # 配置类
│   ├── controller/       # 控制器
│   ├── service/         # 服务层
│   ├── mapper/          # MyBatis-Plus Mapper
│   ├── entity/          # 实体类
│   ├── dto/             # 数据传输对象
│   ├── common/          # 公共类（结果封装、异常处理）
│   └── security/         # JWT安全相关
├── src/main/resources/
│   └── application.yml
└── pom.xml
```

### 5.2 前端结构（Vue 3）
```
library-vue/
├── src/
│   ├── api/             # API 请求封装
│   ├── assets/          # 静态资源
│   ├── components/      # 公共组件
│   ├── layouts/         # 布局组件
│   ├── router/          # 路由配置
│   ├── stores/          # Pinia 状态管理
│   ├── utils/           # 工具函数
│   ├── views/           # 页面组件
│   ├── App.vue
│   └── main.js
├── index.html
├── vite.config.js
└── package.json
```

---

## 6. 安全设计

### 6.1 JWT 认证
- 登录成功后返回 access_token
- 前端请求头携带：Authorization: Bearer <token>
- Token 有效期：7 天
- Token 续期：借书时自动检查并续期

### 6.2 密码加密
- 使用 BCrypt 加密存储

### 6.3 权限控制
- 后端接口权限注解：@RequiresRole("admin")、@RequiresRole("user")
- 前端路由守卫：检查登录状态和角色

---

## 7. 实施计划（建议分阶段）

### 第一阶段：基础框架搭建
1. 搭建 Spring Boot 项目，配置 MyBatis-Plus
2. 搭建 Vue 3 + Vite 项目，配置 Element Plus
3. 实现数据库表创建

### 第二阶段：核心功能
1. 实现用户注册、登录（JWT）
2. 实现图书增删改查
3. 实现图书分类管理

### 第三阶段：业务功能
1. 实现借阅功能
2. 实现读者管理
3. 实现个人中心

### 第四阶段：优化完善
1. 搜索、筛选、分页
2. 数据统计
3. UI 细节优化

---

## 8. 验收标准

- [ ] 用户可以注册、登录、登出
- [ ] 管理员可以增删改查图书
- [ ] 管理员可以管理图书分类
- [ ] 读者可以搜索和浏览图书
- [ ] 读者可以借书、还书、续借
- [ ] 借阅记录完整追踪
- [ ] 响应式布局，支持移动端
- [ ] 界面美观，设计感强

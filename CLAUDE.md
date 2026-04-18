# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A full-stack library management system with **Spring Boot 3.2** backend and **Vue 3 + Vite** frontend, using JWT authentication.

## Architecture

```
LibraryManager/
├── library-server/     # Spring Boot backend (Java 17, MyBatis-Plus, JWT)
└── library-vue/         # Vue 3 frontend (Element Plus, Pinia, Vue Router)
```

### Backend (library-server)

- **Layered architecture**: Controller → Service → Mapper
- **Authentication**: JWT via `JwtAuthFilter` ( JwtUtil for token generation)
- **Security**: BCrypt password encoding, role-based access (ROLE_ADMIN=1, ROLE_USER=2)
- **API prefix**: `/api/*` (auth, books, categories, borrows)
- **Allowed public paths**: `/api/auth/login`, `/api/auth/register`, `/api/books` (GET only)

### Frontend (library-vue)

- **State management**: Pinia store (`stores/user.js`) with token in localStorage
- **HTTP client**: Axios with interceptors (`utils/request.js`) - auto-attaches JWT Bearer token
- **Routing**: Vue Router 4 with navigation guard checking `localStorage.token` and `userInfo.role`
- **UI**: Element Plus components, all icons registered globally in `main.js`

## Commands

### Backend
```bash
cd library-server
mvn spring-boot:run          # Start backend on port 8080
mvn clean package            # Build JAR
```

### Frontend
```bash
cd library-vue
npm install                  # Install dependencies
npm run dev                  # Start dev server on port 5173
npm run build                # Production build
```

### Database
```bash
mysql -u root -p < library-server/src/main/resources/sql/init.sql
```
Requires MySQL running with `library` database created.

## Default Admin Account
- Username: `admin`
- Password: `admin123`

## Key Implementation Notes

- **CORS**: Frontend proxies `/api` requests to `http://localhost:8080` (vite.config.js)
- **MyBatis-Plus**: Auto-fill for `createTime`/`updateTime` via `MetaObjectHandler`
- **JWT expiration**: 7 days (604800000ms), configured in `application.yml`
- **Borrow period**: 30 days (`Constants.BORROW_DAYS`)
- **Admin-only write operations**: Controllers check `role` from JWT filter-attached request attribute
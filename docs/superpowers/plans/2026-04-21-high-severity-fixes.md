# High-Severity Bugs Fix Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fix 8 high-severity bugs across backend (Java/Spring) and frontend (Vue 3).

**Architecture:** All fixes are surgical — one bug per task, no refactoring. Backend uses MyBatis-Plus with `LambdaQueryWrapper` and `@Transactional`. Frontend uses Vue 3 + Element Plus. Concurrent borrow uses `SELECT ... FOR UPDATE` via MyBatis-Plus `@Select` annotation.

---

## Files to Modify

### Backend
- `library-server/src/main/java/com/library/entity/BorrowRecord.java` — add `renewCount` field
- `library-server/src/main/java/com/library/service/impl/BorrowServiceImpl.java` — fix renew check, prevent overdue renew, add renew limit, fix status field, add FOR UPDATE lock
- `library-server/src/main/java/com/library/service/impl/BorrowRemindServiceImpl.java` — set OVERDUE status
- `library-server/src/main/java/com/library/service/impl/ReservationServiceImpl.java` — fix position race condition
- `library-server/src/main/java/com/library/security/JwtBlacklist.java` — NEW: in-memory token blacklist
- `library-server/src/main/java/com/library/security/JwtAuthFilter.java` — check blacklist before validate
- `library-server/src/main/java/com/library/controller/AuthController.java` — add token to blacklist on logout
- `library-server/src/main/java/com/library/dto/BorrowRequest.java` — add @NotNull on bookId
- `library-server/src/main/java/com/library/controller/BorrowController.java` — add @Valid annotation
- `library-server/src/main/java/com/library/dto/PageRequest.java` — add `status` field
- `library-server/src/main/java/com/library/mapper/BookMapper.java` — add selectForUpdate method
- `library-server/src/main/java/com/library/mapper/BookReservationMapper.java` — add getNextPositionForUpdate method
- `library-server/src/main/resources/sql/library.sql` — add `renew_count` column

### Frontend
- `library-vue/src/views/borrows/index.vue` — fix filter param name, add error handling
- `library-vue/src/views/users/index.vue` — add pagination
- `library-vue/src/views/login/index.vue` — show error message on failure
- `library-vue/src/views/register/index.vue` — show error message on failure

---

## Task 1: Backend — Set OVERDUE status when book becomes overdue

**Files:**
- Modify: `library-server/src/main/java/com/library/service/impl/BorrowRemindServiceImpl.java`
- Modify: `library-server/src/main/java/com/library/service/impl/BorrowServiceImpl.java`

**Root cause:** `BorrowRemindServiceImpl.sendOverdueRemind()` sends overdue reminders but never updates `borrow_record.status` to `BORROW_STATUS_OVERDUE` (3). The status stays 1 (BORROWING) forever. Meanwhile `BorrowServiceImpl.returnBook()` only accepts records with `status == BORROWING`, so overdue books can be returned — but the UI can't distinguish "借阅中" from "逾期" without a separate status check.

**Fix Part A:** In `sendOverdueRemind()`, add status update to OVERDUE when sending the first overdue reminder.

**Fix Part B:** In `returnBook()`, allow return of both BORROWING and OVERDUE status records.

- [ ] **Step 1: Modify BorrowRemindServiceImpl — add status update in sendOverdueRemind()**

Find the `sendOverdueRemind()` method. After the `saveRemindLog()` call (around line 105), add:

```java
// Update overdue status
LambdaUpdateWrapper<BorrowRecord> statusWrapper = new LambdaUpdateWrapper<>();
statusWrapper.eq(BorrowRecord::getId, record.getId())
              .set(BorrowRecord::getStatus, Constants.BORROW_STATUS_OVERDUE);
borrowRecordMapper.update(null, statusWrapper);
```

The new `sendOverdueRemind()` full method (lines 80-118 after fix):
```java
private void sendOverdueRemind(BorrowRecord record, LocalDate today) {
    Book book = bookMapper.selectById(record.getBookId());
    long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(record.getDueDate().toLocalDate(), today);

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

        LambdaUpdateWrapper<BorrowRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BorrowRecord::getId, record.getId())
                     .set(BorrowRecord::getOverdueRemindCount, record.getOverdueRemindCount() + 1);
        borrowRecordMapper.update(null, updateWrapper);

        // Update overdue status
        LambdaUpdateWrapper<BorrowRecord> statusWrapper = new LambdaUpdateWrapper<>();
        statusWrapper.eq(BorrowRecord::getId, record.getId())
                     .set(BorrowRecord::getStatus, Constants.BORROW_STATUS_OVERDUE);
        borrowRecordMapper.update(null, statusWrapper);

        saveRemindLog(record, Constants.REMIND_TYPE_OVERDUE, (int) overdueDays);
    }
}
```

- [ ] **Step 2: Modify BorrowServiceImpl — allow return of OVERDUE records**

In `BorrowServiceImpl.java`, in `returnBook()` at line 99, change:
```java
if (!record.getStatus().equals(Constants.BORROW_STATUS_BORROWING)) {
```
To:
```java
if (!record.getStatus().equals(Constants.BORROW_STATUS_BORROWING)
    && !record.getStatus().equals(Constants.BORROW_STATUS_OVERDUE)) {
```

- [ ] **Step 3: Commit**

```bash
git add library-server/src/main/java/com/library/service/impl/BorrowRemindServiceImpl.java library-server/src/main/java/com/library/service/impl/BorrowServiceImpl.java
git commit -m "fix: set OVERDUE status when book becomes overdue and allow return of overdue records"
```

---

## Task 2: Backend — Prevent renewing overdue books + add max 2 renewals limit

**Files:**
- Modify: `library-server/src/main/java/com/library/entity/BorrowRecord.java`
- Modify: `library-server/src/main/java/com/library/service/impl/BorrowServiceImpl.java`
- Modify: `library-server/src/main/resources/sql/library.sql`

**Root cause:** `renewBook()` only checks `status == BORROWING` but does not check if `dueDate < now` (already overdue). It also has no renewal count limit.

**Fix:** Add `renewCount` field, initialize to 0 on borrow, check due date is not past, and reject after 2 renewals.

- [ ] **Step 1: Add renewCount field to BorrowRecord entity**

In `BorrowRecord.java`, add after line 37 (`overdueRemindCount`):
```java
private Integer renewCount; // 续借次数
```

- [ ] **Step 2: Initialize renewCount to 0 in borrowBook()**

In `BorrowServiceImpl.java`, in `borrowBook()` after line 81 (after `setStatus`), add:
```java
record.setRenewCount(0);
```

- [ ] **Step 3: Fix renewBook() with overdue check and limit**

Replace the entire `renewBook()` method (lines 119-134) with:
```java
@Override
public void renewBook(Long id, Long userId) {
    BorrowRecord record = borrowRecordMapper.selectById(id);
    if (record == null) {
        throw new RuntimeException("借阅记录不存在");
    }
    if (!record.getUserId().equals(userId)) {
        throw new RuntimeException("无权操作此借阅记录");
    }
    if (!record.getStatus().equals(Constants.BORROW_STATUS_BORROWING)) {
        throw new RuntimeException("该图书已归还或已逾期，无法续借");
    }
    // Prevent renew if already overdue
    if (record.getDueDate().isBefore(LocalDateTime.now())) {
        throw new RuntimeException("图书已逾期，请先归还再借");
    }
    // Max 2 renewals per record
    if (record.getRenewCount() != null && record.getRenewCount() >= 2) {
        throw new RuntimeException("该借阅记录已达到最大续借次数");
    }

    int newCount = (record.getRenewCount() == null) ? 1 : record.getRenewCount() + 1;
    record.setRenewCount(newCount);
    record.setDueDate(record.getDueDate().plusDays(Constants.BORROW_DAYS));
    borrowRecordMapper.updateById(record);
}
```

- [ ] **Step 4: Add renew_count column to database schema**

In `library-server/src/main/resources/sql/library.sql`, find the `borrow_record` table and add after `overdue_fee` column:
```sql
`renew_count` int DEFAULT 0 COMMENT '续借次数',
```

- [ ] **Step 5: Commit**

```bash
git add library-server/src/main/java/com/library/entity/BorrowRecord.java library-server/src/main/java/com/library/service/impl/BorrowServiceImpl.java library-server/src/main/resources/sql/library.sql
git commit -m "fix: prevent renewing overdue books and limit renewals to 2 per record"
```

---

## Task 3: Backend — Fix race condition in borrowBook()

**Files:**
- Modify: `library-server/src/main/java/com/library/mapper/BookMapper.java`
- Modify: `library-server/src/main/java/com/library/service/impl/BorrowServiceImpl.java`

**Root cause:** `borrowBook()` checks `book.getStock() > 0` then decrements. Two concurrent requests can both pass the check before either decrements.

**Fix:** Use `SELECT ... FOR UPDATE` to lock the book row during the transaction.

- [ ] **Step 1: Add selectForUpdate to BookMapper**

Read `library-server/src/main/java/com/library/mapper/BookMapper.java`. If `selectForUpdate` does not exist, add:
```java
@Select("SELECT * FROM book WHERE id = #{id} FOR UPDATE")
Book selectForUpdate(Long id);
```

- [ ] **Step 2: Modify borrowBook() to use pessimistic lock**

In `BorrowServiceImpl.java`, replace the start of `borrowBook()`:
```java
@Override
@Transactional
public void borrowBook(BorrowRequest request, Long userId) {
    // Use FOR UPDATE to lock the book row and prevent race conditions
    Book book = bookMapper.selectForUpdate(request.getBookId());
    if (book == null) {
        throw new RuntimeException("图书不存在");
    }
    if (book.getStock() <= 0) {
        throw new RuntimeException("图书库存不足");
    }
    // ... rest unchanged from here (check existing borrow, insert record, decrement stock)
}
```

The rest of the method (check existing borrow, insert record, decrement stock) remains the same.

- [ ] **Step 3: Commit**

```bash
git add library-server/src/main/java/com/library/mapper/BookMapper.java library-server/src/main/java/com/library/service/impl/BorrowServiceImpl.java
git commit -m "fix: use SELECT FOR UPDATE to prevent concurrent borrow race condition"
```

---

## Task 4: Backend — Fix race condition in createReservation() position

**Files:**
- Modify: `library-server/src/main/java/com/library/mapper/BookReservationMapper.java`
- Modify: `library-server/src/main/java/com/library/service/impl/ReservationServiceImpl.java`

**Root cause:** `createReservation()` does `SELECT MAX(position) ... LIMIT 1` then `+1`. Two concurrent requests can get the same max and same next position.

**Fix:** Add `getNextPositionForUpdate` using `FOR UPDATE`, called inside the existing `@Transactional` method.

- [ ] **Step 1: Add atomic position method to BookReservationMapper**

Read `library-server/src/main/java/com/library/mapper/BookReservationMapper.java`. Add:
```java
@Select("SELECT COALESCE(MAX(br.position), 0) + 1 FROM book_reservation br WHERE br.book_id = #{bookId} AND br.status = #{status} FOR UPDATE")
Integer getNextPositionForUpdate(Long bookId, Integer status);
```

- [ ] **Step 2: Replace position-getting logic in createReservation()**

In `ReservationServiceImpl.java`, replace lines 67-75 (the "获取当前最大position" block):
```java
// 获取当前最大position
LambdaQueryWrapper<BookReservation> positionWrapper = new LambdaQueryWrapper<>();
positionWrapper.eq(BookReservation::getBookId, bookId)
        .eq(BookReservation::getStatus, RESERVATION_STATUS_PENDING)
        .select(BookReservation::getPosition)
        .orderByDesc(BookReservation::getPosition)
        .last("LIMIT 1");
BookReservation maxPositionReservation = reservationMapper.selectOne(positionWrapper);
int nextPosition = (maxPositionReservation == null) ? 1 : maxPositionReservation.getPosition() + 1;
```
With:
```java
// Atomically get next position with FOR UPDATE lock
Integer nextPosition = reservationMapper.getNextPositionForUpdate(bookId, RESERVATION_STATUS_PENDING);
if (nextPosition == null) {
    nextPosition = 1;
}
```

- [ ] **Step 3: Commit**

```bash
git add library-server/src/main/java/com/library/mapper/BookReservationMapper.java library-server/src/main/java/com/library/service/impl/ReservationServiceImpl.java
git commit -m "fix: use atomic position assignment to prevent concurrent reservation race condition"
```

---

## Task 5: Backend — JWT logout token invalidation

**Files:**
- Create: `library-server/src/main/java/com/library/security/JwtBlacklist.java`
- Modify: `library-server/src/main/java/com/library/security/JwtAuthFilter.java`
- Modify: `library-server/src/main/java/com/library/controller/AuthController.java`

**Root cause:** `AuthController.logout()` returns success without doing anything. The JWT token remains valid until its 7-day natural expiration.

**Fix:** In-memory `ConcurrentHashMap` set of blacklisted tokens. `JwtAuthFilter` checks the blacklist before `jwtUtil.validateToken()`. `AuthController.logout()` adds the token to the blacklist.

- [ ] **Step 1: Create JwtBlacklist**

**Create:** `library-server/src/main/java/com/library/security/JwtBlacklist.java`
```java
package com.library.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtBlacklist {
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void add(String token) {
        blacklistedTokens.add(token);
    }

    public boolean contains(String token) {
        return blacklistedTokens.contains(token);
    }
}
```

- [ ] **Step 2: Modify JwtAuthFilter to check blacklist**

In `JwtAuthFilter.java`:
```java
private final JwtBlacklist jwtBlacklist;  // add this field (constructor already uses @RequiredArgsConstructor)
```

In `doFilterInternal()`, after `extractToken()` and before `jwtUtil.validateToken()`:
```java
if (jwtBlacklist.contains(token)) {
    sendUnauthorizedResponse(response, "Token已失效，请重新登录");
    return;
}
```

Full changed section:
```java
String token = extractToken(request);
// ...
if (jwtBlacklist.contains(token)) {
    sendUnauthorizedResponse(response, "Token已失效，请重新登录");
    return;
}
if (!StringUtils.hasText(token) || !jwtUtil.validateToken(token)) {
```

Also change the direct `ObjectMapper` instantiation at line 22 to use constructor injection — but this is optional for this task. Focus on the blacklist check.

- [ ] **Step 3: Modify AuthController.logout()**

Change `AuthController` to inject `JwtBlacklist`:
```java
private final UserService userService;
private final JwtBlacklist jwtBlacklist;
```

Change `logout()`:
```java
@GetMapping("/logout")
public Result<Void> logout(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
        String token = bearerToken.substring(7);
        jwtBlacklist.add(token);
    }
    return Result.success();
}
```

- [ ] **Step 4: Commit**

```bash
git add library-server/src/main/java/com/library/security/JwtBlacklist.java library-server/src/main/java/com/library/security/JwtAuthFilter.java library-server/src/main/java/com/library/controller/AuthController.java
git commit -m "fix: invalidate JWT token on logout using in-memory blacklist"
```

---

## Task 6: Backend — Add @NotNull validation to BorrowRequest

**Files:**
- Modify: `library-server/src/main/java/com/library/dto/BorrowRequest.java`
- Modify: `library-server/src/main/java/com/library/controller/BorrowController.java`

- [ ] **Step 1: Add validation to BorrowRequest**

Replace `BorrowRequest.java`:
```java
package com.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowRequest {
    @NotNull(message = "图书ID不能为空")
    private Long bookId;
}
```

- [ ] **Step 2: Add @Valid to BorrowController**

Update the `borrowBook` method signature:
```java
public Result<String> borrowBook(@Valid @RequestBody BorrowRequest request, HttpServletRequest httpRequest) {
```

- [ ] **Step 3: Commit**

```bash
git add library-server/src/main/java/com/library/dto/BorrowRequest.java library-server/src/main/java/com/library/controller/BorrowController.java
git commit -m "fix: add @NotNull validation to BorrowRequest.bookId"
```

---

## Task 7: Frontend — Fix borrow status filter and add error handling

**Files:**
- Modify: `library-server/src/main/java/com/library/dto/PageRequest.java`
- Modify: `library-server/src/main/java/com/library/service/impl/BorrowServiceImpl.java`
- Modify: `library-vue/src/views/borrows/index.vue`

**Root cause:** Frontend sends `keyword` as the status filter value but backend `PageRequest` uses `keyword` field (a String). The semantic mismatch means the type comparison `eq(BorrowRecord::getStatus, request.getKeyword())` is comparing Integer to String.

**Fix:** Add a dedicated `Integer status` field to `PageRequest`, update `BorrowServiceImpl` to use it, and update the frontend to send `status` instead of `keyword`. Also add `ElMessage.error()` to all catch blocks.

- [ ] **Step 1: Add status field to PageRequest**

Replace `PageRequest.java`:
```java
package com.library.dto;

import lombok.Data;

@Data
public class PageRequest {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Integer status;  // dedicated status filter field
}
```

- [ ] **Step 2: Update BorrowServiceImpl to use status field**

In `BorrowServiceImpl.getBorrowRecords()`, replace lines 39-41:
```java
if (StringUtils.hasText(request.getKeyword())) {
    wrapper.eq(BorrowRecord::getStatus, request.getKeyword());
}
```
With:
```java
if (request.getStatus() != null) {
    wrapper.eq(BorrowRecord::getStatus, request.getStatus());
} else if (StringUtils.hasText(request.getKeyword())) {
    // Backward compatibility fallback
    wrapper.eq(BorrowRecord::getStatus, request.getKeyword());
}
```

- [ ] **Step 3: Fix frontend borrows/index.vue — change keyword to status**

In `loadBorrows()` in `borrows/index.vue`, change:
```javascript
const params = {
  pageNum: pagination.pageNum,
  pageSize: pagination.pageSize,
  keyword: filterStatus.value?.toString()
}
```
To:
```javascript
const params = {
  pageNum: pagination.pageNum,
  pageSize: pagination.pageSize,
  status: filterStatus.value ?? undefined
}
```

- [ ] **Step 4: Add user-facing error messages in borrows/index.vue**

In `loadBorrows()` catch block, change:
```javascript
} catch (error) {
  console.error(error)
}
```
To:
```javascript
} catch (error) {
  console.error(error)
  ElMessage.error(error?.response?.data?.message || '加载借阅记录失败')
}
```

In `handleReturn()` catch block:
```javascript
} catch (error) {
  console.error(error)
}
```
To:
```javascript
} catch (error) {
  console.error(error)
  ElMessage.error(error?.response?.data?.message || '还书失败')
}
```

In `handleRenew()` catch block:
```javascript
} catch (error) {
  console.error(error)
}
```
To:
```javascript
} catch (error) {
  console.error(error)
  ElMessage.error(error?.response?.data?.message || '续借失败')
}
```

- [ ] **Step 5: Commit**

```bash
git add library-server/src/main/java/com/library/dto/PageRequest.java library-server/src/main/java/com/library/service/impl/BorrowServiceImpl.java library-vue/src/views/borrows/index.vue
git commit -m "fix: use dedicated status field for borrow filter and show user error messages"
```

---

## Task 8: Frontend — Add pagination to users list

**Files:**
- Modify: `library-vue/src/views/users/index.vue`

**Root cause:** `user.list()` returns paginated data (`records`, `total`) but the view has no `el-pagination` component, so only the first page is visible.

- [ ] **Step 1: Add pagination reactive state**

Add to the `<script setup>` section:
```javascript
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
```

- [ ] **Step 2: Update loadUsers to pass pagination params and handle response**

Replace `loadUsers()`:
```javascript
const loadUsers = async () => {
  loading.value = true
  try {
    const res = await user.list({ pageNum: pagination.pageNum, pageSize: pagination.pageSize })
    users.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
    ElMessage.error(error?.response?.data?.message || '加载用户列表失败')
  } finally {
    loading.value = false
  }
}
```

- [ ] **Step 3: Add el-pagination after the table**

In the template, after the `</el-table>` closing tag and before `</el-card>`:
```vue
<el-pagination
  v-model:current-page="pagination.pageNum"
  v-model:page-size="pagination.pageSize"
  :total="pagination.total"
  :page-sizes="[10, 20, 50]"
  layout="total, sizes, prev, pager, next"
  style="margin-top: var(--spacing-lg); justify-content: flex-end"
  @size-change="loadUsers"
  @current-change="loadUsers"
/>
```

- [ ] **Step 4: Commit**

```bash
git add library-vue/src/views/users/index.vue
git commit -m "fix: add pagination to users list view"
```

---

## Task 9: Frontend — Show login/register error messages

**Files:**
- Modify: `library-vue/src/views/login/index.vue`
- Modify: `library-vue/src/views/register/index.vue`

**Root cause:** Both pages only `console.error(error)` in catch blocks — no user-visible message.

- [ ] **Step 1: Fix login/index.vue catch block**

In `handleLogin()` catch block (line 99-100), change:
```javascript
} catch (error) {
  console.error(error)
}
```
To:
```javascript
} catch (error) {
  console.error(error)
  ElMessage.error(error?.response?.data?.message || '登录失败，请检查用户名和密码')
}
```

- [ ] **Step 2: Fix register/index.vue catch block**

In `handleRegister()` catch block (line 134-135), change:
```javascript
} catch (error) {
  console.error(error)
}
```
To:
```javascript
} catch (error) {
  console.error(error)
  ElMessage.error(error?.response?.data?.message || '注册失败，请稍后重试')
}
```

- [ ] **Step 3: Commit**

```bash
git add library-vue/src/views/login/index.vue library-vue/src/views/register/index.vue
git commit -m "fix: show user-facing error messages on login and register failure"
```

---

## Self-Review Checklist

- [ ] All 9 tasks have file paths, exact code changes, and commit commands
- [ ] No placeholder text (no "TBD", "TODO", "implement later", "similar to X")
- [ ] Task 1: OVERDUE status update added after saveRemindLog() in sendOverdueRemind()
- [ ] Task 1: returnBook() accepts both BORROWING and OVERDUE status
- [ ] Task 2: renewCount field added to entity, initialized to 0, incremented on renew, max 2 check
- [ ] Task 3: selectForUpdate added to BookMapper, used in borrowBook()
- [ ] Task 4: getNextPositionForUpdate added, called inside @Transactional createReservation()
- [ ] Task 5: JwtBlacklist uses ConcurrentHashMap.newKeySet(), checked before validateToken
- [ ] Task 6: @NotNull on BorrowRequest.bookId, @Valid on controller method
- [ ] Task 7: PageRequest has dedicated status Integer field, frontend sends status
- [ ] Task 8: pagination reactive added, passed to API, el-pagination in template
- [ ] Task 9: ElMessage.error added to login and register catch blocks

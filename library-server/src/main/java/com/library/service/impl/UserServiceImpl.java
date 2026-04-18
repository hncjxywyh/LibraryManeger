package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Constants;
import com.library.dto.LoginRequest;
import com.library.dto.PageRequest;
import com.library.dto.RegisterRequest;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.security.JwtUtil;
import com.library.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User register(RegisterRequest request) {
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
        user.setRole(Constants.ROLE_USER);
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

    @Override
    public User updateUser(Long id, User user, Long adminId) {
        User existing = userMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("用户不存在");
        }
        // 管理员不能禁用自己
        if (id.equals(adminId) && user.getStatus() != null && user.getStatus().equals(Constants.STATUS_DISABLED)) {
            throw new RuntimeException("不能禁用自己的账号");
        }
        if (user.getRealName() != null) {
            existing.setRealName(user.getRealName());
        }
        if (user.getPhone() != null) {
            existing.setPhone(user.getPhone());
        }
        if (user.getEmail() != null) {
            existing.setEmail(user.getEmail());
        }
        if (user.getStatus() != null) {
            existing.setStatus(user.getStatus());
        }
        userMapper.updateById(existing);
        return existing;
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("新密码不能与原密码相同");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    @Override
    public Page<User> getUsers(PageRequest request) {
        Page<User> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.like(User::getUsername, request.getKeyword());
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userMapper.selectPage(page, wrapper);
        // 密码不能返回给前端
        result.getRecords().forEach(u -> u.setPassword(null));
        return result;
    }
}
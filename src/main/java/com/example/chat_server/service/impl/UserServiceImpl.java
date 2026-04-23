package com.example.chat_server.service.impl;

import com.example.chat_server.entity.User;
import com.example.chat_server.mapper.UserMapper;
import com.example.chat_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String account, String password) {
        User user = userMapper.selectByAccount(account);
        //TODO：BCrypt加密验证
        if (user != null && user.getPassword().equals(password)){
            return user;
        }
        return null;
    }


    @Override
    public User register(User user) {
        user.setId(UUID.randomUUID().toString().replace("-", ""));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        // TODO：BCrypt加密存储密码
        userMapper.insert(user);
        return user;
    }

    @Override
    public User getUserById(String id) {
        return userMapper.selectById(id);
    }

    @Override
    public User updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
        return user;
    }

    @Override
    public void deleteUser(String id) {
        userMapper.deleteById(id);
    }
}


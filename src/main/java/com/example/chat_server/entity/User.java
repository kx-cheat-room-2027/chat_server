package com.example.chat_server.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private String id;
    private String account;
    private String name;
    private String portrait;
    private String password;
    private String sex;
    private String email;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

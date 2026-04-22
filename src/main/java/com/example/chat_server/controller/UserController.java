package com.example.chat_server.controller;

import com.example.chat_server.entity.User;
import com.example.chat_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;



@RestController
@RequestMapping("/api/user")


public class UserController {
    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.access-path}")
    private String accessPath;
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String account = params.get("account");
        String password = params.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(account, password)
            );

            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", authentication.getPrincipal());
        } catch (AuthenticationException e) {
            result.put("code", 401);
            result.put("message", "账号或密码错误");
        }
        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        User registeredUser = userService.register(user);
        result.put("code", 200);
        result.put("message", "注册成功");
        result.put("data", registeredUser);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.getUserById(id);
        if (user != null) {
            result.put("code", 200);
            result.put("data", user);
        } else {
            result.put("code", 404);
            result.put("message", "用户不存在");
        }
        return result;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }


    @PostMapping("/uploadAvatar")
    public Map<String, Object> uploadAvatar(@RequestParam("file") MultipartFile file,
                                            @RequestParam("userId") String userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 检查文件是否为空
            if (file.isEmpty()) {
                result.put("code", 400);
                result.put("message", "文件不能为空");
                return result;
            }

            // 2. 检查用户是否存在
            User user = userService.getUserById(userId);
            if (user == null) {
                result.put("code", 404);
                result.put("message", "用户不存在");
                return result;
            }

            // 3. 生成唯一文件名（防止重名覆盖）
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                result.put("code", 400);
                result.put("message", "文件名无效");
                return result;
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 4. 确保保存目录存在
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    result.put("code", 500);
                    result.put("message", "创建上传目录失败");
                    return result;
                }
            }

            // 5. 将文件保存到磁盘
            File destFile = new File(uploadPath + newFilename);
            file.transferTo(destFile);

            // 6. 更新数据库中的头像路径
            String avatarUrl = accessPath + newFilename; // 例如：/avatars/xxxx.jpg
            user.setPortrait(avatarUrl);
            userService.updateUser(user); // 这里调用 Service 层更新数据库

            // 7. 返回结果
            result.put("code", 200);
            result.put("message", "上传成功");
            result.put("data", avatarUrl);

        } catch (IOException e) {
            result.put("code", 500);
            result.put("message", "上传失败：" + e.getMessage());
        }

        return result;
    }
}

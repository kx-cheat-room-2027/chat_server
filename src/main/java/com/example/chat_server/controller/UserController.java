package com.example.chat_server.controller;

import com.example.chat_server.annotation.UrlFree;
import com.example.chat_server.utils.ResultUtil;
import com.example.chat_server.entity.User;
import com.example.chat_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.hutool.json.JSONObject;

import java.util.HashMap;
import com.example.chat_server.utils.JwtUtil;
import java.util.Map;



@RestController
@RequestMapping("/api/user")


public class UserController {
    @Autowired
    private UserService userService;

    @UrlFree
    @PostMapping("/login")
    public JSONObject login(@RequestBody Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");

        //TODO：实现登录逻辑
        User user = userService.login(account,password);
        if(user != null){
            //TODO:生成JWT Token
            //创建存储用户信息的Map(除密码）
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("account", user.getAccount());
            claims.put("name", user.getName());
            claims.put("email", user.getEmail());
            //生成Token
            String token = JwtUtil.createToken(claims);
            //把token和用户的信息一起返回
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("token", token);
            resultData.put("user", user);
            return ResultUtil.Succeed("登录成功",resultData);
        }else{
            return ResultUtil.Fail("账号或密码错误");
        }
    }

    @UrlFree
    @PostMapping("/register")
    public JSONObject register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        return ResultUtil.Succeed("注册成功",registeredUser);
    }

    @UrlFree
    @GetMapping("/{id}")
    public JSONObject getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResultUtil.Succeed(user);
        } else {
            return ResultUtil.Fail("用户不存在");
        }
    }

    @DeleteMapping("/{id}")
    public JSONObject deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResultUtil.Succeed();
    }
}

package com.example.chat_server.filter;

import com.example.chat_server.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
            return;
        }

        //todo 部分接口应该直接放行 未配置
        String tokenName = "token";
        String token = httpServletRequest.getHeader(tokenName);
        Claims claims = null;
        try {
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            //todo 返回错误
            return;
        }
        // 设置用户信息
        Map<String, Object> map = new HashMap<>();
        claims.entrySet().stream().forEach(e -> map.put(e.getKey(), e.getValue()));
        //验证角色是否有权限
        httpServletRequest.setAttribute("userinfo", map);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}

package com.example.chat_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileConfig implements WebMvcConfigurer {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.access-path}")
    private String accessPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射：访问 /avatars/xxx.jpg → 实际读取 D:/chat-room-avatars/xxx.jpg
        registry.addResourceHandler(accessPath + "**")
                .addResourceLocations("file:D:/chat-room-avatars/");
    }
}

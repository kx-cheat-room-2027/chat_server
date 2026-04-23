package com.example.chat_server.controller;

import cn.hutool.json.JSONObject;
import com.example.chat_server.annotation.Userid;
import com.example.chat_server.entity.Message;
import com.example.chat_server.service.MessageService;
import com.example.chat_server.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 发送消息（第一步）
     *
     * @param fromId 发送方ID
     * @param toId 接收方ID
     * @param type 消息类型（text/image/file）
     * @param content 消息内容
     * @return messageId
     */
    @PostMapping("/send")
    public JSONObject sendMessage(
            @Userid String fromId,
            @RequestParam String toId,
            @RequestParam String type,
            @RequestParam(required = false, defaultValue = "") String content) {

        String messageId = messageService.sendMessage(fromId, toId, type, content);

        JSONObject result = ResultUtil.Succeed();
        result.set("data", messageId);
        return result;
    }

    /**
     * 上传文件到消息（第二步）
     * 前端上传文件时，携带messageId + 文件流
     * @param messageId 消息ID
     * @param file 文件
     * @return 文件URL
     */
    @PostMapping("/upload-file/{messageId}")
    public JSONObject uploadFile(
            @PathVariable String messageId,
            @RequestParam("file") MultipartFile file) throws Exception {

            String fileUrl = messageService.uploadFileByMessageId(messageId, file);

            return ResultUtil.Succeed(fileUrl);

    }

    /**
     * 获取消息详情
     *
     * @param messageId 消息ID
     * @return 消息对象
     */
    @GetMapping("/{messageId}")
    public JSONObject getMessage(@PathVariable String messageId) {
        Message message = messageService.getMessageById(messageId);

        JSONObject result = ResultUtil.Succeed();
        result.set("data", message);
        return result;
    }
}

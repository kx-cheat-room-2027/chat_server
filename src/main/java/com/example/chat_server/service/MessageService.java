package com.example.chat_server.service;

import com.example.chat_server.entity.Message;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

    /**
     * 发送消息（第一步）
     *
     * @param fromId 发送方ID
     * @param toId 接收方ID
     * @param type 消息类型（text/image/file）
     * @param content 消息内容
     * @return 消息ID
     */
    String sendMessage(String fromId, String toId, String type, String content);

    /**
     * 根据消息ID上传文件到MinIO（第二步）
     *
     * @param messageId 消息ID
     * @param file 文件
     * @return 文件访问URL
     */
    String uploadByMessageId(String messageId, MultipartFile file , String type) throws Exception;
    String uploadFileByMessageId(String messageId, MultipartFile file ) throws Exception;
    String uploadImageByMessageId(String messageId, MultipartFile file ) throws Exception;

    /**
     * 获取消息详情
     *
     * @param messageId 消息ID
     * @return 消息对象
     */
    Message getMessageById(String messageId);
}

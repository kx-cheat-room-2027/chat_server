package com.example.chat_server.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.example.chat_server.config.MinioConfig;
import com.example.chat_server.entity.Message;
import com.example.chat_server.entity.MsgContent;
import com.example.chat_server.exception.BaseException;
import com.example.chat_server.mapper.MessageMapper;
import com.example.chat_server.service.MessageService;
import com.example.chat_server.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MinioService minioService;

    @Autowired
    private MinioConfig minioConfig;

    @Override
    @Transactional
    public String sendMessage(String fromId, String toId, String type, String content) {
        //生成消息ID
        String messageId = IdUtil.simpleUUID();
        //封装消息内容
        //TODO 发送方头像,昵称根据formId查询填充到msgContent中
        MsgContent msgContent = MsgContent.builder()
                .fromUserId(fromId)//发送方ID
                .type(type)//消息类型
                .content(content)//消息内容,如果是文件消息,则content为文件名
                .build();

        Message message = new Message();
        message.setId(messageId);
        message.setFromId(fromId);
        message.setToId(toId);
        message.setMsgContent(msgContent);
        message.setIsShowTime(true);
        //TODO 时间填充

        messageMapper.insert(message);

        return messageId;
    }

    @Override
    @Transactional
    //上传通用
    public String uploadByMessageId(String messageId, MultipartFile file , String type) throws Exception {
        //根据messageId查询,定位文件/图像所在消息
        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BaseException("消息不存在");
        }
        //默认前端传过来的文件名称没有后缀
        MsgContent msgContent = message.getMsgContent();//msg -> msgcontent -> content(文件名)
        String Filename = msgContent.getContent();//文件/图像名

        // 2. 生成唯一文件/图像名：Filename + UUID + 原始扩展名,密码规则大概率要改🫠//TODO 要不要更新数据库里面的content(文件名)
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = Filename + UUID.randomUUID().toString() + fileExtension;


        String bucketName = "";//桶,参数
        String MinioName = "";//minio内部文件名,返回不一样的

        // 3. 根据文件类型分流桶
        // TODO 待优化

        if (Objects.equals(type, "file")) {
            bucketName = minioConfig.getFileBucket();
            MinioName = minioService.uploadFile(file, objectName);//存文件
        } else if (Objects.equals(type, "image")) {
            bucketName = minioConfig.getImageBucket();
            MinioName = minioService.uploadImage(file, objectName);
        }


        String fileUrl = minioService.getFileUrl(MinioName, bucketName, 60 * 24 * 7);//获取文件URL,有效期7天

        return fileUrl;
    }

    //上传文件
    @Override
    public String uploadFileByMessageId(String messageId, MultipartFile file) throws Exception {
        return uploadByMessageId(messageId, file, "file");
    }

    //上传图像
    @Override
    public String uploadImageByMessageId(String messageId, MultipartFile file) throws Exception {
        return uploadByMessageId(messageId, file, "image");
    }

    //获取消息
    @Override
    public Message getMessageById(String messageId) {
        return messageMapper.selectById(messageId);
    }

}

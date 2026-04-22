CREATE DATABASE IF NOT EXISTS chat_room DEFAULT CHARSET utf8mb4;
USE chat_room;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`          varchar(64)   NOT NULL,
    `account`     varchar(64)   NOT NULL COMMENT '用户账号',
    `name`        varchar(200)  NOT NULL COMMENT '用户名',
    `portrait`    text          default NULL COMMENT '头像',
    `password`    varchar(200)  NOT NULL COMMENT '密码',
    `sex`         varchar(64)   default NULL COMMENT '性别',
    `email`       varchar(200)  default NULL COMMENT '邮箱',
    `create_time` timestamp(3)  NOT NULL COMMENT '创建时间',
    `update_time` timestamp(3)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表' row_format=dynamic;

DELETE FROM user;

package com.example.chat_server.mapper;

import com.example.chat_server.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE account = #{account}")
    User selectByAccount(@Param("account") String account);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(@Param("id") String id);

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(String id);


    int insert(User user);

    int update(User user);

}

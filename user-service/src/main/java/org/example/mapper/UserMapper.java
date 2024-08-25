package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.User;
import org.example.entity.UserInfoDTO;

import java.util.List;

@Mapper
public interface UserMapper {
    User queryOne(@Param("username") String username, @Param("password") String password);
    void insertUser(User user);
    User queryOneById(long userId);

    List<UserInfoDTO> queryByIds(List<Long> ids);
}

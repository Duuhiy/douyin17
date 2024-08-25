package org.example.service;

import org.example.entity.UserInfoDTO;
import org.example.vo.LoginResp;
import org.example.vo.LoginVo;
import org.example.vo.RegisterVO;

import java.util.List;

public interface UserService {
    LoginResp login(LoginVo loginVo);

    Long register(RegisterVO registerVO);

    UserInfoDTO queryUserInfo(long userId);

    List<UserInfoDTO> queryUserInfoList(List<Long> ids);
}

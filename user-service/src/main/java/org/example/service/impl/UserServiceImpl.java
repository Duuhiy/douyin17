package org.example.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.example.entity.User;
import org.example.entity.UserInfoDTO;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.example.utils.JwtUtil;
import org.example.utils.UserHolder;
import org.example.vo.LoginResp;
import org.example.vo.LoginVo;
import org.example.vo.RegisterVO;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Override
    public LoginResp login(LoginVo loginVo) {
        String password = DigestUtils.md5Hex(loginVo.getPassword());
        User user = userMapper.queryOne(loginVo.getUsername(), password);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String token = JwtUtil.createToken(user.getId(), user.getName());
        return new LoginResp(user.getId(), token);
    }

    @Override
    public Long register(RegisterVO registerVO) {
        String password = registerVO.getPassword();
        password = DigestUtils.md5Hex(password);
        User user = new User();
        user.setName(registerVO.getUsername());
        user.setPassword(password);
        userMapper.insertUser(user);
        return user.getId();
    }

    @Override
    public UserInfoDTO queryUserInfo(long userId) {
        long myId = UserHolder.getUser().getId();
        User objectUser = userMapper.queryOneById(userId);
        UserInfoDTO userInfoDTO = new UserInfoDTO(objectUser.getId(), objectUser.getName(), objectUser.getFollowCount(), objectUser.getFollowerCount(), objectUser.getWorkCount(), objectUser.getFavoriteCount(), objectUser.getTotalFavorited(), objectUser.getAvatar(), objectUser.getBackgroundImage(), objectUser.getSignature(), false);
        if (myId != objectUser.getId()) {
            // Todo: 查是否关注
        }
        return userInfoDTO;
    }

    @Override
    public List<UserInfoDTO> queryUserInfoList(List<Long> ids) {
        log.info("queryUserInfoList {}", ids);
        List<UserInfoDTO> userInfoList = userMapper.queryByIds(ids);
        return userInfoList;
    }
}

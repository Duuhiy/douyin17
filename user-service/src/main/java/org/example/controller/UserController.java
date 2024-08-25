package org.example.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserInfoDTO;
import org.example.resp.ResultData;
import org.example.service.UserService;
import org.example.vo.LoginResp;
import org.example.vo.LoginVo;
import org.example.vo.RegisterVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/douyin")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping(value = "/user/register")
    public ResultData<Long> register(@RequestBody RegisterVO registerVO) {
        log.info("register {}", registerVO.getUsername());
        return ResultData.success(userService.register(registerVO));
    }

    @PostMapping(value = "/user/login")
    public ResultData<LoginResp> login(@RequestBody LoginVo loginVo) {
        return ResultData.success(userService.login(loginVo));
    }

    @GetMapping(value = "/user")
    public ResultData<UserInfoDTO> userInfo(@RequestParam("user_id") long userId, @RequestParam("token") String token) {
        return ResultData.success(userService.queryUserInfo(userId));
    }

    @GetMapping(value = "/user/list")
    public List<UserInfoDTO> userInfoList(@RequestParam("user_id") List<Long> ids) {
        if (ids.isEmpty())
            return null;
        return userService.queryUserInfoList(ids);
    }
}

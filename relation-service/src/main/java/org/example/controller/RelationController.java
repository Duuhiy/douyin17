package org.example.controller;

import jakarta.annotation.Resource;
import org.example.entity.UserDTO;
import org.example.entity.UserInfoDTO;
import org.example.resp.ResultData;
import org.example.service.RelationService;
import org.example.vo.Action;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/douyin/relation")
public class RelationController {
    @Resource
    private RelationService relationService;
    @PostMapping("/action")
    public ResultData<String> relationAction(@RequestBody Action action){
        relationService.relationAction(action);
        return ResultData.success("操作成功");
    }

    @GetMapping("/follow/list")
    public ResultData<List<UserInfoDTO>> followList(@RequestParam("user_id") long userId, @RequestParam("token") String token) {
        return ResultData.success(relationService.followList(userId));
    }

    @GetMapping("/follower/list")
    public ResultData<List<UserInfoDTO>> followerList(@RequestParam("user_id") long userId, @RequestParam("token") String token) {
        return ResultData.success(relationService.followerList(userId));
    }

    @GetMapping("/friend/list")
    public ResultData<List<UserInfoDTO>> friendList(@RequestParam("user_id") long userId, @RequestParam("token") String token) {
        return ResultData.success(relationService.friendList(userId));
    }
}

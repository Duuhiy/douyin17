package org.example.controller;

import jakarta.annotation.Resource;
import org.example.entity.FavoriteActionRequest;
import org.example.entity.PublishActionRequest;
import org.example.entity.Video;
import org.example.service.FavorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class VideoController {
    @Resource
    private FavorService favorService;

    @PostMapping("/publish/action")
    public String publish(PublishActionRequest req) throws IOException {

        return req.getTitle() + " " + req.getVideo().getContentType();
    }

    @PostMapping("/favorite/action")
    public String favor(FavoriteActionRequest req) throws ExecutionException, InterruptedException {
        favorService.action(req);
        return "操作成功";
    }

    // 点赞
    // select video_id from favor f join video v on f.video_id = v.id where f.user_id = ? and f.ctime > ? order by ctime limit 10;
    // 喜欢列表
    @GetMapping("/favorite/list")
    public List<Video> favoriteList(@RequestParam("user_id") long userId, @RequestParam("token") String token) {
        return favorService.list(userId);
    }
}

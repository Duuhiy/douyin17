package org.example.controller;

import jakarta.annotation.Resource;
import org.example.entity.Action;
import org.example.entity.Message;
import org.example.service.ImService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/douyin/message")
public class ImController {
    @Resource
    private ImService imService;
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            4,
            8,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(64),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @GetMapping("/new")
    public DeferredResult<List<Message>> queryNewMessage(@RequestParam("user") long user, @RequestParam("time") long time) {
        DeferredResult<List<Message>> deferredResult = new DeferredResult<>(1000L);
        deferredResult.onTimeout(() -> {
            deferredResult.setResult(Collections.emptyList());
        });
        deferredResult.setResult(imService.getNewMessage(user, time));
        return deferredResult;
    }

    @GetMapping("/chat")
    public List<Message> getNewMessage(@RequestParam("token") String token, @RequestParam("to_user_id") long fromUserId, @RequestParam("pre_msg_time") long preMsgTime) {
        return imService.getNewMessage(fromUserId, preMsgTime);
    }

    @PostMapping("/action")
    public String sendMessage(@RequestBody Action action) {
        return imService.sendMessage(action);
    }
}

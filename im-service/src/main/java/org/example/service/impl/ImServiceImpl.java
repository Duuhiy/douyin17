package org.example.service.impl;

import jakarta.annotation.Resource;
import org.example.entity.Action;
import org.example.entity.Message;
import org.example.mapper.ImMapper;
import org.example.service.ImService;
import org.example.utils.UserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImServiceImpl implements ImService {
    @Resource
    private ImMapper imMapper;
    @Resource
    private ImService imService;

    @Override
    public List<Message> getNewMessage(long fromUserId, long preMsgTime) {
        // Todo: 如果preMsgTime = 0，说明是在当前设备第一次登录，使用消息记录的逻辑，返回最新20条消息记录
        // Todo: 上划 -> 查历史
        long userId = UserHolder.getUser().getId();
        return imMapper.queryNewMessage(userId, fromUserId, preMsgTime);
    }

    @Override
    @Transactional
    public String sendMessage(Action action) {
        long userId = UserHolder.getUser().getId(), toUserId = action.getToUserId();
        String room = userId < toUserId ? userId + ":" + toUserId : toUserId + ":" + userId;
        // 存储到message表
        Message message = new Message();
        message.setFromUser(userId);
        message.setToUser(toUserId);
        message.setRoom(room);
        message.setContent(action.getContent());
        imMapper.insertMessage(message);
        // 对方的timeline加一条记录
        imMapper.insertTimeline(toUserId, message.getId());
        return null;
    }

    public List<Message> getMessageHistory(String room, long preMsgTime) {
        // Todo
        return null;
    }
}

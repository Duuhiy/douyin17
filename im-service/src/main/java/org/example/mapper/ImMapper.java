package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.entity.Message;

import java.util.Date;
import java.util.List;

@Mapper
public interface ImMapper {
    List<Message> queryNewMessage(long toUser, long fromUser, long time);
    List<Message> queryMessageRecord(long room, Date time);

    void insertMessage(Message message);

    void insertTimeline(long toUserId, long msgId);
}

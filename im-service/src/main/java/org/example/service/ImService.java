package org.example.service;

import org.example.entity.Action;
import org.example.entity.Message;

import java.util.List;

public interface ImService {
    List<Message> getNewMessage(long rcvUserId, long preMsgTime);

    String sendMessage(Action action);
}

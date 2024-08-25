package org.example.service;

import org.example.entity.UserInfoDTO;
import org.example.vo.Action;

import java.util.List;

public interface RelationService {
    void relationAction(Action action);

    List<UserInfoDTO> followList(long userId);

    List<UserInfoDTO> followerList(long userId);

    List<UserInfoDTO> friendList(long userId);
}

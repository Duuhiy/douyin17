package org.example.service.impl;

import jakarta.annotation.Resource;
import org.example.apis.UserFeignApi;
import org.example.entity.Relation;
import org.example.entity.UserInfoDTO;
import org.example.mapper.RelationMapper;
import org.example.service.RelationService;
import org.example.utils.UserHolder;
import org.example.vo.Action;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelationServiceImpl implements RelationService {
    @Resource
    private RelationMapper relationMapper;
    @Resource
    private UserFeignApi userFeignApi;
    @Override
    public void relationAction(Action action) {
        long userId = UserHolder.getUser().getId();
        if (action.getActionType() == 1) {
            follow(userId, action.getToUserId());
        } else if (action.getActionType() == 2) {
            unFollow(userId, action.getToUserId());
        } else {
            throw new RuntimeException("操作码错误");
        }
    }

    private void follow(long userId, long toUserId) {
        // follow from = me to = other status |= 1
        // update from = other to = me status |= 2
        relationMapper.follow(toUserId, userId);
        relationMapper.syncFollow(userId, toUserId);
    }

    private void unFollow(long userId, long toUserId) {
        relationMapper.unFollow(toUserId, userId);
        relationMapper.syncUnFollow(userId, toUserId);
    }

    @Override
    public List<UserInfoDTO> followList(long userId) {
        List<Relation> followList = relationMapper.queryFollowList(userId);
        if (followList.isEmpty()) {
            return null;
        }
        List<Long> idList = followList.stream().map(Relation::getToUserId).collect(Collectors.toList());
        List<UserInfoDTO> userInfoList = userFeignApi.userInfoList(idList);
        userInfoList.forEach(u -> u.setFollow(true));
        return userInfoList;
    }

    @Override
    public List<UserInfoDTO> followerList(long userId) {
        List<Relation> followerList = relationMapper.queryFollowerList(userId);
        if (followerList.isEmpty()) {
            return null;
        }
        List<Long> idList = followerList.stream().map(Relation::getFromUserId).collect(Collectors.toList());
        List<UserInfoDTO> userInfoList = userFeignApi.userInfoList(idList);
        for (int i = 0; i < followerList.size(); i++) {
            if (followerList.get(i).getStatus() == 2) {
                userInfoList.get(i).setFollow(true);
            }
        }
        return userInfoList;
    }

    @Override
    public List<UserInfoDTO> friendList(long userId) {
        List<Relation> friendList = relationMapper.queryFriendList(userId);
        if (friendList.isEmpty()) {
            return null;
        }
        List<Long> idList = friendList.stream().map(Relation::getToUserId).collect(Collectors.toList());
        List<UserInfoDTO> userInfoList = userFeignApi.userInfoList(idList);
        userInfoList.forEach(u -> u.setFollow(true));
        return userInfoList;
    }
}

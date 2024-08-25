package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.entity.Relation;
import org.example.entity.UserInfoDTO;

import java.util.List;

@Mapper
public interface RelationMapper {
    void follow(long toUserId, long fromUserId);
    void syncFollow(long toUserId, long fromUserId);

    void unFollow(long toUserId, long fromUserId);
    void syncUnFollow(long toUserId, long fromUserId);

    List<Relation> queryFollowList(long userId);

    List<Relation> queryFollowerList(long userId);

    List<Relation> queryFriendList(long userId);
}

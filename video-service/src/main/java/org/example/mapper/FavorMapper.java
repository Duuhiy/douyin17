package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.entity.Favorite;

@Mapper
public interface FavorMapper {
    void insert(long id, long videoId);
}

package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VideoMapper {
    void updateFavoriteCount(@Param("videoId") long videoId, @Param("change") int change);
}

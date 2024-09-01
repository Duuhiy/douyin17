package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> queryCommentsByRoot(@Param("videoId") long videoId, @Param("root") int root);

    void insertComment(Comment comment);
}

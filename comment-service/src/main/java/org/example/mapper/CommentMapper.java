package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.entity.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> queryIndex(int videoId, int root);
    List<Comment> queryComments(List<Long> ids);
    void insertCommentIndex(Comment comment);
    void insertCommentContent(Comment comment);
    List<Comment> queryCommentsByRoot(long videoId, int root);
}

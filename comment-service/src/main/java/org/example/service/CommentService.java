package org.example.service;

import org.example.entity.Action;
import org.example.entity.Comment;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CommentService {
    List<Comment> queryComments(int videoId, int root, int page, int size);
    List<Comment> queryCommentsFromDB(int videoId, int root, int page, int size);
    String commentAction(Action action);
    void addComment(Comment comment);
}

package org.example.service;

import org.example.entity.Action;
import org.example.entity.Comment;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CommentService {
    List<Comment> queryFirstComments(int videoId, int page, int size);
    List<Comment> queryInFloorComments(int videoId, int root, int page, int size);
    List<Comment> queryFirstCommentFromDB(int videoId, int page, int size);
    List<Comment> queryInFloorCommentsFromDB(int videoId, int root, int page, int size);
    String commentAction(Action action) throws ExecutionException, InterruptedException;
    void addComment(Comment comment);
}

package org.example.controller;

import jakarta.annotation.Resource;
import org.example.entity.Action;
import org.example.service.CommentService;
import org.springframework.web.bind.annotation.*;

import org.example.entity.Comment;


import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private CommentService commentService;

    @GetMapping(value = "/list/first")
    public List<Comment> queryFirstComments(int videoId, int page, int size) {
        return commentService.queryComments(videoId, 0, page, size);
    }

    @GetMapping(value = "/list/infloor")
    public List<Comment> queryCommentsByRoot(int videoId, int root, int page, int size) {
        return commentService.queryComments(videoId, root, page, size);
    }

    @PostMapping(value = "/action")
    public String commentAction(@RequestBody Action action) {
        return commentService.commentAction(action);
    }
}

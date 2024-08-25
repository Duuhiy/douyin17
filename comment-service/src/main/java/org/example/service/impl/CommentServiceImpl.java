package org.example.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.entity.Action;
import org.example.entity.Comment;
import org.example.mapper.CommentMapper;
import org.example.service.CommentService;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.example.constant.Constant.COMMENT_CONTENT_KEY;
import static org.example.constant.Constant.COMMENT_INDEX_KEY;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    CommentService commentService;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;
    private static final String COMMENT_TOPIC = "commentTopic";

    public List<Comment> queryFirstCommentFromDB(int videoId, int page, int size) {
        List<Comment> comments = commentMapper.queryCommentsByRoot(videoId, 0);
        return comments;
    }

    @Override
    public List<Comment> queryInFloorCommentsFromDB(int videoId, int root, int page, int size) {
        List<Comment> comments = commentMapper.queryCommentsByRoot(videoId, root);
        return comments;
    }

    public List<Comment> queryFirstComments(int videoId, int page, int size) {
        List<Comment> comments = new ArrayList<>();
        // redis zset 查 评论id
        Set<String> ids = stringRedisTemplate.opsForZSet().reverseRange(COMMENT_INDEX_KEY + videoId + ":0", page * size, (page + 1) * size);
        if (ids != null && !ids.isEmpty()) {
            // redis hash 查评论
            queryCommentFromRedis(comments, ids);
        } else {
            // 拿锁
            // double check
            ids = stringRedisTemplate.opsForZSet().reverseRange(COMMENT_INDEX_KEY + videoId + ":0", page * size, (page + 1) * size);
            if (ids != null && !ids.isEmpty()) {
                // redis hash 查评论
                queryCommentFromRedis(comments, ids);
            } else {
                List<Comment> commentIndexList = commentMapper.queryIndex(videoId, 0);
                List<Long> idList = new ArrayList<>(commentIndexList.size());
                Set<ZSetOperations.TypedTuple<String>> idTuple = new HashSet<>();
                for (Comment comment : commentIndexList) {
                    idList.add(comment.getId());
                    idTuple.add(new DefaultTypedTuple<>(String.valueOf(comment.getId()), (double) comment.getCtime().getTime()));
                }
                comments = commentMapper.queryComments(idList);
                // 写回redis
                // Todo: redis里只存部分评论，后面的评论不放大redis
                stringRedisTemplate.opsForZSet().add(COMMENT_INDEX_KEY + videoId + ":0", idTuple);
                for (Comment comment : comments) {
                    stringRedisTemplate.opsForValue().set(COMMENT_CONTENT_KEY + comment.getId(), JSONUtil.toJsonStr(comment));
                }
            }
        }
        return comments;
    }

    private void queryCommentFromRedis(List<Comment> comments, Set<String> ids) {
        for (String id : ids) {
            String commentJson = stringRedisTemplate.opsForValue().get(COMMENT_CONTENT_KEY + id);
            if (StrUtil.isNotBlank(commentJson)) {
                Comment comment = JSONUtil.toBean(commentJson, Comment.class);
                comments.add(comment);
            }
        }
    }

    public List<Comment> queryInFloorComments(int videoId, int root, int page, int size) {
        List<Comment> comments = new ArrayList<>();
        // redis zset 查 评论id
        Set<String> ids = stringRedisTemplate.opsForZSet().reverseRange(COMMENT_INDEX_KEY + videoId + ":" + root, page * size, (page + 1) * size);
        if (ids != null && !ids.isEmpty()) {
            // redis hash 查评论
            queryCommentFromRedis(comments, ids);
        } else {
            // 拿锁
            // double check
            ids = stringRedisTemplate.opsForZSet().reverseRange(COMMENT_INDEX_KEY + videoId + ":" + root, page * size, (page + 1) * size);
            if (ids != null && !ids.isEmpty()) {
                // redis hash 查评论
                queryCommentFromRedis(comments, ids);
            } else {
                List<Comment> commentIndexList = commentMapper.queryIndex(videoId, root);
                List<Long> idList = new ArrayList<>(commentIndexList.size());
                Set<ZSetOperations.TypedTuple<String>> idTuple = new HashSet<>();
                for (Comment comment : commentIndexList) {
                    idList.add(comment.getId());
                    idTuple.add(new DefaultTypedTuple<>(String.valueOf(comment.getId()), (double) comment.getCtime().getTime()));
                }
                comments = commentMapper.queryComments(idList);
                // 写回redis
                // Todo: redis里只存部分评论，后面的评论不放大redis
                stringRedisTemplate.opsForZSet().add(COMMENT_INDEX_KEY + videoId + ":" + root, idTuple);
                for (Comment comment : comments) {
                    stringRedisTemplate.opsForValue().set(COMMENT_CONTENT_KEY + comment.getId(), JSONUtil.toJsonStr(comment));
                }
            }
        }
        return comments;
    }

    @Override
    public String commentAction(Action action) throws ExecutionException, InterruptedException {
        if (action.getActionType() == 1 || action.getActionType() == 2) {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(COMMENT_TOPIC, action);
            future.whenComplete((v, e) -> {
                if (e != null) {
                    log.error("commentAction action{} 发送失败{}", action, e.getMessage());
                } else {
                    log.info("commentAction action{} 发送成功", action);
                }
            });
        } else {
            throw new RuntimeException("评论操作错误");
        }
        return "操作成功";
    }

    @KafkaListener(topics = COMMENT_TOPIC, groupId = "comment-C")
    public void commentConsume(ConsumerRecord<String, String> record) {
        Action action = JSONUtil.toBean(record.value(), Action.class);
        Comment comment = new Comment();
        comment.setVideoId(action.getVideoId());
        // Todo: 加拦截器，从token解析用户信息存到ThreadLocal中
        comment.setUserId(1);
        comment.setRoot(action.getRoot());
        comment.setParent(action.getParent());
        comment.setContent(action.getCommentText());
        if (action.getActionType() == 1) {
            commentService.addComment(comment);
        } else {
            // 删除
        }
    }

    @Transactional
    public void addComment(Comment comment) {
        commentMapper.insertCommentContent(comment);
        commentMapper.insertCommentIndex(comment);
        // 先写数据库，再删缓存
//        stringRedisTemplate.delete(COMMENT_INDEX_KEY + comment.getVideoId() + ":" + comment.getRoot());
    }
}

package org.example.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
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

import java.util.*;
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

    @Override
    public List<Comment> queryCommentsFromDB(int videoId, int root, int page, int size) {
        PageHelper.startPage(page, size);
        List<Comment> comments = commentMapper.queryCommentsByRoot(videoId, root);
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);
        return pageInfo.getList();
    }

    private List<Comment> queryCommentFromRedis(long videoId, int root, long start, long end) {
        Set<String> ids = stringRedisTemplate.opsForZSet().reverseRange(COMMENT_INDEX_KEY + videoId + ":" + root, start, end);
        if (ids == null || ids.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<Comment> commentList = new ArrayList<>(ids.size());
        for (String id : ids) {
            String commentJson = stringRedisTemplate.opsForValue().get(COMMENT_CONTENT_KEY + id);
            if (StrUtil.isNotBlank(commentJson)) {
                Comment comment = JSONUtil.toBean(commentJson, Comment.class);
                commentList.add(comment);
            }
        }
        return commentList;
    }

    public List<Comment> queryComments(int videoId, int root, int page, int size) {
        // redis zset 查 评论
        List<Comment> comments = queryCommentFromRedis(videoId, root, page * size, (page + 1) * size);
        if (comments.equals(Collections.EMPTY_LIST)) {
            // 拿锁
            // double check
            comments = queryCommentFromRedis(videoId, root, page * size, (page + 1) * size);
            if (comments.equals(Collections.EMPTY_LIST)) {
                // 从db查
                comments = queryCommentsFromDB(videoId, root, page, size);
                if (comments == null || comments.isEmpty()) {
                    return null;
                }
                Set<ZSetOperations.TypedTuple<String>> idTuple = new HashSet<>();
                for (Comment comment : comments) {
                    idTuple.add(new DefaultTypedTuple<>(String.valueOf(comment.getId()), (double) comment.getCtime().getTime()));
                }
                // 写回redis
                // Todo: 1.redis里只存部分评论，后面的评论不放大redis
                // Todo: 2.怎么保证一定成功
                stringRedisTemplate.opsForZSet().add(COMMENT_INDEX_KEY + videoId + ":0", idTuple);
                for (Comment comment : comments) {
                    stringRedisTemplate.opsForValue().set(COMMENT_CONTENT_KEY + comment.getId(), JSONUtil.toJsonStr(comment));
                }
            }
        }
        return comments;
    }

    @Override
    public String commentAction(Action action) {
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
    public void commentConsume(ConsumerRecord<String, String> record, Consumer consumer) {
        Action action = JSONUtil.toBean(record.value(), Action.class);
        log.info("commentConsume " + action.toString());
        Comment comment = new Comment();
        comment.setVideoId(action.getVideoId());
        // Todo: 加拦截器，从token解析用户信息存到ThreadLocal中
        comment.setUserId(1);
        comment.setRoot(action.getRoot());
        comment.setParent(action.getParent());
        comment.setContent(action.getCommentText());
        if (action.getActionType() == 1) {
            commentService.addComment(comment);
            // 删缓存
            Set<String> ids = stringRedisTemplate.opsForZSet().reverseRange(COMMENT_INDEX_KEY + action.getVideoId() + ":" + action.getRoot(), 0, Long.MAX_VALUE);
            if (ids != null && !ids.isEmpty()) {
                for (String id : ids) {
                    stringRedisTemplate.delete(COMMENT_CONTENT_KEY + id);
                }
            }
            stringRedisTemplate.delete(COMMENT_INDEX_KEY + action.getVideoId() + ":" + action.getRoot());
        } else {
            // 删除
        }
        consumer.commitAsync();
    }

    @Transactional
    public void addComment(Comment comment) {
        commentMapper.insertComment(comment);
        // 先写数据库，再删缓存
//        stringRedisTemplate.delete(COMMENT_INDEX_KEY + comment.getVideoId() + ":" + comment.getRoot());
    }
}

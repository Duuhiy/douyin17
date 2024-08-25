package org.example.service.impl;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.entity.FavoriteActionRequest;
import org.example.entity.Video;
import org.example.service.FavorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class FavorServiceImpl implements FavorService {
    @Resource
    private FavorService favorService;
    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;
    private static final String FAVOR_TOPIC = "favorTopic";
    @Override
    public void action(FavoriteActionRequest action) throws ExecutionException, InterruptedException {
        if (action.getActionType() != 1 && action.getActionType() != 2) {
            return;
        }
        CompletableFuture<SendResult<String, Object>> cf = kafkaTemplate.send(FAVOR_TOPIC, "key", action);
        cf.whenComplete((v, e) -> {
            if (e != null) {
                log.error("发送失败: {}", e.getMessage());
            }
            log.info("发送成功: {}", v.getRecordMetadata());
        });
    }

    @KafkaListener(topics = FAVOR_TOPIC, groupId = "favor-C")
    public void commentConsume(ConsumerRecord<String, String> record) {
        FavoriteActionRequest action = JSONUtil.toBean(record.value(), FavoriteActionRequest.class);
        favorService.addFavor(action);
    }

    @Transactional
    public void addFavor(FavoriteActionRequest action) {
        // video表视频点赞数

        // 操作表记录点赞操作

        // user表点赞数
        if (action.getActionType() == 1) {
            // update video set favorite_count = favorite_count + 1 where video_id = ? on duplicate key update delete = 0;
            // insert into favorite (user_id, video_id) values(?, ?);
        } else {
            // update video set favorite_count = favorite_count - 1 where video_id = ?;
            // update favorite set delete = 1 where user_id = ? and video_id = ?;
        }
    }

    @Override
    public List<Video> list(long userId) {
        //
        return null;
    }


}

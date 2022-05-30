package com.zxc.community.event;

import com.alibaba.fastjson.JSONObject;
import com.zxc.community.entity.Event;
import com.zxc.community.entity.Message;
import com.zxc.community.service.MessageService;
import com.zxc.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("队列消息为空!");
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);

        if (event != null) {
            logger.error("消息格式错误!");
        }

        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        message.setToId(event.getEntityUserId());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.insertMessage(message);
    }
}

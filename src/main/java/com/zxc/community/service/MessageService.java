package com.zxc.community.service;

import com.zxc.community.dao.MessageMapper;
import com.zxc.community.entity.Message;
import com.zxc.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> selectConversationList(int userId, int limit, int offset) {
        return messageMapper.selectConversationList(userId, limit, offset);
    }

    public int selectConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }


    public List<Message> selectLetters(String conversationId, int limit , int offset) {
        return messageMapper.selectLetters(conversationId,limit,offset);
    }

    public int selectLettersCount(String conversationId) {
        return messageMapper.selectLettersCount(conversationId);
    }

    public int selectUnreadLettersCount(String conversationId, int userId) {
        return messageMapper.selectUnreadLettersCount(conversationId,userId);
    }

    public void insertMessage(Message message) {
        String s = HtmlUtils.htmlEscape(message.getContent());
        message.setContent(sensitiveFilter.filter(s));

        messageMapper.insertMessage(message);
    }

    public void readMessage(List<Integer> ids) {
        messageMapper.updateStatus(ids,1);
    }


    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    public int findNoticeUnreadCount(int userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    public List<Message> findNotices(int userId, String topic, int limit, int offset) {
        return messageMapper.findNotices(userId, topic, limit, offset);
    }
}

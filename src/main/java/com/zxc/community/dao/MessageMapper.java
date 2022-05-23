package com.zxc.community.dao;

import com.zxc.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户的会话列表 针对每个会话只返回一条最新私信
    List<Message> selectConversationList(int userId, int limit, int offset);

    //查询当前user会话数量
    int selectConversationCount(int userId);

    //查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId, int limit , int offset);

    //某个会话所包含的私信数量
    int selectLettersCount(String conversationId);

    //查询未读的私信数量
    int selectUnreadLettersCount(String conversationId, int userId);

    void insertMessage(Message message);

    void updateStatus(List<Integer> ids , int status);

    // 查询某个主题下最新的通知
    Message selectLatestNotice(int userId, String topic);

    // 查询某个主题所包含的通知数量
    int selectNoticeCount(int userId, String topic);

    // 查询未读的通知的数量
    int selectNoticeUnreadCount(int userId, String topic);

    List<Message> findNotices(int userId, String topic, int limit, int offset);
}

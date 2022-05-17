package com.zxc.community.dao;

import com.zxc.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    //根据类型 id 偏移量 和 limit 查找偏移量
    List<Comment> selectCommentByType(int entityType, int entityId, int limit, int offset);

    //查找entity总数
    int selectCommentCount(int entityType, int entityId);

    int insertComment(Comment comment);
}

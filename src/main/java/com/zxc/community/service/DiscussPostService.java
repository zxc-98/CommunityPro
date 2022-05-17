package com.zxc.community.service;

import com.zxc.community.dao.DiscussPostMapper;
import com.zxc.community.entity.DiscussPost;
import com.zxc.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int insertDiscussPost(DiscussPost discussPost) {
        // 转义 防止html对象引用
        String title = HtmlUtils.htmlEscape(discussPost.getTitle());
        String content = HtmlUtils.htmlEscape(discussPost.getContent());

        discussPost.setTitle(sensitiveFilter.filter(title));
        discussPost.setContent(sensitiveFilter.filter(content));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost selectDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }
}

package com.zxc.community.controller;

import com.zxc.community.entity.Comment;
import com.zxc.community.entity.DiscussPost;
import com.zxc.community.entity.Event;
import com.zxc.community.entity.User;
import com.zxc.community.event.EventProducer;
import com.zxc.community.service.CommentService;
import com.zxc.community.service.DiscussPostService;
import com.zxc.community.util.CommunityConstant;
import com.zxc.community.util.HostHolder;
import com.zxc.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@RequestMapping("comment")
@Controller
public class CommentController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/add/{postId}", method = RequestMethod.POST)
    public String insertDiscussPost(@PathVariable("postId") int postId, Comment comment) {
        User user = hostHolder.getUser();

        comment.setCreateTime(new Date());
        comment.setStatus(0);
        comment.setUserId(user.getId());
        commentService.insertComment(comment);

        Event event = new Event();
        event.setTopic(TOPIC_COMMENT)
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setUserId(user.getId())
                .setData("postId", postId);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost discussPost = discussPostService.selectDiscussPostById(comment.getEntityId());
            event.setEntityUserId(discussPost.getUserId());
        }
        else if(comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(postId);
            eventProducer.fireEvent(event);
            // 计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }



        return "redirect:/discuss/detail/" + postId;
    }
}

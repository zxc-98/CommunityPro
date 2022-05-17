package com.zxc.community.controller;

import com.zxc.community.entity.Comment;
import com.zxc.community.entity.User;
import com.zxc.community.service.CommentService;
import com.zxc.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@RequestMapping("comment")
@Controller
public class CommentController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add/{postId}", method = RequestMethod.POST)
    public String insertDiscussPost(@PathVariable("postId") int postId, Comment comment) {
        User user = hostHolder.getUser();

        comment.setCreateTime(new Date());
        comment.setStatus(0);
        comment.setUserId(user.getId());


        commentService.insertComment(comment);

        return "redirect:/discuss/detail/" + postId;
    }
}

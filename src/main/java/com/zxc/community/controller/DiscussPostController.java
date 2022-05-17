package com.zxc.community.controller;

import com.zxc.community.dao.CommentMapper;
import com.zxc.community.entity.Comment;
import com.zxc.community.entity.DiscussPost;
import com.zxc.community.entity.Page;
import com.zxc.community.entity.User;
import com.zxc.community.service.CommentService;
import com.zxc.community.service.DiscussPostService;
import com.zxc.community.service.UserService;
import com.zxc.community.util.CommunityConstant;
import com.zxc.community.util.CommunityUtil;
import com.zxc.community.util.HostHolder;
import com.zxc.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {


    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录哦!");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());

        discussPostService.insertDiscussPost(discussPost);
        return CommunityUtil.getJSONString(0, "发布成功");
    }

    @RequestMapping(path = "/detail/{postId}", method = RequestMethod.GET)
    public String addDiscussPost(@PathVariable("postId") int id, Model model, Page page) {
        //帖子
        DiscussPost post = discussPostService.selectDiscussPostById(id);
        model.addAttribute("post", post);

        //作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        //评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" +id);
        page.setRows(post.getCommentCount());

        //
        List<Comment> comments = commentService.selectCommentByType(ENTITY_TYPE_POST, id, page.getLimit(), page.getOffset());

        // 评论: 给帖子的评论
        // 回复: 给评论的评论
        // 评论VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (comments != null) {
            for (Comment comment : comments) {
                // 评论VO
                Map<String, Object> commentVO = new HashMap<>();
                // 评论
                commentVO.put("comment", comment);
                // 作者
                commentVO.put("user", userService.findUserById(comment.getUserId()));

                //回复列表
                List<Comment> replyList = commentService.selectCommentByType(ENTITY_TYPE_COMMENT, comment.getId(), Integer.MAX_VALUE, 0);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVO = new HashMap<>();
                        replyVO.put("reply", reply);
                        replyVO.put("user", userService.findUserById(reply.getUserId()));

                        //回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getUserId());
                        replyVO.put("target", target);
                        replyVoList.add(replyVO);
                    }
                }
                commentVO.put("replys", replyVoList);

                //回复数量
                int count = commentService.selectCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("replyCount", count);
                commentVoList.add(commentVO);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}

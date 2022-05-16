package com.zxc.community.controller;

import com.zxc.community.entity.DiscussPost;
import com.zxc.community.entity.User;
import com.zxc.community.service.DiscussPostService;
import com.zxc.community.service.UserService;
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

import java.util.Date;
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {


    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

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
    public String addDiscussPost(@PathVariable("postId") int id, Model model) {
        DiscussPost post = discussPostService.selectDiscussPostById(id);
        model.addAttribute("post", post);

        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        return "/site/discuss-detail";
    }
}

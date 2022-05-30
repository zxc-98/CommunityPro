package com.zxc.community.controller;

import com.zxc.community.entity.Event;
import com.zxc.community.entity.Page;
import com.zxc.community.entity.User;
import com.zxc.community.event.EventProducer;
import com.zxc.community.service.FollowService;
import com.zxc.community.util.CommunityConstant;
import com.zxc.community.util.CommunityUtil;
import com.zxc.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant{

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;


    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setEntityUserId(entityId)
                .setEntityId(entityId)
                .setEntityType(entityType)
                .setUserId(user.getId());

        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "已关注!");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "已取消关注!");
    }


    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String findFolloweeList(@PathVariable("userId") int userId, Page page, Model model) {
        User user = hostHolder.getUser();
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        //set page
        page.setLimit(5);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));
        page.setPath("/followees/" + userId);

        List<Map<String, Object>> followeeList = followService.findFolloweeList(userId, page.getLimit(), page.getOffset());

        for (Map<String, Object> map : followeeList) {
            User u = (User) map.get("user");
            map.put("hasFollowed", hasFollower(u.getId()));
        }
        model.addAttribute("users", followeeList);

        return "/site/followee";
    }

    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String findFollowerList(@PathVariable("userId") int userId, Page page, Model model) {
        User user = hostHolder.getUser();
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        //set page
        page.setLimit(5);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));
        page.setPath("/followers/" + userId);

        List<Map<String, Object>> followerList = followService.findFollowerList(userId, page.getLimit(), page.getOffset());

        for (Map<String, Object> map : followerList) {
            User u = (User) map.get("user");
            map.put("hasFollowed", hasFollower(u.getId()));
        }
        model.addAttribute("users", followerList);

        return "/site/follower";
    }


    private boolean hasFollower(int id) {
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.findHasFollowEntity(hostHolder.getUser().getId(), ENTITY_TYPE_USER, id);
    }

}

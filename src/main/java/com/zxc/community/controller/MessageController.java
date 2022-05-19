package com.zxc.community.controller;

import com.zxc.community.entity.Message;
import com.zxc.community.entity.Page;
import com.zxc.community.entity.User;
import com.zxc.community.service.MessageService;
import com.zxc.community.service.UserService;
import com.zxc.community.util.CommunityUtil;
import com.zxc.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        // set page
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.selectConversationCount(user.getId()));

        List<Map<String, Object>> conversations = new ArrayList<>();
        // set conversationList
        List<Message> messages = messageService.selectConversationList(user.getId(), page.getLimit(), page.getOffset());
        if (messages != null) {
            for (Message message : messages) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.selectLettersCount(message.getConversationId()));
                map.put("unreadCount", messageService.selectUnreadLettersCount(message.getConversationId(), user.getId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }

        model.addAttribute("conversations", conversations);

        model.addAttribute("letterUnreadCount", messageService.selectUnreadLettersCount(null, user.getId()));
        return "site/letter";
    }


    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        //set page
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.selectLettersCount(conversationId));
        page.setLimit(5);

        List<Map<String, Object>> letterList = new ArrayList<>();

        List<Message> letters = messageService.selectLetters(conversationId, page.getLimit(), page.getOffset());
        if (letters != null) {
            for (Message letter : letters) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", letter);
                map.put("fromUser", userService.findUserById(letter.getFromId()));
                letterList.add(map);
            }
        }

        model.addAttribute("letters", letterList);
        model.addAttribute("target", getTargetUser(conversationId));

        List<Integer> ids = getLetterIds(letters);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private List<Integer> getLetterIds(List<Message> letters) {
        List<Integer> ids = new ArrayList<>();
        if (letters != null) {
            for (Message letter : letters) {
                if (hostHolder.getUser().getId() == letter.getToId() && letter.getStatus() == 0) {
                    ids.add(letter.getId());
                }
            }
        }

        return ids;
    }


    private User getTargetUser(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);


        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        }
        else {
            return userService.findUserById(id0);
        }
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User toUser = userService.findUserByName(toName);
        if (toUser == null) {
            return CommunityUtil.getJSONString(1, "发送用户不存在");
        }
        int fromId = hostHolder.getUser().getId();
        int toId = toUser.getId();
        Message message = new Message();
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setStatus(0);
        message.setFromId(fromId);
        message.setToId(toId);

        String conversationId;
        if (fromId > toId) {
            conversationId = toId + "_" + fromId;
        }
        else {
            conversationId = fromId + "_" + toId;
        }

        message.setConversationId(conversationId);

        messageService.insertMessage(message);

        return CommunityUtil.getJSONString(0);
    }
}

package com.zxc.community.controller.interceptor;

import com.zxc.community.entity.User;
import com.zxc.community.service.MessageService;
import com.zxc.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            int lettersCount = messageService.selectUnreadLettersCount(null, user.getId());

            modelAndView.addObject("allUnreadCount", lettersCount + noticeUnreadCount);
        }
    }

}

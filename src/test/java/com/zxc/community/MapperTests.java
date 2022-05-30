package com.zxc.community;

import com.zxc.community.dao.DiscussPostMapper;
import com.zxc.community.dao.LoginTicketMapper;
import com.zxc.community.dao.UserMapper;
import com.zxc.community.entity.DiscussPost;
import com.zxc.community.entity.LoginTicket;
import com.zxc.community.entity.User;
import com.zxc.community.service.AlphaService;
import com.zxc.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private AlphaService alphaService;

    @Test
    public void testDiscussPostMapper() {
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(199);
        discussPost.setTitle("test");
        discussPost.setContent("xxxxx");
        discussPost.setCreateTime(new Date());
        int i = discussPostMapper.insertDiscussPost(discussPost);
        System.out.println(i);
    }

    @Test
    public void testSensitiveFilter() {
        String text = "真是❤小❤学❤生❤";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
    }

    @Test
    public void testSelectPosts() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10,0);
        for(DiscussPost post : list) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }


    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abs");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        int i = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(i);
    }

    @Test
    public void testSelectLogin(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abs");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abs", 1);
        System.out.println(loginTicket);
    }


    @Test
    public void testTransactional(){
        Object ans = alphaService.save1();
        System.out.println(ans);
    }

}

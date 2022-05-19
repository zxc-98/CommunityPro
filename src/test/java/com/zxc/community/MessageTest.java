package com.zxc.community;

import com.zxc.community.dao.MessageMapper;
import com.zxc.community.entity.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MessageTest {
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testMessageSelect() {
        List<Message> messages = messageMapper.selectLetters("111_112", 5, 0);
        for (Message message : messages) {
            System.out.println(message);
        }

    }
}

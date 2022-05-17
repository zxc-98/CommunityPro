package com.zxc.community;

import com.zxc.community.dao.CommentMapper;
import com.zxc.community.entity.Comment;
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
public class CommentTests {
    @Autowired
    private CommentMapper commentMapper;
    @Test
    public void testSelect() {
        List<Comment> comments = commentMapper.selectCommentByType(1, 228, 5, 0);
        for (Comment comment : comments) {
            System.out.println(comment);
        }
    }
}

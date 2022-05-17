package com.zxc.community.service;

import com.zxc.community.dao.AlphaDao;
import com.zxc.community.dao.UserMapper;
import com.zxc.community.entity.User;
import com.zxc.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
//@Scope("prototype")
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    public AlphaService() {
//        System.out.println("实例化AlphaService");
    }

    @PostConstruct
    public void init() {
//        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destroy() {
//        System.out.println("销毁AlphaService");
    }

    public String find() {
        return alphaDao.select();
    }

    //REQUIRED ：支持当前事务，如果不存在，则创建新事务
    //REQUIRES_NEW ：创建新事务，并且暂停当前事务
    //NESTED ： 如果当前存在事务，则嵌套在该事务中执行，否则和REQUIRED一样
    @Transactional(isolation = Isolation.READ_COMMITTED , propagation = Propagation.REQUIRED)
    public Object save1(){
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword("123" + user.getSalt());
        user.setEmail("alpah@163.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/ppt.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);


        Integer.valueOf("abx");
        return "ok";
    }

}

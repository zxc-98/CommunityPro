package com.zxc.community;

import com.zxc.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.List;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTest {

    @Autowired
    private AlphaService alphaService;

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);


    //spring 普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    //spring 执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    private static void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // jdk thread pool
    @Test
    public void testJDKThreadPool1() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello world!");
            }
        };

        for (int i = 0; i < 10; i++) {
            executorService.submit(runnable);
        }

        sleep(10*1000);
    }

    @Test
    public void testJDKThreadPool2() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello world!");
                sleep(5*1000);
            }
        };

        // initialDelay : 延迟多少秒开始执行任务
        // period ：间隔多少秒执行一次任务 等待前一个任务执行完 任务时间>延迟等待时间 立刻执行下一个任务
        //scheduledExecutorService.scheduleAtFixedRate(runnable, 10*1000, 1000, TimeUnit.MILLISECONDS);

        //任务时间>延迟等待时间 等待一段延迟时间后执行下一个任务
        scheduledExecutorService.scheduleWithFixedDelay(runnable, 5 * 1000, 1000, TimeUnit.MILLISECONDS);
        sleep(30*1000);
    }

    // 3.Spring普通线程池
    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ThreadPoolTaskExecutor");
            }
        };

        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }

        sleep(10000);
    }


    // 4.Spring普通线程池
    @Test
    public void testThreadPoolTaskScheduler() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello world!");
            }
        };

        // initialDelay : 延迟多少秒开始执行任务
        // period ：间隔多少秒执行一次任务 等待前一个任务执行完 任务时间>延迟等待时间 立刻执行下一个任务
        //scheduledExecutorService.scheduleAtFixedRate(runnable, 10*1000, 1000, TimeUnit.MILLISECONDS);

        //任务时间>延迟等待时间 等待一段延迟时间后执行下一个任务
        Date date = new Date(System.currentTimeMillis() + 5000);
        taskScheduler.scheduleWithFixedDelay(runnable, date, 1000);
        sleep(30*1000);
    }

    //异步的方式使用线程池调用该方法(simple method)
    @Test
    public void testSimple1() {
        for (int i = 0; i < 10; i++) {
            alphaService.executor1();
        }
    }

    // 简化地调用异步的方法 定时地去调用
    @Test
    public void testSimple2() {
        sleep(30*1000);
    }
}

package com.zxc.community.service;

import com.zxc.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    // 将指定的IP计入UV
    public void recordUA(String ip) {
        String UAKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(UAKey, ip);
    }

    // 统计指定日期范围内的UV
    public Long getUA(Date startDay , Date endDay) {
        if (startDay == null || endDay == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        List<String> UAList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDay);
        while (!calendar.getTime().after(endDay)) {
            String key = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            UAList.add(key);
            calendar.add(Calendar.DATE, 1);
        }
        String redisKey = RedisKeyUtil.getUVKey(df.format(startDay), df.format(endDay));
        // union calculate special value
        redisTemplate.opsForHyperLogLog().union(redisKey, UAList.toArray());

        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }


    // record DAU
    public void recordDAU(int userId) {
        String dauKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(dauKey, userId, true);
    }

    // calculate DAU
    public long getDAU(Date startDay, Date endDay) {
        if (startDay == null || endDay == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        List<byte[]> DAUList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDay);
        while (!calendar.getTime().after(endDay)) {
            String key = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            DAUList.add(key.getBytes());
            calendar.add(Calendar.DATE, 1);
        }


        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(df.format(startDay), df.format(endDay));
                connection.bitOp(RedisStringCommands.BitOperation.OR, redisKey.getBytes(), DAUList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });
    }
}

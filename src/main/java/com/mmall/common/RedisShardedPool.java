package com.mmall.common;

import com.mmall.utils.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

public class RedisShardedPool {

    //保证启动时加载 sharded jedis连接池
    private static ShardedJedisPool pool;
    //最大连接数
    private static Integer maxTotal = Integer.valueOf(PropertiesUtil.getProperty("redis.max.total","20"));
    //连接池空闲的最大连接数
    private static Integer maxIdel =  Integer.valueOf(PropertiesUtil.getProperty("redis.max.idle","10"));
    //连接池空闲的最小连接数
    private static Integer minIdel = Integer.valueOf(PropertiesUtil.getProperty("redis.min.idle","2"));;

    //borrow一个jedis实例是否验证，，true:获得的是可用连接
    private static Boolean testOnBarrow=Boolean.valueOf(PropertiesUtil.getProperty("redis.test.borrow","true"));;
    //return一个jedis实例是否验证，true:校验放回的连接肯定可用，false不校验
    private  static Boolean testOnReturn = Boolean.valueOf(PropertiesUtil.getProperty("redis.test.return","true"));;

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");

    private static Integer redis1Port = Integer.valueOf(PropertiesUtil.getProperty("redis1.port"));;

    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");

    private static Integer redis2Port = Integer.valueOf(PropertiesUtil.getProperty("redis2.port"));;


    //防止外部调用，只能初始化一次（静态块调用）
    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdel);
        config.setMinIdle(minIdel);

        config.setTestOnBorrow(testOnBarrow);
        config.setTestOnReturn(testOnReturn);
        //连接耗尽时，false报异常，true阻塞等等新的连接(默认值)
        config.setBlockWhenExhausted(true);

        //超时时间2s:默认就是2s
        JedisShardInfo jedisShardInfo1 = new JedisShardInfo(redis1Ip, redis1Port, 1000 * 2);
        JedisShardInfo jedisShardInfo2 = new JedisShardInfo(redis2Ip, redis2Port, 1000 * 2);
        List<JedisShardInfo> jedisShardInfoList = new ArrayList<>(2);
        jedisShardInfoList.add(jedisShardInfo1);
        jedisShardInfoList.add(jedisShardInfo2);
        //一致性哈希
        pool = new ShardedJedisPool(config,jedisShardInfoList, Hashing.MURMUR_HASH,Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static {
        initPool();
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    //封装，放回正常连接
    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    //封装放到坏了的连接池
    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }


    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();
         for(int i=0;i<10;i++){
             jedis.set("key"+i,"value"+i);
         }
        returnResource(jedis);

        pool.destroy();//临时调用，销毁连接池所有连接
        System.out.println("end ");
    }

}

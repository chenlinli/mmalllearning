package com.mmall.common;

import com.mmall.utils.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {

    //保证启动时加载jedis连接池
    private static JedisPool pool;
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

    private static String redisIp = PropertiesUtil.getProperty("redis.ip");

    private static Integer redisPort = Integer.valueOf(PropertiesUtil.getProperty("redis.port"));;


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

        //超时时间2s
        pool = new JedisPool(config,redisIp,redisPort,1000*2);
    }

    static {
        initPool();
    }

    public static Jedis getJedis(){
        return pool.getResource();
    }

    //封装，放回正常连接
    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

    //封装放到坏了的连接池
    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }


    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("key","value");
        returnResource(jedis);

        pool.destroy();//临时调用，销毁连接池所有连接
        System.out.println("end ");
    }

}

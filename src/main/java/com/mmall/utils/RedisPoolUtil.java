package com.mmall.utils;

import com.mmall.common.RedisPool;
import jdk.internal.util.xml.impl.ReaderUTF8;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
public class RedisPoolUtil {


    /**
     *设置带有效期
     * @param key
     * @param value
     * @param exTime:”s“
     * @return
     */
    public static String setEx(String key,String value,int exTime){
        Jedis jedis = null;
        String result  = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("setEx key:{},value:{} error",key,value,e);
            //放回异常的连接
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 重新设置key有效期
     * @param key
     * @param exTime 秒
     * @return
     */
    public static Long expire(String key,int exTime){
        Jedis jedis = null;
        Long result  = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("expire key:{},error",key,e);
            //放回异常的连接
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 设置key value
     * @param key
     * @param value
     * @return
     */
     public static String set(String key,String value){
         Jedis jedis = null;
         String result  = null;
         try {
             jedis = RedisPool.getJedis();
             result = jedis.set(key,value);
         } catch (Exception e) {
            log.error("set key:{},value:{} error",key,value,e);
            //放回异常的连接
            RedisPool.returnBrokenResource(jedis);
         }
        RedisPool.returnResource(jedis);
         return result;
     }

    /**
     * 获得key的value
     * @param key
     * @return
     */
    public static String get(String key){
        Jedis jedis = null;
        String result  = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error",key,e);
            //放回异常的连接
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 删除
     * @param key
     * @return
     */
    public static Long del(String key){
        Jedis jedis = null;
        Long result  = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error",key,e);
            //放回异常的连接
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        Jedis jedis = RedisPool.getJedis();
        RedisPoolUtil.set("key1","v1");
        String v1 = RedisPoolUtil.get("key1");
        RedisPoolUtil.setEx("keyex","vex",60*10);
        RedisPoolUtil.expire("key1",60*20);
        RedisPoolUtil.del("key1");
        System.out.println("end");


    }
}

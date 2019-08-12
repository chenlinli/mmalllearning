package com.mmall.utils;

import com.mmall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

/**
 * 使用redisShardedPoolUtil替换
 */
@Slf4j
public class RedisShardedPoolUtil {


    /**
     *设置带有效期
     * @param key
     * @param value
     * @param exTime:”s“
     * @return
     */
    public static String setEx(String key,String value,int exTime){
        ShardedJedis jedis = null;
        String result  = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("setEx key:{},value:{} error",key,value,e);
            //放回异常的连接
            RedisShardedPool.returnBrokenResource(jedis);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    /**
     * 重新设置key有效期
     * @param key
     * @param exTime 秒
     * @return
     */
    public static Long expire(String key,int exTime){
        ShardedJedis jedis = null;
        Long result  = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("expire key:{},error",key,e);
            //放回异常的连接
            RedisShardedPool.returnBrokenResource(jedis);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    /**
     * 设置key value
     * @param key
     * @param value
     * @return
     */
     public static String set(String key,String value){
         ShardedJedis jedis = null;
         String result  = null;
         try {
             jedis = RedisShardedPool.getJedis();
             result = jedis.set(key,value);
         } catch (Exception e) {
            log.error("set key:{},value:{} error",key,value,e);
            //放回异常的连接
            RedisShardedPool.returnBrokenResource(jedis);
         }
        RedisShardedPool.returnResource(jedis);
         return result;
     }

    /**
     * 获得key的value
     * @param key
     * @return
     */
    public static String get(String key){
        ShardedJedis jedis = null;
        String result  = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error",key,e);
            //放回异常的连接
            RedisShardedPool.returnBrokenResource(jedis);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    /**
     * 删除
     * @param key
     * @return
     */
    public static Long del(String key){
        ShardedJedis jedis = null;
        Long result  = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error",key,e);
            //放回异常的连接
            RedisShardedPool.returnBrokenResource(jedis);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        ShardedJedis jedis = RedisShardedPool.getJedis();
        RedisShardedPoolUtil.set("key1","v1");
        String v1 = RedisShardedPoolUtil.get("key1");
        RedisShardedPoolUtil.setEx("keyex","vex",60*10);
        RedisShardedPoolUtil.expire("key1",60*20);
        RedisShardedPoolUtil.del("key1");
        System.out.println("end");


    }
}

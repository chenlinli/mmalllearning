package com.mmall.common;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenCache {

    public static final String TOKEN_PREFIX = "token_";
    private  static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    //缓存初始化容量1000，最大10000，12小时过期
    public static LoadingCache<String,String > localCache =
            CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000)
                    .expireAfterAccess(12, TimeUnit.HOURS)
                    .build(new CacheLoader<String, String>() {
                        //默认数据加载实现，get没有对应的值（没有命中），调用load加载
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });
    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String val= null;
        try{
            val=localCache.get(key);
            if("null".equals(val)){
                return null;
            }
            return val;
        }catch (Exception e){
            logger.error("localCache error:"+e);
        }
        return null;
    }
}

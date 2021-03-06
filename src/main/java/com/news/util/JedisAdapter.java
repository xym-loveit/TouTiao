package com.news.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * Created by Baoxu on 2017/9/3.
 */
@Component
public class JedisAdapter implements  InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost",6379);
    }

    private Jedis getJedis(){
        return pool.getResource();
    }
    public String get(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("Jedis异常"+e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void set(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key,value);
        } catch (Exception e) {
            logger.error("Jedis异常"+e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long sadd(String key, String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key,value);
        } catch (Exception e) {
            logger.error("Jedis异常"+e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long srem(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key,value);
        } catch (Exception e) {
            logger.error("Jedis异常"+e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 查看key中是否有value
     * 有，返回true
     * 没有，返回false
     * @param key
     * @param value
     * @return
     */
    public boolean sismember(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key,value);
        } catch (Exception e) {
            logger.error("Jedis异常"+e.getMessage());
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long scard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("Jedis异常"+e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setex(String key, String value){
        // 验证码, 防机器注册，记录上次注册时间，有效期天60s
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.setex(key,60,value);
        } catch (Exception e) {
            logger.error("Jedis异常"+e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 以JSON的形式存入一个对象
     * @param key
     * @param object
     */
    public void setObject(String key,Object object){
        set(key, JSON.toJSONString(object));
    }

    /**
     * 以JSON的形式获得一个对象
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getObject(String key,Class<T> clazz){
        String value = get(key);
        if (value != null) {
            return JSON.parseObject(value,clazz);
        }
        return null;
    }
}

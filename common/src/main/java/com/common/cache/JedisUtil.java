package com.common.cache;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class JedisUtil {

    @Autowired
    private ShardedJedisPool sPool;

    private static final Logger log = LoggerFactory.getLogger(JedisUtil.class);

    /**
     *获取缓存
     * @param key
     * @param type 缓存类型
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> type) throws Exception{
        ShardedJedis jedis = sPool.getResource();

        try {
            return JSON.parseObject(jedis.get(key), type);
        } catch (Exception e) {
            log.error("错误 : " + e);
            throw e;
        } finally {
            jedis.close();

        }

    }

    /**
     *加入缓存
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        ShardedJedis jedis = sPool.getResource();
        try {
            jedis.set(key, JSON.toJSONString(value));
        } catch (Exception e) {
            log.error("错误 : " + e);
        } finally {
            jedis.close();

        }

    }

    /**
     * set(设置过期时间版)
     * @param key
     * @param value
     * @param seconds
     */
    public void set(String key, Object value, int seconds) {
        ShardedJedis jedis = sPool.getResource();
        try {
            jedis.setex(key, seconds, JSON.toJSONString(value));
        } catch (Exception e) {
            log.error("错误 : " + e);
        } finally {
            jedis.close();

        }

    }


    /**
     * set(无重复则添加)
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public boolean setnx(String key, Object value, int seconds) throws Exception{
        ShardedJedis jedis = sPool.getResource();
        try {
            Long result = jedis.setnx(key, JSON.toJSONString(value));
            if (result == 1) {
                jedis.expire(key, seconds);
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            log.error("错误 : " + e);
            throw e;
        } finally {
            jedis.close();

        }

    }



    /**
     * hashset(带过期时间)
     * @param key
     * @param field
     * @param value
     * @param seconds
     */
    public void hset(String key, String field, Object value, int seconds) throws Exception {
        ShardedJedis jedis = sPool.getResource();
        try {
            jedis.hset(key, field, JSON.toJSONString(value));
            jedis.expire(key, seconds);
        } catch (Exception e) {
            log.error("错误 : " + e);
            throw e;
        } finally {
            jedis.close();

        }
    }
    /**
     * hashset(不带过期时间)
     * @param key
     * @param field
     * @param value
     * @param
     */
    public void hset(String key, String field, Object value) throws Exception{
        ShardedJedis jedis = sPool.getResource();
        try {
            jedis.hset(key, field, JSON.toJSONString(value));
        } catch (Exception e) {
            log.error("错误 : " + e);
            throw e;
        } finally {
            jedis.close();

        }
    }


    /**
     * hashget
     * @param type
     * @param key
     * @param field
     * @param <T>
     * @return
     */
    public <T> T hget(Class<T> type, String key, String field) throws Exception{
        ShardedJedis jedis = sPool.getResource();
        try {
            return JSON.parseObject(jedis.hget(key, field), type);
        } catch (Exception e) {
            log.error("错误 : " + e);
            throw e;
        } finally {
            jedis.close();

        }
    }

    /**
     * 删除缓存
     * @param key
     */
    public void del(String key) throws Exception{
        ShardedJedis jedis = sPool.getResource();
        try {
            jedis.del(key);
        } catch (Exception e) {
            log.error("错误 : " + e);
            throw e;
        } finally {
            jedis.close();

        }
    }

    /**
     * hash类型删除
     * @param key
     * @param fields
     */
    public void hdel(String key, String... fields) {

        ShardedJedis jedis = sPool.getResource();

        try {
            if (fields == null || fields.length < 1) {
                throw new Exception("请指定要删除的fields");
            }
            jedis.hdel(key, fields);
        } catch (Exception e) {
            log.error("错误 : " + e);
        } finally {
            jedis.close();

        }
    }

    /**
     * hash批量添加
     * @param key
     * @param map
     * @param seconds
     */
    public void hmset(String key, Map<String, String> map, int seconds) throws Exception{
        if (map.size()==0)
            return;
        ShardedJedis jedis = sPool.getResource();
        try {
            jedis.hmset(key, map);
            jedis.expire(key, seconds);
        } catch (Exception e) {
            log.error("错误 : " + e);
            throw e;
        } finally {
            jedis.close();
        }


    }
    /**
     * hash批量添加
     * @param key
     * @param map
     * @param
     */
    public void hmset(String key, Map<String, String> map) throws Exception{
        if (map.size()==0)
            return;
        ShardedJedis jedis = sPool.getResource();
        try {
            jedis.hmset(key, map);
        } catch (Exception e) {
            log.error("错误 : " + e);
            throw e;
        } finally {
            jedis.close();
        }

    }

    /**
     * hash批量获取
     * @param key
     * @param fields
     * @return
     */
    public List<String> hmget(String key, String... fields) throws Exception{
        ShardedJedis jedis = sPool.getResource();
        try {
            return jedis.hmget(key, fields);
        } catch (Exception e) {
            log.error("错误 : " + e);
            throw e;
        } finally {
            jedis.close();
        }
    }

    /**
     * 获取一个哈希表中的所有field名
     *
     * @param key hashKey 名称
     * @return
     */
    public Set<String> hkeys(String key) throws Exception{
        ShardedJedis jedis = sPool.getResource();
        try {
            return jedis.hkeys(key);
        } catch (Exception e) {
            log.error("错误 : " + e);
            throw e;
        } finally {
            jedis.close();
        }
    }


    /**
     * 哈希表 key 中所有域的值
     *
     * @param key hashKey 名称
     * @return
     */
    public List<String> hvals(String key) throws Exception{
        ShardedJedis jedis = sPool.getResource();
        try {
            return jedis.hvals(key);
        } catch (Exception e) {
            log.error("错误 : " + e);
            throw e;
        } finally {
            jedis.close();
        }
    }

    /**
     * 返回哈希表 key 中，所有的域和值
     *
     * @param key key hashKey 名称
     * @return
     */
    public Map<String, String> hgetAll(String key) throws Exception{
        ShardedJedis jedis = sPool.getResource();
        try {
            return jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("错误 : " + e);
            throw e;
        } finally {
            jedis.close();
        }
    }


    /**
     * 计数器
     *
     * @param key
     * @param seconds
     * @return
     */
    public long inc(String key, int seconds) throws Exception{
        ShardedJedis jedis = sPool.getResource();
        try {
            boolean exist = jedis.exists(key);
            long count = jedis.incr(key);
            if (!exist) {
                jedis.expire(key, seconds);
            }
            return count;
        } catch (Exception e) {
            log.error("错误 : " + e);
            throw e;
        } finally {
            jedis.close();
        }
    }

    /**
     * 递减
     *
     * @param key
     * @param seconds
     * @return
     */
    public long decr(String key, int seconds) throws Exception{
        ShardedJedis jedis = sPool.getResource();
        try {
            Long val = this.get(key, Long.class);
            if (val > 0) {
                return jedis.decr(key);
            }
            return 0;
        } catch (Exception e) {
            log.error("错误 : " + e);
            throw e;
        } finally {
            jedis.close();
        }
    }


}

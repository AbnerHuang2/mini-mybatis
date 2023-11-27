package org.skitii.ibatis.cache.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.skitii.ibatis.cache.Cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author skitii
 * @since 2023/11/27
 **/
@Slf4j
public class PerpetualCache implements Cache {
    private String id;
    private Map<Object, Object> cache = new HashMap<>();

    public PerpetualCache(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void putObject(Object key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object getObject(Object key) {
        Object obj = cache.get(key);
        if (null != obj) {
            log.info("一级缓存 \r\nkey：{} \r\nval：{}", key, JSON.toJSONString(obj));
        }
        return obj;
    }

    @Override
    public Object removeObject(Object key) {
        return cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int getSize() {
        return cache.size();
    }
}

package org.skitii.ibatis.binding;

import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * @author skitii
 * @since 2023/11/08
 * 负责将xml定义的mapper接口生成代理类注册到MapperRegistry中,
 * 管理mapper接口
 **/
public class MapperRegistry {
    private final Configuration configuration;
    private Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    public MapperRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new BindingException("Error getting mapper instance. Cause: " + e, e);
        }
    }

    public void addMapper(Class<?> mapperInterface) {
        if (mapperInterface.isInterface()) {
            if (hasMapper(mapperInterface)) {
                throw new BindingException("Type " + mapperInterface + " is already known to the MapperRegistry.");
            }
            knownMappers.put(mapperInterface, new MapperProxyFactory<>(mapperInterface));
        }
    }

    private boolean hasMapper(Class<?> mapperInterface) {
        return knownMappers.containsKey(mapperInterface);
    }
}

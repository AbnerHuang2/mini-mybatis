package org.skitii.ibatis.binding;

import org.skitii.ibatis.session.SqlSession;

import java.lang.reflect.Proxy;

/**
 * @author skitii
 * @since 2023/11/08
 * 负责创建mapper代理类
 **/
public class MapperProxyFactory<T> {
    private Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(SqlSession sqlSession) {
        return (T) Proxy.newProxyInstance(
                mapperInterface.getClassLoader(),
                new Class[]{mapperInterface},
                new MapperProxy(sqlSession, mapperInterface));
    }

}

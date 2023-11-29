package org.skitii.middleware.spring;

import org.skitii.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.FactoryBean;

import javax.annotation.Resource;

/**
 * @author skitii
 * @since 2023/11/28
 **/
public class MapperFactoryBean<T> implements FactoryBean<T> {
    private Class<T> mapperInterface;
    @Resource
    private SqlSessionFactory sqlSessionFactory;

    public MapperFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public T getObject() throws Exception {
        return sqlSessionFactory.openSession().getMapper(mapperInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }
}

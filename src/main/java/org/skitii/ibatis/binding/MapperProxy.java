package org.skitii.ibatis.binding;

import org.skitii.ibatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author skitii
 * @since 2023/11/08
 * mapper代理类,负责执行mapper接口的方法，实际上是调用SqlSession的方法
 **/
public class MapperProxy<T> implements InvocationHandler, Serializable {

    private SqlSession sqlSession;

    private final Class<T> mapperInterface;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO 如何通过反射执行语句？
        /**
         * 1. 通过反射获取方法名
         * 2. 通过反射获取参数
         * 3. 通过反射获取返回值类型
         * 4. 从sqlSession获取mapper对应的sql语句
         * 5. 通过sqlSession执行sql
         * 6. 处理resultMap 返回结果
         */

        return String.format("method: %s 被代理了, args: %s\n", method.getName(), args);
    }
}

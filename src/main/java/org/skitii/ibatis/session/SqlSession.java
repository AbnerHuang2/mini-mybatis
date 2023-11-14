package org.skitii.ibatis.session;

import java.util.List;

/**
 * @author skitii
 * @since 2023/11/07
 **/
public interface SqlSession {

    Configuration getConfiguration();

    <T> T getMapper(Class<T> type);

    <T> T selectOne(String statement);

    <T> T selectOne(String statement, Object[] parameter);

    <T> List<T> selectList(String statement);

    <T> List<T> selectList(String statement, Object parameter);
}

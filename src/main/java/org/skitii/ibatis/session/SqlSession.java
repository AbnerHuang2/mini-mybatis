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

    <T> T selectOne(String statement, Object parameter);

    <T> List<T> selectList(String statement);

    <T> List<T> selectList(String statement, Object parameter);

    int insert(String statement, Object parameter);
    int update(String statement, Object parameter);
    int delete(String statement, Object parameter);

    /**
     * 以下是事务控制方法 commit,rollback
     * Flushes batch statements and commits database connection.
     * Note that database connection will not be committed if no updates/deletes/inserts were called.
     */
    void commit();

    /**
     * 关闭Session
     */
    void close();

    /**
     * 清理 Session 缓存
     */
    void clearCache();

}

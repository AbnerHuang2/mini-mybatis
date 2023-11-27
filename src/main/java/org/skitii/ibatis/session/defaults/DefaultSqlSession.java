package org.skitii.ibatis.session.defaults;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.skitii.ibatis.executor.Executor;
import org.skitii.ibatis.mapping.BoundSql;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.session.RowBounds;
import org.skitii.ibatis.session.SqlSession;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author skitii
 * @since 2023/11/07
 **/
@Slf4j
public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;

    Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public <T> T selectOne(String statement) {
        return selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<Object> objects = selectList(statement, parameter);
        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else if (objects.size() > 1) {
            throw new RuntimeException("Too many result");
        } else {
            return null;
        }
    }

    @Override
    public <T> List<T> selectList(String statement) {
        return selectList(statement, null);
    }

    @Override
    public <T> List<T> selectList(String statement, Object parameter) {
        log.info("执行查询 statement：{} parameter：{}", statement, JSON.toJSONString(parameter));
        try {
            MappedStatement mappedStatement = configuration.getMappedStatement(statement);
            return executor.query(mappedStatement, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public int insert(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public int update(String statement, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        try {
            return executor.update(mappedStatement, parameter);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating database.  Cause: " + e);
        }
    }

    @Override
    public int delete(String statement, Object parameter) {
        return update(statement, parameter);
    }
}

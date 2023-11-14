package org.skitii.ibatis.session.defaults;

import org.skitii.ibatis.executor.Executor;
import org.skitii.ibatis.mapping.BoundSql;
import org.skitii.ibatis.mapping.Environment;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author skitii
 * @since 2023/11/07
 **/
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
    public <T> T selectOne(String statement, Object[] parameter) {
        try {
            MappedStatement mappedStatement = configuration.getMappedStatement(statement);
            List<T> list = executor.query(mappedStatement, parameter, Executor.NO_RESULT_HANDLER, mappedStatement.getBoundSql());
            return list.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> List<T> selectList(String statement) {
        return selectList(statement, null);
    }

    @Override
    public <T> List<T> selectList(String statement, Object parameter) {
        try {
            MappedStatement mappedStatement = configuration.getMappedStatement(statement);
            return executor.query(mappedStatement, parameter, Executor.NO_RESULT_HANDLER, mappedStatement.getBoundSql());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}

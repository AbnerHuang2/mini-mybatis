package org.skitii.ibatis.executor.statement;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.db.meta.JdbcType;
import org.skitii.ibatis.executor.Executor;
import org.skitii.ibatis.executor.resultset.ResultSetHandler;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.mapping.SqlSource;
import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.session.ResultHandler;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author skitii
 * @since 2023/11/14
 **/
public class PreparedStatementHandler extends BaseStatementHandler{

    public PreparedStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, ResultHandler resultHandler) {
        super(executor, mappedStatement, parameterObject, resultHandler);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(boundSql.getSql());
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        //处理参数注入
        parameterHandler.setParameters(ps);
    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return resultSetHandler.handleResultSets(ps);
    }
}

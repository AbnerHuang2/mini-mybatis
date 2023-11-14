package org.skitii.ibatis.executor.statement;

import org.skitii.ibatis.executor.Executor;
import org.skitii.ibatis.executor.resultset.ResultSetHandler;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author skitii
 * @since 2023/11/14
 **/
public class SimpleStatementHandler extends BaseStatementHandler{

    public SimpleStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, ResultHandler resultHandler) {
        super(executor, mappedStatement, parameterObject, resultHandler);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {

    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
        String sql = boundSql.getSql();
        statement.execute(sql);
        return resultSetHandler.handleResultSets(statement);
    }
}

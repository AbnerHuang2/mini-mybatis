package org.skitii.ibatis.executor.statement;

import org.skitii.ibatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author skitii
 * @since 2023/11/14
 **/
public interface StatementHandler {
    Statement prepare(Connection connection) throws SQLException;
    void parameterize(Statement statement) throws SQLException;
    <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;
}

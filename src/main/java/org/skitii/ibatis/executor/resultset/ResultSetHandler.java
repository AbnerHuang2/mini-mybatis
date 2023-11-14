package org.skitii.ibatis.executor.resultset;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author skitii
 * @since 2023/11/14
 **/
public interface ResultSetHandler {

    <E> List<E> handleResultSets(Statement stmt) throws SQLException;
}

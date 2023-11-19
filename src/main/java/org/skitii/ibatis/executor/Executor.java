package org.skitii.ibatis.executor;

import org.skitii.ibatis.mapping.BoundSql;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.session.ResultHandler;
import org.skitii.ibatis.session.RowBounds;
import org.skitii.ibatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * @author skitii
 * @since 2023/11/14
 **/
public interface Executor {

    ResultHandler NO_RESULT_HANDLER = null;

    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql);

    Transaction getTransaction();

    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    void close(boolean forceRollback);

}

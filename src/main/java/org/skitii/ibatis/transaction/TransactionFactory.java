package org.skitii.ibatis.transaction;

import org.skitii.ibatis.session.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author skitii
 * @since 2023/11/09
 **/
public interface TransactionFactory {
    /**
     * 通过 Connection 创建一个Transaction
     * @param conn
     * @return
     */
    Transaction newTransaction(Connection conn);

    /**
     * 通过数据源和事务隔离级别创建一个Transaction
     * @param dataSource
     * @param level
     * @param autoCommit
     * @return
     */
    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);
}

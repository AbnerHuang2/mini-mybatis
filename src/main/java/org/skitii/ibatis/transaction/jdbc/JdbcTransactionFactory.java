package org.skitii.ibatis.transaction.jdbc;

import org.skitii.ibatis.session.TransactionIsolationLevel;
import org.skitii.ibatis.transaction.Transaction;
import org.skitii.ibatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

public class JdbcTransactionFactory implements TransactionFactory {

    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }
}

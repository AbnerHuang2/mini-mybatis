package org.skitii.ibatis.session.defaults;

import org.skitii.ibatis.executor.Executor;
import org.skitii.ibatis.mapping.Environment;
import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.session.SqlSession;
import org.skitii.ibatis.session.SqlSessionFactory;
import org.skitii.ibatis.session.TransactionIsolationLevel;
import org.skitii.ibatis.transaction.Transaction;
import org.skitii.ibatis.transaction.TransactionFactory;

/**
 * @author skitii
 * @since 2023/11/07
 **/
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return openSession(TransactionIsolationLevel.READ_COMMITTED, false);
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel transactionIsolationLevel, boolean autoCommit) {
        final Environment environment = configuration.getEnvironment();
        TransactionFactory transactionFactory = environment.getTransactionFactory();
        Transaction tx = transactionFactory.newTransaction(configuration.getEnvironment().getDataSource(), transactionIsolationLevel, autoCommit);
        // 创建执行器
        final Executor executor = configuration.newExecutor(tx);
        return new DefaultSqlSession(configuration, executor);
    }
}

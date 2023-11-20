package org.skitii.ibatis.session;


/**
 * @author skitii
 * @since 2023/11/07
 **/
public interface SqlSessionFactory {
    SqlSession openSession();

    SqlSession openSession(TransactionIsolationLevel transactionIsolationLevel,boolean autoCommit);

}

package org.skitii.ibatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author skitii
 * @since 2023/11/09
 **/
public interface Transaction {
    Connection getConnection() throws SQLException;

    void commit() throws SQLException;

    void roTransactionllback() throws SQLException;

    void close() throws SQLException;

}

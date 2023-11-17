package org.skitii.ibatis.type;


import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author skitii
 * @since 2023/11/17
 **/
public interface TypeHandler<T> {
    /**
     * 设置参数
     */
    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;
}

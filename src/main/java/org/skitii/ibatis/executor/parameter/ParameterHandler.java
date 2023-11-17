package org.skitii.ibatis.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author skitii
 * @since 2023/11/17
 **/
public interface ParameterHandler {
    /**
     * 获取参数
     */
    Object getParameterObject();

    void setParameters(PreparedStatement ps) throws SQLException;
}

package org.skitii.ibatis.mapping;

import lombok.Data;
import org.skitii.ibatis.session.Configuration;

import java.util.Map;

/**
 * @author skitii
 * @since 2023/11/07
 **/
@Data
public class MappedStatement {
    private Configuration configuration;
    private String namespace;
    private String id;
    private String parameterType;
    private String resultType;
    private String sql;
    private SqlCommandType sqlCommandType;
    private Map<Integer, String> parameter;

    /**
     * 建造者
     */
    public static class Builder {

        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlCommandType sqlCommandType, String parameterType, String resultType, String sql, Map<Integer, String> parameter) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.parameterType = parameterType;
            mappedStatement.resultType = resultType;
            mappedStatement.sql = sql;
            mappedStatement.parameter = parameter;
        }

        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            return mappedStatement;
        }

    }

}

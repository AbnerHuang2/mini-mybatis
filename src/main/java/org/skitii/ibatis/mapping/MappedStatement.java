package org.skitii.ibatis.mapping;

import lombok.Data;
import org.skitii.ibatis.scripting.LanguageDriver;
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

    private SqlSource sqlSource;
    private SqlCommandType sqlCommandType;
    Class<?> resultType;
    private LanguageDriver lang;

    /**
     * 建造者
     */
    public static class Builder {

        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlCommandType sqlCommandType, SqlSource sqlSource, Class<?> resultType) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.sqlSource = sqlSource;
            mappedStatement.resultType = resultType;
            mappedStatement.lang = configuration.getDefaultScriptingLanguageInstance();
        }

        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            return mappedStatement;
        }

    }

}

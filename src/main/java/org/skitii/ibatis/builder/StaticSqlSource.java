package org.skitii.ibatis.builder;

import org.skitii.ibatis.mapping.BoundSql;
import org.skitii.ibatis.mapping.ParameterMapping;
import org.skitii.ibatis.mapping.SqlSource;
import org.skitii.ibatis.session.Configuration;

import java.util.List;

/**
 * @author skitii
 * @since 2023/11/16
 **/
public class StaticSqlSource implements SqlSource {
    private String sql;
    private List<ParameterMapping> parameterMappings;
    private Configuration configuration;

    public StaticSqlSource(Configuration configuration, String sql) {
        this(configuration, sql, null);
    }

    public StaticSqlSource(Configuration configuration, String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.configuration = configuration;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return new BoundSql(configuration, sql, parameterMappings, parameterObject);
    }
}

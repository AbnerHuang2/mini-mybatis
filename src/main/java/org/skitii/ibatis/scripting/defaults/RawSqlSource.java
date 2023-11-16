package org.skitii.ibatis.scripting.defaults;

import org.skitii.ibatis.builder.SqlSourceBuilder;
import org.skitii.ibatis.mapping.BoundSql;
import org.skitii.ibatis.mapping.SqlSource;
import org.skitii.ibatis.scripting.xmltags.DynamicContext;
import org.skitii.ibatis.scripting.xmltags.SqlNode;
import org.skitii.ibatis.session.Configuration;

import java.util.HashMap;

/**
 * @author skitii
 * @since 2023/11/16
 **/
public class RawSqlSource implements SqlSource {
    private SqlSource sqlSource;

    public RawSqlSource(Configuration configuration, SqlNode rootSqlNode, Class<?> parameterType) {
        this(configuration, getSql(configuration, rootSqlNode), parameterType);
    }

    public RawSqlSource(Configuration configuration, String sql, Class<?> parameterType) {
        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
        Class<?> clazz = parameterType == null ? Object.class : parameterType;
        sqlSource = sqlSourceParser.parse(sql, clazz, new HashMap<>());
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }

    private static String getSql(Configuration configuration, SqlNode rootSqlNode) {
        DynamicContext context = new DynamicContext(configuration, null);
        rootSqlNode.apply(context);
        return context.getSql();
    }

}

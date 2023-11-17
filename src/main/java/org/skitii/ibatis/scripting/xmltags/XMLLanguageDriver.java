package org.skitii.ibatis.scripting.xmltags;

import org.dom4j.Element;
import org.skitii.ibatis.executor.parameter.ParameterHandler;
import org.skitii.ibatis.mapping.BoundSql;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.mapping.SqlSource;
import org.skitii.ibatis.scripting.LanguageDriver;
import org.skitii.ibatis.scripting.defaults.DefaultParameterHandler;
import org.skitii.ibatis.session.Configuration;

/**
 * @author skitii
 * @since 2023/11/16
 **/
public class XMLLanguageDriver implements LanguageDriver {
    @Override
    public SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType) {
        // 用XML脚本构建器解析
        XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
        return builder.parseScriptNode();
    }

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }

}

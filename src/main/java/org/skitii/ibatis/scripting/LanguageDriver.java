package org.skitii.ibatis.scripting;

import org.dom4j.Element;
import org.skitii.ibatis.executor.parameter.ParameterHandler;
import org.skitii.ibatis.mapping.BoundSql;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.mapping.SqlSource;
import org.skitii.ibatis.session.Configuration;

/**
 * @author skitii
 * @since 2023/11/16
 * 用于把不同语言的脚本转换成SqlSource，比如XMLLanguageDriver，用于解析XML脚本。 JavaLanguageDriver，用于解析Java脚本等。
 **/
public interface LanguageDriver {
    /**
     * 创建SQL源码(mapper xml方式)
     */
    SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType);

    /**
     * 创建SQL源码(annotation 注解方式)
     */
    SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType);

    ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql);
}

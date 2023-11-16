package org.skitii.ibatis.builder.xml;

import org.dom4j.Element;
import org.skitii.ibatis.builder.BaseBuilder;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.mapping.SqlCommandType;
import org.skitii.ibatis.mapping.SqlSource;
import org.skitii.ibatis.scripting.LanguageDriver;
import org.skitii.ibatis.session.Configuration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLStatementBuilder extends BaseBuilder {
    private String namespace;
    private Element element;

    public XMLStatementBuilder(Configuration configuration, Element element, String namespace) {
        super(configuration);
        this.element = element;
        this.namespace = namespace;
    }

    public void parseStatementNode() {
        String id = element.attributeValue("id");
        String parameterType = element.attributeValue("parameterType");
        Class<?> parameterTypeClass = resolveAlias(parameterType);
        String resultType = element.attributeValue("resultType");
        Class<?> resultTypeClass = resolveAlias(resultType);
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(element.getName().toUpperCase(Locale.ENGLISH));

        // 获取默认语言驱动器
        LanguageDriver driver = configuration.getLanguageRegistry().getDefaultDriver();
        SqlSource sqlSource = driver.createSqlSource(configuration, element, parameterTypeClass);
        String msId = namespace + "." + id;
        MappedStatement.Builder mappedStatementBuilder = new MappedStatement.Builder(configuration, msId, sqlCommandType, sqlSource, resultTypeClass);
        MappedStatement mappedStatement = mappedStatementBuilder.build();
        // 添加到configuration中
        configuration.addMappedStatement(mappedStatement);
    }

}

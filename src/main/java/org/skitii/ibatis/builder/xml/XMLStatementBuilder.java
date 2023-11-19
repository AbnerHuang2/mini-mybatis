package org.skitii.ibatis.builder.xml;

import org.dom4j.Element;
import org.skitii.ibatis.builder.BaseBuilder;
import org.skitii.ibatis.builder.MapperBuildAssistant;
import org.skitii.ibatis.mapping.SqlCommandType;
import org.skitii.ibatis.mapping.SqlSource;
import org.skitii.ibatis.scripting.LanguageDriver;
import org.skitii.ibatis.session.Configuration;

import java.util.Locale;

public class XMLStatementBuilder extends BaseBuilder {
    private String namespace;
    private Element element;
    private MapperBuildAssistant builderAssistant;

    public XMLStatementBuilder(Configuration configuration, Element element, String namespace, MapperBuildAssistant builderAssistant) {
        super(configuration);
        this.element = element;
        this.namespace = namespace;
        this.builderAssistant = builderAssistant;
    }

    public void parseStatementNode() {
        String id = element.attributeValue("id");
        String parameterType = element.attributeValue("parameterType");
        Class<?> parameterTypeClass = resolveAlias(parameterType);
        String resultType = element.attributeValue("resultType");
        String resultMap = element.attributeValue("resultMap");
        Class<?> resultTypeClass = resolveAlias(resultType);
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(element.getName().toUpperCase(Locale.ENGLISH));

        // 获取默认语言驱动器
        LanguageDriver driver = configuration.getLanguageRegistry().getDefaultDriver();
        SqlSource sqlSource = driver.createSqlSource(configuration, element, parameterTypeClass);
        builderAssistant.setCurrentNamespace(namespace);
        // 添加到configuration中
        builderAssistant.addMappedStatement(id, sqlSource, sqlCommandType, parameterTypeClass, resultTypeClass, resultMap, driver);
    }

}

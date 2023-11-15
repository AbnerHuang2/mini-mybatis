package org.skitii.ibatis.builder.xml;

import org.dom4j.Element;
import org.skitii.ibatis.builder.BaseBuilder;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.mapping.SqlCommandType;
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
        String resultType = element.attributeValue("resultType");
        String sql = element.getText();

        // ? 匹配
        Map<Integer, String> parameter = new HashMap<>();
        Pattern pattern = Pattern.compile("(#\\{(.*?)})");
        Matcher matcher = pattern.matcher(sql);
        for (int i = 1; matcher.find(); i++) {
            String g1 = matcher.group(1);
            String g2 = matcher.group(2);
            parameter.put(i, g2);
            sql = sql.replace(g1, "?");
        }
        String msId = namespace + "." + id;
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(element.getName().toUpperCase(Locale.ENGLISH));
        MappedStatement.Builder mappedStatementBuilder = new MappedStatement.Builder(configuration, msId, sqlCommandType, parameterType, resultType, sql, parameter);
        MappedStatement mappedStatement = mappedStatementBuilder.build();
        // 添加到configuration中
        configuration.addMappedStatement(mappedStatement);
    }

}

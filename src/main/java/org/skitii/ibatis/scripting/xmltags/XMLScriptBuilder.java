package org.skitii.ibatis.scripting.xmltags;

import org.dom4j.Element;
import org.skitii.ibatis.builder.BaseBuilder;
import org.skitii.ibatis.mapping.SqlSource;
import org.skitii.ibatis.scripting.defaults.RawSqlSource;
import org.skitii.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skitii
 * @since 2023/11/16
 **/
public class XMLScriptBuilder extends BaseBuilder {
    private Element element;
    private boolean isDynamic;
    private Class<?> parameterType;

    public XMLScriptBuilder(Configuration configuration, Element element, Class<?> parameterType) {
        super(configuration);
        this.element = element;
        this.parameterType = parameterType;
    }

    public SqlSource parseScriptNode() {
        List<SqlNode> contents = parseDynamicTags(element);
        MixedSqlNode rootSqlNode = new MixedSqlNode(contents);
        return new RawSqlSource(configuration, rootSqlNode, parameterType);
    }

    List<SqlNode> parseDynamicTags(Element element) {
        List<SqlNode> contents = new ArrayList<>();
        // element.getText 拿到 SQL
        String data = element.getText();
        contents.add(new StaticTextSqlNode(data));
        return contents;
    }

}

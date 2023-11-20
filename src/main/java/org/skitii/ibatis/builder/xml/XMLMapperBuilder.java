package org.skitii.ibatis.builder.xml;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.skitii.ibatis.builder.BaseBuilder;
import org.skitii.ibatis.builder.MapperBuilderAssistant;
import org.skitii.ibatis.io.Resources;
import org.skitii.ibatis.session.Configuration;
import org.xml.sax.InputSource;

import java.io.Reader;
import java.util.List;

public class XMLMapperBuilder extends BaseBuilder {
    private String resource;
    private Element element;
    private String namespace;
    private MapperBuilderAssistant builderAssistant;

    public XMLMapperBuilder(Configuration configuration, Reader reader, String resource) throws DocumentException {
        super(configuration);
        this.element = new SAXReader().read(new InputSource(reader)).getRootElement();
        this.namespace = element.attributeValue("namespace");
        this.resource = resource;
        this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
    }

    public void parse() throws Exception{
        if (configuration.isResourceLoaded(resource)) {
            return;
        }

        // 2.配置select|insert|update|delete
        // 2.配置select|insert|update|delete
        buildStatementFromContext(element.elements("select"),
                element.elements("insert"),
                element.elements("update"),
                element.elements("delete"));

       configuration.addLoadedResource(resource);
       configuration.addMapper(Resources.classForName(namespace));
    }

    private void buildStatementFromContext(List<Element>... list) {
        for (List<Element> elements : list) {
            for (Element element : elements) {
                XMLStatementBuilder statementBuilder = new XMLStatementBuilder(configuration, element, namespace, builderAssistant);
                statementBuilder.parseStatementNode();
            }
        }
    }

}

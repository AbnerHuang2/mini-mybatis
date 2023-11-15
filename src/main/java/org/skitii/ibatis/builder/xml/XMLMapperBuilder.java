package org.skitii.ibatis.builder.xml;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.skitii.ibatis.builder.BaseBuilder;
import org.skitii.ibatis.io.Resources;
import org.skitii.ibatis.session.Configuration;
import org.xml.sax.InputSource;

import java.io.Reader;
import java.util.List;

public class XMLMapperBuilder extends BaseBuilder {
    private String resource;
    private Element element;
    private String namespace;

    public XMLMapperBuilder(Configuration configuration, Reader reader, String resource) throws DocumentException {
        super(configuration);
        this.element = new SAXReader().read(new InputSource(reader)).getRootElement();
        this.namespace = element.attributeValue("namespace");
        this.resource = resource;
    }

    public void parse() throws Exception{
        if (configuration.isResourceLoaded(resource)) {
            return;
        }

        // 2.配置select|insert|update|delete
        buildStatementFromContext(element.elements("select"));

       configuration.addLoadedResource(resource);
       configuration.addMapper(Resources.classForName(namespace));
    }

    private void buildStatementFromContext(List<Element> list) {
        for (Element ele : list) {
            //解析sql语句
            XMLStatementBuilder statementBuilder = new XMLStatementBuilder(configuration, ele, namespace);
            statementBuilder.parseStatementNode();
        }
    }

}

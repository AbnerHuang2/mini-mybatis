package org.skitii.ibatis.builder.xml;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.skitii.ibatis.builder.BaseBuilder;
import org.skitii.ibatis.builder.MapperBuilderAssistant;
import org.skitii.ibatis.builder.ResultMapResolver;
import org.skitii.ibatis.io.Resources;
import org.skitii.ibatis.mapping.ResultFlag;
import org.skitii.ibatis.mapping.ResultMap;
import org.skitii.ibatis.mapping.ResultMapping;
import org.skitii.ibatis.session.Configuration;
import org.xml.sax.InputSource;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
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
        this.builderAssistant = new MapperBuilderAssistant(configuration, resource,namespace);
    }

    public void parse() throws Exception{
        if (configuration.isResourceLoaded(resource)) {
            return;
        }
        configurationElement(element);

       configuration.addLoadedResource(resource);
       configuration.addMapper(Resources.classForName(namespace));
    }

    private void configurationElement(Element element) {
        // 1.配置resultMap[这一步一定要比语句解析先执行，因为语句解析中可能会用到resultMap]
        resultMapElements(element.elements("resultMap"));

        // 2.配置select|insert|update|delete
        buildStatementFromContext(element.elements("select"),
                element.elements("insert"),
                element.elements("update"),
                element.elements("delete"));

    }

    private void resultMapElements(List<Element> resultMap) {
        for (Element element : resultMap) {
            resultMapElement(element, Collections.emptyList());
        }
    }

    /**
     * 解析resultMap
     * 如：
     * <resultMap id="BaseResultMap" type="org.skitii.ibatis.domain.User">
     *   <id column="id" property="id" jdbcType="INTEGER"/>
     *   <result column="name" property="name" jdbcType="VARCHAR"/>
     *   <result column="age" property="age" jdbcType="INTEGER"/>
     * </resultMap>
     */
    private ResultMap resultMapElement(Element resultMapNode, List<ResultMapping> addtionalResultMappings) {
        String id = resultMapNode.attributeValue("id");
        String type = resultMapNode.attributeValue("type");
        Class<?> typeClass = resolveAlias(type);

        List<ResultMapping> resultMappings = new ArrayList<>(addtionalResultMappings);
        List<Element> resultItems = resultMapNode.elements();
        for (Element item : resultItems) {
            List<ResultFlag> resultFlags = new ArrayList<>();
            if ("id".equals(item.getName())) {
                resultFlags.add(ResultFlag.ID);
            }
            resultMappings.add(buildResultMappingFromContext(item, typeClass, resultFlags));
        }
        //创建结果映射解析器
        ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, resultMappings);
        return resultMapResolver.resolve();
    }

    private ResultMapping buildResultMappingFromContext(Element context, Class<?> resultType, List<ResultFlag> flags) {
        String property = context.attributeValue("property");
        String column = context.attributeValue("column");
        return builderAssistant.buildResultMapping(resultType, property, column, flags);
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

package org.skitii.ibatis.builder.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.skitii.ibatis.builder.BaseBuilder;
import org.skitii.ibatis.datasource.DataSourceFactory;
import org.skitii.ibatis.io.Resources;
import org.skitii.ibatis.mapping.Environment;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.mapping.SqlCommandType;
import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.transaction.TransactionFactory;
import org.xml.sax.InputSource;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author skitii
 * @since 2023/11/08
 **/
public class XMLConfigBuilder extends BaseBuilder {

    private Element root;

    public XMLConfigBuilder(Reader reader) {
        super(new Configuration());
        // 从reader中读取xml信息
        // 2. dom4j 处理 xml
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new InputSource(reader));
            root = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public Configuration parse() {
       try {
           // 解析xml获取environment信息
           environmentsElement(root.element("environments"));
           // 解析xml获取mapper信息
           mapperElement(root.selectNodes("mappers"));
       } catch (Exception e) {

           throw new RuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
       }

        return configuration;
    }

    // 获取数据源配置信息
    private void environmentsElement(Element environments) throws InstantiationException, IllegalAccessException {
        String defaultEnvironmentName = environments.attributeValue("default");

        List<Element> environmentList = environments.elements("environment");
        for (Element environment : environmentList) {
            String environmentName = environment.attributeValue("id");
            if (environmentName.equals(defaultEnvironmentName)) {
                // 设置事务管理器
                TransactionFactory txFactory = (TransactionFactory) typeAliasRegistry.resolveAlias(environment.element("transactionManager").attributeValue("type")).newInstance();
                // 设置默认数据源
                Element dataSource = environment.element("dataSource");
                DataSourceFactory dataSourceFactory = (DataSourceFactory) configuration.getTypeAliasRegistry().resolveAlias(dataSource.attribute("type").getValue()).newInstance();
                List<Element> propertyList = dataSource.elements("property");
                Properties props = new Properties();
                for (Element element : propertyList) {
                    String name = element.attributeValue("name");
                    String value = element.attributeValue("value");
                    props.setProperty(name, value);
                }
                dataSourceFactory.setProperties(props);

                Environment.Builder environmentBuilder = new Environment.Builder(environmentName);
                environmentBuilder.dataSource(dataSourceFactory.getDataSource())
                                .transactionFactory(txFactory);
                configuration.setEnvironment(environmentBuilder.build());
            }
        }
    }

    // 获取SQL语句信息
    private void mapperElement(List<Element> mapperList) {

        Element element = mapperList.get(0);
        List content = element.content();
        for (Object o : content) {
            // 获取具体的mapper对应的xml
            Element e = (Element) o;
            String resource = e.attributeValue("resource");

            try {
                Reader reader = Resources.getResourceAsReader(resource);
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(new InputSource(reader));
                Element root = document.getRootElement();
                //命名空间
                String namespace = root.attributeValue("namespace");

                // SELECT
                List<Element> selectNodes = root.selectNodes("select");
                for (Element node : selectNodes) {
                    String id = node.attributeValue("id");
                    String parameterType = node.attributeValue("parameterType");
                    String resultType = node.attributeValue("resultType");
                    String sql = node.getText();

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
                    SqlCommandType sqlCommandType = SqlCommandType.valueOf(node.getName().toUpperCase(Locale.ENGLISH));
                    MappedStatement.Builder mappedStatementBuilder = new MappedStatement.Builder(configuration, msId, sqlCommandType, parameterType, resultType, sql, parameter);
                    MappedStatement mappedStatement = mappedStatementBuilder.build();
                    // 添加到configuration中
                    configuration.addMappedStatement(mappedStatement);
                }
                configuration.addMapper(Resources.classForName(namespace));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

}

package org.skitii.ibatis.builder.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.skitii.ibatis.builder.BaseBuilder;
import org.skitii.ibatis.io.Resources;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.mapping.SqlCommandType;
import org.skitii.ibatis.session.Configuration;
import org.xml.sax.InputSource;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
        // 解析xml获取datasouce信息
        // 解析xml获取mapper信息
        mapperElement(root.selectNodes("mappers"));
        configuration.setDataSource(dataSource(root.selectNodes("//dataSource")));
        configuration.setConnection(connection(configuration.getDataSource()));
        return configuration;
    }

    // 获取数据源配置信息
    private Map<String,String> dataSource(List<Element> list) {
        Map<String, String> dataSource = new HashMap<>(4);
        Element element = list.get(0);
        List content = element.content();
        for (Object o : content) {
            Element e = (Element) o;
            String name = e.attributeValue("name");
            String value = e.attributeValue("value");
            dataSource.put(name, value);
        }
        return dataSource;
    }
    private Connection connection(Map<String, String> dataSource) {
        try {
            Class.forName(dataSource.get("driver"));
            return DriverManager.getConnection(dataSource.get("url"), dataSource.get("username"), dataSource.get("password"));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
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

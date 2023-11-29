package org.skitii.ibatis.builder.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.skitii.ibatis.builder.BaseBuilder;
import org.skitii.ibatis.datasource.DataSourceFactory;
import org.skitii.ibatis.io.Resources;
import org.skitii.ibatis.mapping.Environment;
import org.skitii.ibatis.plugin.Interceptor;
import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.session.LocalCacheScope;
import org.skitii.ibatis.transaction.TransactionFactory;
import org.xml.sax.InputSource;

import java.io.Reader;
import java.util.List;
import java.util.Properties;

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

    public XMLConfigBuilder(Document doc) {
        super(new Configuration());
        root = doc.getRootElement();
    }

    public Configuration parse() {
       try {
           // 解析xml获取plugin信息
           pluginElement(root.element("plugins"));
           // 解析xml获取settings信息
           settingsElement(root.element("settings"));
           // 解析xml获取environment信息
           environmentsElement(root.element("environments"));
           // 解析xml获取mapper信息
           mapperElement(root.selectNodes("mappers"));
       } catch (Exception e) {
           throw new RuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
       }

        return configuration;
    }

    /**
     * <settings>
     * <!-- 全局缓存：true/false -->
     * <setting name="cacheEnabled" value="false"/>
     * <!--缓存级别：SESSION/STATEMENT-->
     * <setting name="localCacheScope" value="SESSION"/>
     * </settings>
     */
    private void settingsElement(Element context) {
        if (context == null) return;
        List<Element> elements = context.elements();
        Properties props = new Properties();
        for (Element element : elements) {
            props.setProperty(element.attributeValue("name"), element.attributeValue("value"));
        }
        configuration.setCacheEnabled(booleanValueOf(props.getProperty("cacheEnabled"), true));
        configuration.setLocalCacheScope(LocalCacheScope.valueOf(props.getProperty("localCacheScope")));
    }

    protected Boolean booleanValueOf(String value, Boolean defaultValue) {
        return value == null ? defaultValue : Boolean.valueOf(value);
    }

    private void pluginElement(Element parent) throws Exception {
        if (parent == null) return;
        List<Element> elements = parent.elements();
        for (Element element : elements) {
            String interceptor = element.attributeValue("interceptor");
            // 参数配置
            Properties properties = new Properties();
            List<Element> propertyElementList = element.elements("property");
            for (Element property : propertyElementList) {
                properties.setProperty(property.attributeValue("name"), property.attributeValue("value"));
            }
            // 获取插件实现类并实例化：cn.bugstack.mybatis.test.plugin.TestPlugin
            Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).newInstance();
            interceptorInstance.setProperties(properties);
            configuration.addInterceptor(interceptorInstance);
        }
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
                configuration.setDatabaseId(defaultEnvironmentName);
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
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration,reader, resource);
                xmlMapperBuilder.parse();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

}

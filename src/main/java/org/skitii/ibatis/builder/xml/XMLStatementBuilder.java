package org.skitii.ibatis.builder.xml;

import org.dom4j.Element;
import org.skitii.ibatis.builder.BaseBuilder;
import org.skitii.ibatis.builder.MapperBuilderAssistant;
import org.skitii.ibatis.executor.kengen.Jdbc3KeyGenerator;
import org.skitii.ibatis.executor.kengen.KeyGenerator;
import org.skitii.ibatis.executor.kengen.NoKeyGenerator;
import org.skitii.ibatis.executor.kengen.SelectKeyGenerator;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.mapping.SqlCommandType;
import org.skitii.ibatis.mapping.SqlSource;
import org.skitii.ibatis.scripting.LanguageDriver;
import org.skitii.ibatis.session.Configuration;

import java.util.List;
import java.util.Locale;

public class XMLStatementBuilder extends BaseBuilder {
    private String namespace;
    private Element element;
    private MapperBuilderAssistant builderAssistant;

    public XMLStatementBuilder(Configuration configuration, Element element, String namespace, MapperBuilderAssistant builderAssistant) {
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
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
        boolean flushCache = Boolean.parseBoolean(element.attributeValue("flushCache", String.valueOf(!isSelect)));
        boolean useCache = Boolean.parseBoolean(element.attributeValue("useCache", String.valueOf(!isSelect)));
        // 获取默认语言驱动器
        LanguageDriver driver = configuration.getLanguageRegistry().getDefaultDriver();
        SqlSource sqlSource = driver.createSqlSource(configuration, element, parameterTypeClass);

        // 解析<selectKey>
        processSelectKeyNodes(id, parameterTypeClass, driver);

        // 属性标记【仅对 insert 有用】, MyBatis 会通过 getGeneratedKeys 或者通过 insert 语句的 selectKey 子元素设置它的值
        String keyProperty = element.attributeValue("keyProperty");
        KeyGenerator keyGenerator = getKeyGenerator(id, parameterTypeClass, driver, sqlCommandType);

        // 添加到configuration中
        builderAssistant.addMappedStatement(id,
                sqlSource,
                sqlCommandType,
                parameterTypeClass,
                resultTypeClass,
                resultMap,
                flushCache,
                useCache,
                keyGenerator,
                keyProperty,
                driver);
    }


    private void processSelectKeyNodes(String id, Class<?> parameterTypeClass, LanguageDriver langDriver) {
        // 获取<selectKey>节点
        List<Element> selectKeyNodes = element.selectNodes("selectKey");
        // 解析<selectKey>节点
        parseSelectKeyNodes(id, selectKeyNodes, parameterTypeClass, langDriver);
    }

    private void parseSelectKeyNodes(String parentId, List<Element> list, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
        for (Element nodeToHandle : list) {
            String id = parentId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
            parseSelectKeyNode(id, nodeToHandle, parameterTypeClass, languageDriver);
        }
    }

    /**
     * <selectKey keyProperty="id" order="AFTER" resultType="long">
     * SELECT LAST_INSERT_ID()
     * </selectKey>
     */
    private void parseSelectKeyNode(String id, Element nodeToHandle, Class<?> parameterTypeClass, LanguageDriver langDriver) {
        String resultType = nodeToHandle.attributeValue("resultType");
        Class<?> resultTypeClass = resolveClass(resultType);
        boolean executeBefore = "BEFORE".equals(nodeToHandle.attributeValue("order", "AFTER"));
        String keyProperty = nodeToHandle.attributeValue("keyProperty");

        // default
        String resultMap = null;
        boolean flushCache = false;
        boolean useCache = false;
        KeyGenerator keyGenerator = new NoKeyGenerator();

        // 解析成SqlSource，DynamicSqlSource/RawSqlSource
        SqlSource sqlSource = langDriver.createSqlSource(configuration, nodeToHandle, parameterTypeClass);
        SqlCommandType sqlCommandType = SqlCommandType.SELECT;

        // 调用助手类
        builderAssistant.addMappedStatement(id,
                sqlSource,
                sqlCommandType,
                parameterTypeClass,
                resultTypeClass,
                resultMap,
                flushCache,
                useCache,
                keyGenerator,
                keyProperty,
                langDriver);

        // 给id加上namespace前缀
        id = builderAssistant.applyCurrentNamespace(id, false);

        // 存放键值生成器配置
        MappedStatement keyStatement = configuration.getMappedStatement(id);
        configuration.addKeyGenerator(id, new SelectKeyGenerator(keyStatement, executeBefore));
    }

    private KeyGenerator getKeyGenerator(String id, Class<?> parameterTypeClass, LanguageDriver langDriver, SqlCommandType sqlCommandType) {
        KeyGenerator keyGenerator = null;
        String keyStatementId = id + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        keyStatementId = builderAssistant.applyCurrentNamespace(keyStatementId, true);

        if (configuration.hasKeyGenerator(keyStatementId)) {
            keyGenerator = configuration.getKeyGenerator(keyStatementId);
        } else {
            keyGenerator = configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType) ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
        }
        return keyGenerator;
    }


}

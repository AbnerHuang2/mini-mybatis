package org.skitii.ibatis.builder;

import org.skitii.ibatis.cache.Cache;
import org.skitii.ibatis.cache.decorators.FifoCache;
import org.skitii.ibatis.cache.impl.PerpetualCache;
import org.skitii.ibatis.executor.kengen.KeyGenerator;
import org.skitii.ibatis.mapping.*;
import org.skitii.ibatis.reflection.MetaClass;
import org.skitii.ibatis.scripting.LanguageDriver;
import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.type.TypeHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MapperBuilderAssistant extends BaseBuilder {
    private String currentNamespace;
    private String resource;

    private Cache currentCache;

    public MapperBuilderAssistant(Configuration configuration, String resource) {
        super(configuration);
        this.resource = resource;
    }

    public MapperBuilderAssistant(Configuration configuration, String resource, String currentNamespace) {
        super(configuration);
        this.resource = resource;
        this.currentNamespace = currentNamespace;
    }

    public String getCurrentNamespace() {
        return currentNamespace;
    }

    public void setCurrentNamespace(String currentNamespace) {
        this.currentNamespace = currentNamespace;
    }

    public String applyCurrentNamespace(String base, boolean isReference) {
        if (base == null) {
            return null;
        }
        if (isReference) {
            if (base.contains(".")) return base;
        }
        return currentNamespace + "." + base;
    }

    /**
     * 添加语句，resultMap的id必须包含类引用
     */
    public MappedStatement addMappedStatement(
            String id,
            SqlSource sqlSource,
            SqlCommandType sqlCommandType,
            Class<?> parameterType,
            Class<?> resultType,
            String resultMap,
            boolean flushCache,
            boolean useCache,
            KeyGenerator keyGenerator,
            String keyProperty,
            LanguageDriver lang
    ) {
        id = applyCurrentNamespace(id, false);
        //是否是select语句
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;

        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlCommandType, sqlSource, resultType);
        statementBuilder.keyGenerator(keyGenerator);
        statementBuilder.keyProperty(keyProperty);

        setStatementResultMap(resultMap, resultType, statementBuilder);
        setStatementCache(isSelect, flushCache, useCache, currentCache, statementBuilder);

        MappedStatement statement = statementBuilder.build();
        configuration.addMappedStatement(statement);
        return statement;
    }


    private void setStatementResultMap(String resultMap, Class<?> resultType, MappedStatement.Builder statementBuilder) {
        resultMap = applyCurrentNamespace(resultMap, true);
        List<ResultMap> resultMaps = new ArrayList<>();
        if (resultMap != null) {
            // 暂无Map结果映射配置
            String[] resultMapNames = resultMap.split(",");
            for (String resultMapName : resultMapNames) {
                resultMaps.add(configuration.getResultMap(resultMapName.trim()));
            }
        } else if (resultType != null) {
            ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(configuration, statementBuilder.getId() + "-Inline",
                    resultType, new ArrayList<>());
            resultMaps.add(inlineResultMapBuilder.build());
        }
        statementBuilder.resultMaps(resultMaps);
    }

    /**
     * 添加结果映射， id不能包含类引用
     */
    public ResultMap addResultMap(String id, Class<?> type, List<ResultMapping> resultMappings) {
        // 补全ID全路径
        id = applyCurrentNamespace(id, false);
        ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(
                configuration,
                id,
                type,
                resultMappings);

        ResultMap resultMap = inlineResultMapBuilder.build();
        configuration.addResultMap(resultMap);
        return resultMap;
    }

    public ResultMapping buildResultMapping(
            Class<?> resultType,
            String property,
            String column,
            List<ResultFlag> flags) {

        Class<?> javaTypeClass = resolveResultJavaType(resultType, property, null);
        TypeHandler<?> typeHandlerInstance = resolveTypeHandler(javaTypeClass, null);

        ResultMapping.Builder builder = new ResultMapping.Builder(configuration, property, column, javaTypeClass);
        builder.typeHandler(typeHandlerInstance);
        builder.flags(flags);

        return builder.build();
    }

    private Class<?> resolveResultJavaType(Class<?> resultType, String property, Class<?> javaType) {
        if (javaType == null && property != null) {
            try {
                MetaClass metaResultType = MetaClass.forClass(resultType);
                javaType = metaResultType.getSetterType(property);
            } catch (Exception ignore) {
            }
        }
        if (javaType == null) {
            javaType = Object.class;
        }
        return javaType;
    }

    private void setStatementCache(
            boolean isSelect,
            boolean flushCache,
            boolean useCache,
            Cache cache,
            MappedStatement.Builder statementBuilder) {
        flushCache = valueOrDefault(flushCache, !isSelect);
        useCache = valueOrDefault(useCache, isSelect);
        statementBuilder.flushCacheRequired(flushCache);
        statementBuilder.useCache(useCache);
        statementBuilder.cache(cache);
    }

    public Cache useNewCache(Class<? extends Cache> typeClass,
                             Class<? extends Cache> evictionClass,
                             Long flushInterval,
                             Integer size,
                             boolean readWrite,
                             boolean blocking,
                             Properties props) {
        // 判断为null，则用默认值
        typeClass = valueOrDefault(typeClass, PerpetualCache.class);
        evictionClass = valueOrDefault(evictionClass, FifoCache.class);

        // 建造者模式构建 Cache [currentNamespace=cn.bugstack.mybatis.test.dao.IActivityDao]
        Cache cache = new CacheBuilder(currentNamespace)
                .implementation(typeClass)
                .addDecorator(evictionClass)
                .clearInterval(flushInterval)
                .size(size)
                .readWrite(readWrite)
                .blocking(blocking)
                .properties(props)
                .build();

        // 添加缓存
        configuration.addCache(cache);
        currentCache = cache;
        return cache;
    }

    private <T> T valueOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }


}

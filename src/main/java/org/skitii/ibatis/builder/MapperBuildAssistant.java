package org.skitii.ibatis.builder;

import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.mapping.ResultMap;
import org.skitii.ibatis.mapping.SqlCommandType;
import org.skitii.ibatis.mapping.SqlSource;
import org.skitii.ibatis.scripting.LanguageDriver;
import org.skitii.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;

public class MapperBuildAssistant extends BaseBuilder {
    private String currentNamespace;
    private String resource;

    public MapperBuildAssistant(Configuration configuration, String resource) {
        super(configuration);
        this.resource = resource;
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

    public MappedStatement addMappedStatement(
            String id,
            SqlSource sqlSource,
            SqlCommandType sqlCommandType,
            Class<?> parameterType,
            Class<?> resultType,
            String resultMap,
            LanguageDriver lang
    ) {
        id = applyCurrentNamespace(id, false);
        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlCommandType, sqlSource, resultType);
        setStatementResultMap(resultMap, resultType, statementBuilder);
        MappedStatement statement = statementBuilder.build();
        configuration.addMappedStatement(statement);
        return statement;
    }

    private void setStatementResultMap(String resultMap, Class<?> resultType, MappedStatement.Builder statementBuilder) {
        resultMap = applyCurrentNamespace(resultMap, true);
        List<ResultMap> resultMaps = new ArrayList<>();
        if (resultMap != null) {
            // TODO 暂无Map结果映射配置
        } else if (resultType != null) {
            ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(configuration, statementBuilder.getId() + "-Inline",
                    resultType, new ArrayList<>());
            resultMaps.add(inlineResultMapBuilder.build());
        }
        statementBuilder.resultMaps(resultMaps);
    }


}

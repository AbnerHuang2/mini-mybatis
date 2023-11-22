package org.skitii.ibatis.builder;

import org.skitii.ibatis.mapping.ResultMap;
import org.skitii.ibatis.mapping.ResultMapping;

import java.util.List;

/**
 * @author skitii
 * @since 2023/11/21
 **/
public class ResultMapResolver {
    private final MapperBuilderAssistant assistant;
    private String id;
    private Class<?> type;
    private List<ResultMapping> resultMappings;

    public ResultMapResolver(MapperBuilderAssistant assistant, String id, Class<?> type, List<ResultMapping> resultMappings) {
        this.assistant = assistant;
        this.id = id;
        this.type = type;
        this.resultMappings = resultMappings;
    }

    public ResultMap resolve() {
        return assistant.addResultMap(this.id, this.type, this.resultMappings);
    }
}

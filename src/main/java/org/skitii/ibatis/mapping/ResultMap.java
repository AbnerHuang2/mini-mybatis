package org.skitii.ibatis.mapping;

import lombok.Data;
import org.skitii.ibatis.session.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ResultMap {
    private String id;
    private Class<?> type;
    private List<ResultMapping> resultMappings;
    private Set<String> mappedColumns;

    public static class Builder {
        private ResultMap resultMap = new ResultMap();

        public Builder(Configuration configuration, String id, Class<?> type, List<ResultMapping> resultMappings) {
            resultMap.id = id;
            resultMap.type = type;
            resultMap.resultMappings = resultMappings;
        }

        public ResultMap build() {
            resultMap.mappedColumns = new HashSet<>();
            return resultMap;
        }

    }

}

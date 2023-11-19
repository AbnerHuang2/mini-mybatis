package org.skitii.ibatis.mapping;

import lombok.Data;
import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.type.JdbcType;
import org.skitii.ibatis.type.TypeHandler;

@Data
public class ResultMapping {
    private Configuration configuration;
    private String property;
    private String column;
    private Class<?> javaType;
    private JdbcType jdbcType;
    private TypeHandler<?> typeHandler;


    public static class Builder {
        private ResultMapping resultMapping = new ResultMapping();


    }

}

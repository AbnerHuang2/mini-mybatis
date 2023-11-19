package org.skitii.ibatis.scripting.defaults;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.skitii.ibatis.executor.parameter.ParameterHandler;
import org.skitii.ibatis.mapping.BoundSql;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.mapping.ParameterMapping;
import org.skitii.ibatis.reflection.MetaObject;
import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.type.JdbcType;
import org.skitii.ibatis.type.TypeHandler;
import org.skitii.ibatis.type.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author skitii
 * @since 2023/11/17
 **/
@Slf4j
public class DefaultParameterHandler implements ParameterHandler {
    private final TypeHandlerRegistry typeHandlerRegistry;

    private final MappedStatement mappedStatement;
    private final Object parameterObject;
    private BoundSql boundSql;
    private Configuration configuration;

    public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        this.mappedStatement = mappedStatement;
        this.configuration = mappedStatement.getConfiguration();
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
    }

    @Override
    public Object getParameterObject() {
        return parameterObject;
    }

    @Override
    public void setParameters(PreparedStatement ps) throws SQLException {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (null == parameterMappings) {
            return;
        }
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            String propertyName = parameterMapping.getProperty();
            Object value;
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                value = parameterObject;
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);

                value = metaObject.getValue(propertyName);
            }
            // 设置参数
            log.info("根据每个ParameterMapping中的TypeHandler设置对应的参数信息 value：{}", JSON.toJSONString(value));

            JdbcType jdbcType = parameterMapping.getJdbcType();
            TypeHandler typeHandler = parameterMapping.getTypeHandler();
            typeHandler.setParameter(ps, i + 1, value, jdbcType);
        }


    }
}

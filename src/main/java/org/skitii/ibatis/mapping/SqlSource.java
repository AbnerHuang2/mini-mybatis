package org.skitii.ibatis.mapping;

/**
 * @author skitii
 * @since 2023/11/16
 **/
public interface SqlSource {

    BoundSql getBoundSql(Object parameterObject);
}

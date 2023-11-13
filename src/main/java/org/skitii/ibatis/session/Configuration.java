package org.skitii.ibatis.session;

import lombok.Data;
import org.skitii.ibatis.binding.MapperRegistry;
import org.skitii.ibatis.datasource.druid.DruidDataSourceFactory;
import org.skitii.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.skitii.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.skitii.ibatis.mapping.Environment;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.skitii.ibatis.type.TypeAliasRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author skitii
 * @since 2023/11/07
 **/
@Data
public class Configuration {

    //mapper注册器
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    //环境
    protected Environment environment;

    Map<String, MappedStatement> mappedStatements = new HashMap<>();

    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);

        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
    }

    // 类型别名注册机
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public void addMapper(Class<?> type) {
        mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    public Environment getEnvironment() {
        return environment;
    }

}

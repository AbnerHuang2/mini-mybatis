package org.skitii.ibatis.session;

import lombok.Data;
import org.skitii.ibatis.binding.MapperRegistry;
import org.skitii.ibatis.datasource.druid.DruidDataSourceFactory;
import org.skitii.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.skitii.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.skitii.ibatis.executor.Executor;
import org.skitii.ibatis.executor.SimpleExecutor;
import org.skitii.ibatis.executor.resultset.DefaultResultSetHandler;
import org.skitii.ibatis.executor.resultset.ResultSetHandler;
import org.skitii.ibatis.executor.statement.PreparedStatementHandler;
import org.skitii.ibatis.executor.statement.StatementHandler;
import org.skitii.ibatis.mapping.BoundSql;
import org.skitii.ibatis.mapping.Environment;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.reflection.MetaObject;
import org.skitii.ibatis.reflection.factory.DefaultObjectFactory;
import org.skitii.ibatis.reflection.factory.ObjectFactory;
import org.skitii.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.skitii.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.skitii.ibatis.scripting.LanguageDriverRegistry;
import org.skitii.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.skitii.ibatis.transaction.Transaction;
import org.skitii.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.skitii.ibatis.type.TypeAliasRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

        languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
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

    protected final Set<String> loadedResources = new HashSet<>();

    // 对象工厂和对象包装器工厂
    protected ObjectFactory objectFactory = new DefaultObjectFactory();
    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

    protected String databaseId;

    protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();

    /**
     * 创建结果集处理器
     */
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        return new DefaultResultSetHandler(boundSql, mappedStatement);
    }

    /**
     * 生产执行器
     */
    public Executor newExecutor(Transaction transaction) {
        return new SimpleExecutor(this, transaction);
    }

    /**
     * 创建语句处理器
     */
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        return new PreparedStatementHandler(executor, mappedStatement, parameter, resultHandler);
    }

    public boolean isResourceLoaded(String resource) {
        return loadedResources.contains(resource);
    }

    public void addLoadedResource(String resource) {
        loadedResources.add(resource);
    }

    // 创建元对象
    public MetaObject newMetaObject(Object object) {
        return MetaObject.forObject(object, objectFactory, objectWrapperFactory);
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public LanguageDriverRegistry getLanguageRegistry() {
        return languageRegistry;
    }

}

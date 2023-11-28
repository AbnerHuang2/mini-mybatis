package org.skitii.ibatis.executor;

import lombok.extern.slf4j.Slf4j;
import org.skitii.ibatis.cache.CacheKey;
import org.skitii.ibatis.cache.impl.PerpetualCache;
import org.skitii.ibatis.mapping.BoundSql;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.mapping.ParameterMapping;
import org.skitii.ibatis.reflection.MetaObject;
import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.session.ResultHandler;
import org.skitii.ibatis.session.RowBounds;
import org.skitii.ibatis.transaction.Transaction;
import org.skitii.ibatis.type.TypeHandlerRegistry;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author skitii
 * @since 2023/11/14
 **/
@Slf4j
public abstract class BaseExecutor implements Executor {
    protected Configuration configuration;
    protected Transaction transaction;
    protected Executor wrapper;
    private boolean closed;

    private PerpetualCache localCache;
    protected int queryStack = 0;

    protected BaseExecutor(Configuration configuration, Transaction transaction) {
        this.configuration = configuration;
        this.transaction = transaction;
        this.wrapper = this;
        this.localCache = new PerpetualCache("LocalCache");
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        BoundSql boundSql = ms.getSqlSource().getBoundSql(parameter);
        CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
        return query(ms, parameter, rowBounds, resultHandler, boundSql, key);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds,
                             ResultHandler resultHandler, BoundSql boundSql, CacheKey key) throws SQLException {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        // 清理局部缓存，查询堆栈为0则清理。queryStack 避免递归调用清理
        if (queryStack == 0 && ms.isFlushCacheRequired()) {
            clearLocalCache();
        }
        List<E> list;
        try {
            queryStack++;
            // 从一级缓存中获取
            list = resultHandler == null ? (List<E>) localCache.getObject(key) : null;
            if (list == null) {
                list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, boundSql, key);
            }
        } finally {
            queryStack--;
        }

        return list;
    }

    private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, RowBounds rowBounds,
                                          ResultHandler resultHandler, BoundSql boundSql, CacheKey key) throws SQLException {
        List<E> list;
        localCache.putObject(key, ExecutionPlaceholder.EXECUTION_PLACEHOLDER);
        try {
            list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
        } finally {
            // 清除占位符
            localCache.removeObject(key);
        }
        localCache.putObject(key, list);
        return list;
    }

    protected abstract <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql);

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        clearLocalCache();
        return doUpdate(ms, parameter);
    }

    protected abstract int doUpdate(MappedStatement ms, Object parameter) throws SQLException;

    @Override
    public Transaction getTransaction() {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        return transaction;
    }

    @Override
    public void commit(boolean required) {
        if (closed) {
            throw new RuntimeException("Cannot commit, transaction is already closed");
        }
        clearLocalCache();
        if (required) {
            try {
                transaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void rollback(boolean required) {
        if (!closed) {
            clearLocalCache();
            if (required) {
                try {
                    transaction.rollback();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void close(boolean forceRollback) {
        try {
            try{
                rollback(forceRollback);
            } finally {
                transaction.close();
            }
        } catch (SQLException e) {
            log.warn("Unexpected exception on closing transaction.  Cause: " + e);
        } finally {
            transaction = null;
            closed = true;
        }
    }

    protected void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException ignore) {
            }
        }
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql){
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        CacheKey cacheKey = new CacheKey();
        cacheKey.update(ms.getId());
        cacheKey.update(rowBounds.getOffset());
        cacheKey.update(rowBounds.getLimit());
        cacheKey.update(boundSql.getSql());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        TypeHandlerRegistry typeHandlerRegistry = ms.getConfiguration().getTypeHandlerRegistry();
        for (ParameterMapping parameterMapping : parameterMappings) {
            Object value;
            String propertyName = parameterMapping.getProperty();
            if (boundSql.hasAdditionalParameter(propertyName)) {
                value = boundSql.getAdditionalParameter(propertyName);
            } else if (parameterObject == null) {
                value = null;
            } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                value = parameterObject;
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                value = metaObject.getValue(propertyName);
            }
            cacheKey.update(value);
        }
        if (configuration.getEnvironment() != null) {
            cacheKey.update(configuration.getEnvironment().getId());
        }
        return cacheKey;
    }

    @Override
    public void clearLocalCache() {
        if (!closed) {
            localCache.clear();
        }
    }

    @Override
    public void setExecutorWrapper(Executor executor) {
        this.wrapper = wrapper;
    }

}

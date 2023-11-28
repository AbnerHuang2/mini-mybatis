package org.skitii.ibatis.executor;

import lombok.extern.slf4j.Slf4j;
import org.skitii.ibatis.cache.Cache;
import org.skitii.ibatis.cache.CacheKey;
import org.skitii.ibatis.cache.TransactionalCacheManager;
import org.skitii.ibatis.mapping.BoundSql;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.session.ResultHandler;
import org.skitii.ibatis.session.RowBounds;
import org.skitii.ibatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * @author skitii
 * @since 2023/11/28
 **/
@Slf4j
public class CachingExecutor implements Executor{
    private Executor delegate;

    private TransactionalCacheManager tcm = new TransactionalCacheManager();

    public CachingExecutor(Executor delegate) {
        this.delegate = delegate;
        delegate.setExecutorWrapper(this);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        BoundSql boundSql = ms.getSqlSource().getBoundSql(parameter);
        CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
        return query(ms, parameter, rowBounds, resultHandler, boundSql, key);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql, CacheKey key) throws SQLException {
        Cache cache = ms.getCache();
        if (cache != null ) {
            flushCacheIfRequired(ms);
            if (ms.isUseCache() && resultHandler == null) {
                List<E> list = (List<E>) tcm.getObject(cache, key);
                if (list == null) {
                    list = delegate.<E>query(ms, parameter, rowBounds, resultHandler, boundSql, key);
                    // cache：缓存队列实现类，FIFO
                    // key：哈希值 [mappedStatementId + offset + limit + SQL + queryParams + environment]
                    // list：查询的数据
                    tcm.putObject(cache, key, list);
                }
                // 打印调试日志，记录二级缓存获取数据
                if (log.isDebugEnabled() && cache.getSize() > 0) {
                    log.debug("二级缓存：{}", list);
                }
                return list;
            }
        }
        return delegate.<E>query(ms, parameter, rowBounds, resultHandler, boundSql, key);
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        return delegate.update(ms, parameter);
    }

    @Override
    public Transaction getTransaction() {
        return delegate.getTransaction();
    }

    @Override
    public void commit(boolean required) throws SQLException {
        delegate.commit(required);
        tcm.commit();
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        try {
            delegate.rollback(required);
        } finally {
            if (required) {
                tcm.rollback();
            }
        }
    }

    @Override
    public void close(boolean forceRollback) {
        try {
            if (forceRollback) {
                tcm.rollback();
            } else {
                tcm.commit();
            }
        } finally {
            delegate.close(forceRollback);
        }
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        return delegate.createCacheKey(ms, parameterObject, rowBounds, boundSql);
    }

    @Override
    public void clearLocalCache() {
        delegate.clearLocalCache();
    }

    @Override
    public void setExecutorWrapper(Executor executor) {
        throw new UnsupportedOperationException("This method should not be called");
    }

    private void flushCacheIfRequired(MappedStatement ms) {
        Cache cache = ms.getCache();
        if (cache != null && ms.isFlushCacheRequired()) {
            tcm.clear(cache);
        }
    }

}

package org.skitii.ibatis.executor.kengen;

import org.skitii.ibatis.executor.Executor;
import org.skitii.ibatis.mapping.MappedStatement;

import java.sql.Statement;

/**
 * @author skitii
 * @since 2023/11/22
 **/
public interface KeyGenerator {
    /**
     * 针对Sequence主键而言，在执行insert sql前必须指定一个主键值给要插入的记录，
     * 如Oracle、DB2，KeyGenerator提供了processBefore()方法。
     */
    void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter);

    /**
     * 针对自增主键的表，在插入时不需要主键，而是在插入过程自动获取一个自增的主键，
     * 比如MySQL、PostgreSQL，KeyGenerator提供了processAfter()方法
     */
    void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter);
}

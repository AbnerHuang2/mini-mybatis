package org.skitii.ibatis.executor.kengen;



import org.skitii.ibatis.executor.Executor;
import org.skitii.ibatis.mapping.MappedStatement;

import java.sql.Statement;

/**
 * @author skitii 
 * @description 不用键值生成器
 */
public class NoKeyGenerator implements KeyGenerator {

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        // Do Nothing
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        // Do Nothing
    }

}

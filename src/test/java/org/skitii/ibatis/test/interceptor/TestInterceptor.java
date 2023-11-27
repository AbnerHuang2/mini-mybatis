package org.skitii.ibatis.test.interceptor;

import org.skitii.ibatis.executor.statement.StatementHandler;
import org.skitii.ibatis.plugin.Interceptor;
import org.skitii.ibatis.plugin.Intercepts;
import org.skitii.ibatis.plugin.Invocation;
import org.skitii.ibatis.plugin.Signature;

import java.sql.Connection;
import java.util.Properties;

/**
 * @author skitii
 * @since 2023/11/27
 **/
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class}))
public class TestInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取StatementHandler
        StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
        // 获取SQL信息
        String sql = statementHandler.getBoundSql().getSql();
        System.out.println("拦截SQL：" + sql);
        // 放行
        return invocation.proceed();
    }

    @Override
    public void setProperties(Properties properties) {
        properties.forEach((k, v) -> System.out.println("参数输出：" + k + " = " + v));
    }

}

package org.skitii.ibatis.plugin;

import java.util.Properties;

/**
 * @author skitii
 * @since 2023/11/27
 **/
public interface Interceptor {
    /**
     * 拦截，使用方实现
     */
    Object intercept(Invocation invocation) throws Throwable;

    /**
     * 代理
     */
    default Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 设置属性
     */
    default void setProperties(Properties properties) {
        // NOP
    }
}

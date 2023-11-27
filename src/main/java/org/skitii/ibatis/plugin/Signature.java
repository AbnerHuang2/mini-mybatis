package org.skitii.ibatis.plugin;

/**
 * @author skitii
 * @since 2023/11/27
 **/
public @interface Signature {
    /**
     * 被拦截类
     */
    Class<?> type();

    /**
     * 被拦截类的方法
     */
    String method();

    /**
     * 被拦截类的方法的参数
     */
    Class<?>[] args();
}

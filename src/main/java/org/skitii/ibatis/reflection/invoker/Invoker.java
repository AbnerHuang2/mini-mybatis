package org.skitii.ibatis.reflection.invoker;

/**
 * @author skitii
 * @since 2023/11/15
 **/
public interface Invoker {

    Object invoke(Object target, Object[] args) throws Exception;

    Class<?> getType();
}

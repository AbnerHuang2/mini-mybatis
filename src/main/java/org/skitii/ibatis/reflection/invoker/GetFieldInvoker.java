package org.skitii.ibatis.reflection.invoker;

import java.lang.reflect.Field;

/**
 * @author skitii
 * @since 2023/11/15
 **/
public class GetFieldInvoker implements Invoker{
    private  Field field;

    public GetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        return field.get(target);
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}

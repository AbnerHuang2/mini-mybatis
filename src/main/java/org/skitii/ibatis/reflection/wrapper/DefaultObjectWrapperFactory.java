package org.skitii.ibatis.reflection.wrapper;

import org.skitii.ibatis.reflection.MetaObject;

/**
 * @author skitii 
 * @description 默认对象包装工厂
 */
public class DefaultObjectWrapperFactory implements ObjectWrapperFactory {

    @Override
    public boolean hasWrapperFor(Object object) {
        return false;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        throw new RuntimeException("The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
    }

}

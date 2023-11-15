package org.skitii.ibatis.reflection;


import org.skitii.ibatis.reflection.factory.DefaultObjectFactory;
import org.skitii.ibatis.reflection.factory.ObjectFactory;
import org.skitii.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.skitii.ibatis.reflection.wrapper.ObjectWrapperFactory;

/**
 * @author 小傅哥，微信：fustack
 * @description 一些系统级别的元对象
 * @github https://github.com/fuzhengwei
 * @Copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class SystemMetaObject {

    public static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    public static final MetaObject NULL_META_OBJECT = MetaObject.forObject(NullObject.class, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);

    private SystemMetaObject() {
        // Prevent Instantiation of Static Class
    }

    /**
     * 空对象
     */
    private static class NullObject {
    }

    public static MetaObject forObject(Object object) {
        return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);
    }

}

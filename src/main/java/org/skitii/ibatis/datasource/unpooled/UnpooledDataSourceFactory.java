package org.skitii.ibatis.datasource.unpooled;

import org.skitii.ibatis.datasource.DataSourceFactory;
import org.skitii.ibatis.reflection.MetaObject;
import org.skitii.ibatis.reflection.SystemMetaObject;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author skitii
 * @since 2023/11/13
 **/
public class UnpooledDataSourceFactory implements DataSourceFactory {

    protected DataSource dataSource;

    public UnpooledDataSourceFactory() {
        this.dataSource = new UnpooledDataSource();
    }
    @Override
    public void setProperties(Properties props) {
        // 通过反射给dataSource对象的属性赋值【可以动态扩展】
        MetaObject metaObject = SystemMetaObject.forObject(dataSource);
        for (Object key : props.keySet()) {
            String propertyName = (String) key;
            if (metaObject.hasSetter(propertyName)) {
                String value = (String) props.get(propertyName);
                Object convertedValue = convertValue(metaObject, propertyName, value);
                metaObject.setValue(propertyName, convertedValue);
            }
        }
    }

    private Object convertValue(MetaObject metaObject, String propertyName, String value) {
        Object convertedValue = value;
        Class<?> targetType = metaObject.getSetterType(propertyName);
        if (targetType == Integer.class || targetType == int.class) {
            convertedValue = Integer.valueOf(value);
        } else if (targetType == Long.class || targetType == long.class) {
            convertedValue = Long.valueOf(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            convertedValue = Boolean.valueOf(value);
        }
        return convertedValue;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}

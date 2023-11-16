package org.skitii.ibatis.executor.resultset;

import org.skitii.ibatis.mapping.BoundSql;
import org.skitii.ibatis.mapping.MappedStatement;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author skitii
 * @since 2023/11/14
 **/
public class DefaultResultSetHandler implements ResultSetHandler{
    private final BoundSql boundSql;
    private final MappedStatement mappedStatement;

    public DefaultResultSetHandler(BoundSql boundSql, MappedStatement mappedStatement) {
        this.boundSql = boundSql;
        this.mappedStatement = mappedStatement;
    }

    @Override
    public <E> List<E> handleResultSets(Statement stmt) throws SQLException {
        ResultSet resultSet = stmt.getResultSet();
        return resultSet2Obj(resultSet, mappedStatement.getResultType());
    }

    private <T> List<T> resultSet2Obj(ResultSet resultSet, Class<?> clazz) {
        List<T> list = new ArrayList<>();
        try{
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while(resultSet.next()) {
                // 处理基本类型
                if (clazz.getPackage().getName().startsWith("java.lang")){
                    list.add((T) resultSet.getObject(1));
                    continue;
                }
                T obj = (T) clazz.newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    String columnClassName = metaData.getColumnClassName(i);
                    Object value = null;
                    if (columnClassName.contains("BigInteger")) {
                        value = resultSet.getLong(i);
                    } else {
                        value = resultSet.getObject(i);
                    }
                    String columnName = metaData.getColumnName(i);
                    String setMethodName = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                    Method method = clazz.getMethod(setMethodName, value.getClass());
                    method.invoke(obj, value);
                }
                list.add(obj);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}

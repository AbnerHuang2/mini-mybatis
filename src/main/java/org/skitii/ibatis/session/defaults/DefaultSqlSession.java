package org.skitii.ibatis.session.defaults;

import org.skitii.ibatis.session.SqlSession;
import org.skitii.ibatis.mapping.MappedStatement;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author skitii
 * @since 2023/11/07
 **/
public class DefaultSqlSession implements SqlSession {
    private Connection connection;
    private Map<String, MappedStatement> mapperElement;

    public DefaultSqlSession(Connection connection, Map<String, MappedStatement> mapperElement) {
        this.connection = connection;
        this.mapperElement = mapperElement;
    }

    @Override
    public <T> T selectOne(String statement) {
        return selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        try {
            MappedStatement mappedStatement = mapperElement.get(statement);
            PreparedStatement preparedStatement = connection.prepareStatement(mappedStatement.getSql());
            buildParameter(preparedStatement, parameter, mappedStatement.getParameter());
            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> objects = resultSet2Obj(resultSet, Class.forName(mappedStatement.getResultType()));
            return objects.isEmpty() ?  null : objects.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T> List<T> resultSet2Obj(ResultSet resultSet, Class<?> clazz) {
        List<T> list = new ArrayList<>();
        try{
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while(resultSet.next()) {
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

    private void buildParameter(PreparedStatement preparedStatement, Object parameter, Map<Integer, String> parameterMap) throws SQLException, IllegalAccessException {
        if (null == parameter) return;
        int size = parameterMap.size();
        if (parameter instanceof Integer) {
            for (int i = 1; i <= size; i++) {
                preparedStatement.setInt(i, Integer.parseInt(parameter.toString()));
            }
            return;
        }

        if (parameter instanceof Long) {
            for (int i = 1; i <= size; i++) {
                preparedStatement.setLong(i, Long.parseLong(parameter.toString()));
            }
            return;
        }

        if (parameter instanceof String) {
            for (int i = 1; i <= size; i++) {
                preparedStatement.setString(i, parameter.toString());
            }
            return;
        }

        Map<String, Object> fieldMap = new HashMap<>();
        // 对象参数
        Field[] declaredFields = parameter.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            String name = field.getName();
            field.setAccessible(true);
            Object obj = field.get(parameter);
            field.setAccessible(false);
            fieldMap.put(name, obj);
        }

        for (int i = 1; i <= size; i++) {
            String parameterDefine = parameterMap.get(i);
            Object obj = fieldMap.get(parameterDefine);

            if (obj instanceof Short) {
                preparedStatement.setShort(i, Short.parseShort(obj.toString()));
                continue;
            }

            if (obj instanceof Integer) {
                preparedStatement.setInt(i, Integer.parseInt(obj.toString()));
                continue;
            }

            if (obj instanceof Long) {
                preparedStatement.setLong(i, Long.parseLong(obj.toString()));
                continue;
            }

            if (obj instanceof String) {
                preparedStatement.setString(i, obj.toString());
                continue;
            }

            if (obj instanceof java.util.Date) {
                preparedStatement.setDate(i, (java.sql.Date) obj);
            }

        }
    }

    @Override
    public <T> List<T> selectList(String statement) {
        return null;
    }

    @Override
    public <T> List<T> selectList(String statement, Object parameter) {
        return null;
    }

    @Override
    public void close() {
        if (null == connection) return;
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package org.skitii.ibatis.session.defaults;

import org.skitii.ibatis.mapping.Environment;
import org.skitii.ibatis.mapping.MappedStatement;
import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author skitii
 * @since 2023/11/07
 **/
public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;

    Connection connection;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public <T> T selectOne(String statement) {
        return selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object[] parameter) {
        try {
            MappedStatement mappedStatement = configuration.getMappedStatement(statement);
            Environment environment = configuration.getEnvironment();
            connection = environment.getDataSource().getConnection();
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

    private void buildParameter(PreparedStatement preparedStatement, Object[] parameter, Map<Integer, String> parameterMap) throws SQLException, IllegalAccessException {
        if (null == parameter) return;
        int size = parameterMap.size();
        for (int i = 1; i <= size; i++) {
            Object paramValue = parameter[i-1];
            if (paramValue instanceof Integer) {
                preparedStatement.setInt(i, Integer.parseInt(paramValue.toString()));
            }

            if (paramValue instanceof Long) {
                preparedStatement.setLong(i, Long.parseLong(paramValue.toString()));
            }
            if (paramValue instanceof String) {
                preparedStatement.setString(i, paramValue.toString());
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

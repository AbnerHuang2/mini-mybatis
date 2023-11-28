package org.skitii.ibatis.builder.annotation;

import org.skitii.ibatis.annotations.Delete;
import org.skitii.ibatis.annotations.Insert;
import org.skitii.ibatis.annotations.Select;
import org.skitii.ibatis.annotations.Update;
import org.skitii.ibatis.binding.MapperMethod;
import org.skitii.ibatis.builder.MapperBuilderAssistant;
import org.skitii.ibatis.executor.kengen.Jdbc3KeyGenerator;
import org.skitii.ibatis.executor.kengen.KeyGenerator;
import org.skitii.ibatis.executor.kengen.NoKeyGenerator;
import org.skitii.ibatis.mapping.ResultMap;
import org.skitii.ibatis.mapping.SqlCommandType;
import org.skitii.ibatis.mapping.SqlSource;
import org.skitii.ibatis.scripting.LanguageDriver;
import org.skitii.ibatis.session.Configuration;
import org.skitii.ibatis.session.ResultHandler;
import org.skitii.ibatis.session.RowBounds;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author skitii
 * @since 2023/11/20
 */
public class MapperAnnotationBuilder {
    private final Set<Class<? extends Annotation>> sqlAnnotationTypes = new HashSet<>();

    private Configuration configuration;
    private MapperBuilderAssistant assistant;
    private Class<?> type;

    public MapperAnnotationBuilder(Configuration configuration, Class<?> type) {
        String resource = type.getName().replace(".", "/") + ".java (best guess)";
        this.assistant = new MapperBuilderAssistant(configuration, resource);
        this.configuration = configuration;
        this.type = type;
        assistant.setCurrentNamespace(type.getName());

        sqlAnnotationTypes.add(Select.class);
        sqlAnnotationTypes.add(Insert.class);
        sqlAnnotationTypes.add(Update.class);
        sqlAnnotationTypes.add(Delete.class);
    }

    public void parse() {
        String resource = type.toString();
        if (!configuration.isResourceLoaded(resource)) {

            Method[] methods = type.getMethods();
            for (Method method : methods) {
                if (!method.isBridge()) {
                    // 解析语句
                    parseStatement(method);
                }
            }
        }
    }
    /**
     * 解析语句
     */
    private void parseStatement(Method method) {
        Class<?> parameterTypeClass = getParameterType(method);
        LanguageDriver languageDriver = getLanguageDriver(method);
        SqlSource sqlSource = getSqlSourceFromAnnotations(method, parameterTypeClass, languageDriver);

        if (sqlSource != null) {
            final String mappedStatementId = method.getName();
            SqlCommandType sqlCommandType = getSqlCommandType(method);
            boolean isSelect = sqlCommandType == SqlCommandType.SELECT;

            String resultMapId = null;
            if (isSelect) {
                //后续添加MappedStatement需要包括类引用
                resultMapId = parseResultMap(method);
            }

            KeyGenerator keyGenerator;
            String keyProperty = "id";
            if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {
                keyGenerator = configuration.isUseGeneratedKeys() ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
            } else {
                keyGenerator = new NoKeyGenerator();
            }

            // 调用助手类
            assistant.addMappedStatement(
                    mappedStatementId,
                    sqlSource,
                    sqlCommandType,
                    parameterTypeClass,
                    getReturnType(method),
                    resultMapId,
                    false,
                    false,
                    keyGenerator,
                    keyProperty,
                    languageDriver
            );
        }
    }

    /**
     * 重点：DAO 方法的返回类型，如果为 List 则需要获取集合中的对象类型
     */
    private Class<?> getReturnType(Method method) {
        Class<?> returnType = method.getReturnType();
        if (Collection.class.isAssignableFrom(returnType)) {
            Type returnTypeParameter = method.getGenericReturnType();
            if (returnTypeParameter instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) returnTypeParameter).getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    returnTypeParameter = actualTypeArguments[0];
                    if (returnTypeParameter instanceof Class) {
                        returnType = (Class<?>) returnTypeParameter;
                    } else if (returnTypeParameter instanceof ParameterizedType) {
                        // (issue #443) actual type can be a also a parameterized type
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    } else if (returnTypeParameter instanceof GenericArrayType) {
                        Class<?> componentType = (Class<?>) ((GenericArrayType) returnTypeParameter).getGenericComponentType();
                        // (issue #525) support List<byte[]>
                        returnType = Array.newInstance(componentType, 0).getClass();
                    }
                }
            }
        }
        return returnType;
    }

    private String parseResultMap(Method method) {
        // generateResultMapName
        StringBuilder suffix = new StringBuilder();
        for (Class<?> c : method.getParameterTypes()) {
            suffix.append("-");
            suffix.append(c.getSimpleName());
        }
        if (suffix.length() < 1) {
            suffix.append("-void");
        }
        String resultMapId = method.getName() + suffix;

        // 添加 ResultMap
        Class<?> returnType = getReturnType(method);
        // 直接添加resultMap不需要类引用
        ResultMap resultMap = assistant.addResultMap(resultMapId, returnType, new ArrayList<>());
        return resultMap.getId();
    }

    private SqlCommandType getSqlCommandType(Method method) {
        Class<? extends Annotation> type = getSqlAnnotationType(method);
        if (type == null) {
            return SqlCommandType.UNKNOWN;
        }
        return SqlCommandType.valueOf(type.getSimpleName().toUpperCase(Locale.ENGLISH));
    }

    private SqlSource getSqlSourceFromAnnotations(Method method, Class<?> parameterType, LanguageDriver languageDriver) {
        try {
            Class<? extends Annotation> sqlAnnotationType = getSqlAnnotationType(method);
            if (sqlAnnotationType != null) {
                Annotation sqlAnnotation = method.getAnnotation(sqlAnnotationType);
                final String[] strings = (String[]) sqlAnnotation.getClass().getMethod("value").invoke(sqlAnnotation);
                return buildSqlSourceFromStrings(strings, parameterType, languageDriver);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Could not find value method on SQL annotation.  Cause: " + e);
        }
    }

    private SqlSource buildSqlSourceFromStrings(String[] strings, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
        final StringBuilder sql = new StringBuilder();
        for (String fragment : strings) {
            sql.append(fragment);
            sql.append(" ");
        }
        return languageDriver.createSqlSource(configuration, sql.toString(), parameterTypeClass);
    }

    private Class<? extends Annotation> getSqlAnnotationType(Method method) {
        for (Class<? extends Annotation> type : sqlAnnotationTypes) {
            Annotation annotation = method.getAnnotation(type);
            if (annotation != null) return type;
        }
        return null;
    }

    private LanguageDriver getLanguageDriver(Method method) {
        Class<?> langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
        return configuration.getLanguageRegistry().getDriver(langClass);
    }

    private Class<?> getParameterType(Method method) {
        Class<?> parameterType = null;
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> clazz : parameterTypes) {
            if (!RowBounds.class.isAssignableFrom(clazz) && !ResultHandler.class.isAssignableFrom(clazz)) {
                if (parameterType == null) {
                    parameterType = clazz;
                } else {
                    parameterType = MapperMethod.ParamMap.class;
                }
            }
        }
        return parameterType;
    }


}

package org.skitii.ibatis.session;

/**
 * @author skitii
 * @since 2023/11/07
 **/
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration.getConnection(), configuration.getMapperElement());
    }
}

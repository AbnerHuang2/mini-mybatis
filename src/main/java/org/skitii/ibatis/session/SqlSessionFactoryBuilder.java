package org.skitii.ibatis.session;

import org.skitii.ibatis.builder.xml.XMLConfigBuilder;
import org.skitii.ibatis.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * @author skitii
 * @since 2023/11/07
 **/
public class SqlSessionFactoryBuilder {
    public SqlSessionFactory build(Reader reader) {
        // 解析xml到Configuration对象
        Configuration configuration = parseXMl2Configuration(reader);

        // 创建SqlSessionFactory对象
        return new DefaultSqlSessionFactory(configuration);
    }

    private Configuration parseXMl2Configuration(Reader reader) {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        return xmlConfigBuilder.parse();
    }


}

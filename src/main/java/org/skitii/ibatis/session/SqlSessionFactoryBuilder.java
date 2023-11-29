package org.skitii.ibatis.session;

import org.dom4j.Document;
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
        XMLConfigBuilder builder = new XMLConfigBuilder(reader);

        // 创建SqlSessionFactory对象
        return new DefaultSqlSessionFactory(builder.parse());
    }

    public SqlSessionFactory build(Document document) {
        // 解析xml到Configuration对象
        XMLConfigBuilder builder = new XMLConfigBuilder(document);

        // 创建SqlSessionFactory对象
        return new DefaultSqlSessionFactory(builder.parse());
    }

}

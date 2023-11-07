package org.skitii.ibatis.test;

import cn.hutool.core.io.IoUtil;
import org.h2.util.IOUtils;
import org.skitii.ibatis.test.domain.User;
import org.junit.jupiter.api.Test;
import org.skitii.ibatis.io.Resources;
import org.skitii.ibatis.session.SqlSession;
import org.skitii.ibatis.session.SqlSessionFactory;
import org.skitii.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @author skitii
 * @since 2023/11/07
 **/
public class ApiTest {

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    @Test
    public void testDemo() throws IOException {
        //初始化
        String resource = "mybatis-config-datasource.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        //System.out.println(IoUtil.read(reader));

        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(reader);

        //使用
        SqlSession session = sqlSessionFactory.openSession();
        User user = session.selectOne(
                "org.skitii.ibatis.test.dao.UserDao.queryUserInfoById", 1);
        System.out.println(user.getName());
    }
}

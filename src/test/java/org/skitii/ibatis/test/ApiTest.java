package org.skitii.ibatis.test;

import org.junit.jupiter.api.Test;
import org.skitii.ibatis.io.Resources;
import org.skitii.ibatis.session.SqlSession;
import org.skitii.ibatis.session.SqlSessionFactory;
import org.skitii.ibatis.session.SqlSessionFactoryBuilder;
import org.skitii.ibatis.test.dao.UserDao;
import org.skitii.ibatis.test.domain.User;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * @author skitii
 * @since 2023/11/07
 **/
public class ApiTest {


    @Test
    public void testWithReflect() throws IOException {

        //初始化
        String resource = "mybatis-config-datasource.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(reader);
        //使用
        SqlSession session = sqlSessionFactory.openSession();
        UserDao dao = session.getMapper(UserDao.class);
        String name = dao.queryName(1L);
        System.out.println(name);
        User user = dao.queryUserInfoById(1L);
        System.out.println(user.getAge());

        List<User> users = dao.queryUserList();
        System.out.println(users.size());
    }

    @Test
    public void testWithSession() throws IOException{
        //初始化
        String resource = "mybatis-config-datasource.xml";
        Reader reader = Resources.getResourceAsReader(resource);

        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(reader);

        //使用
        SqlSession session = sqlSessionFactory.openSession();
        User user = session.selectOne(
                "org.skitii.ibatis.test.dao.UserDao.queryUserInfoById", new Object[]{1L});
        System.out.println(user.getName());
    }

}

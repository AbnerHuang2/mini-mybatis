package org.skitii.ibatis.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skitii.ibatis.io.Resources;
import org.skitii.ibatis.session.SqlSession;
import org.skitii.ibatis.session.SqlSessionFactory;
import org.skitii.ibatis.session.SqlSessionFactoryBuilder;
import org.skitii.ibatis.session.TransactionIsolationLevel;
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

    private SqlSession sqlSession;

    @BeforeEach
    public void init() throws IOException{
        //初始化
        String resource = "mybatis-config-datasource.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(reader);
        //使用
        sqlSession = sqlSessionFactory.openSession(TransactionIsolationLevel.READ_COMMITTED, true);
    }

    @Test
    public void testWithReflect() throws IOException {


        UserDao dao = sqlSession.getMapper(UserDao.class);
        String name = dao.queryName(1L);
        System.out.println(name);
        User user = dao.queryUserInfoById(1L);
        System.out.println(user.getAge());

        List<User> users = dao.queryUserList();
        System.out.println(users.size());
    }

    @Test
    public void testInsert() {
        UserDao dao = sqlSession.getMapper(UserDao.class);
        User user = new User();
        user.setAge(18);
        user.setName("abner");
        int cnt = dao.insertUser(user);
        System.out.println(cnt);
    }

    @Test
    public void testWithSession() throws IOException{
        User user = sqlSession.selectOne(
                "org.skitii.ibatis.test.dao.UserDao.queryUserInfoById", 1L);
        System.out.println(user.getName());
    }

}

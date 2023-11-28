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
    private SqlSessionFactory sqlSessionFactory;

    @BeforeEach
    public void init() throws IOException{
        //初始化
        String resource = "mybatis-config-datasource.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(reader);
        //使用
        sqlSession = sqlSessionFactory.openSession(TransactionIsolationLevel.READ_COMMITTED, true);
    }

    @Test
    public void testCacheLevel2() {
        UserDao dao = sqlSession.getMapper(UserDao.class);
        User user = dao.queryUserInfoById(1L);
        System.out.println(user.getUserEmail());
        // 关闭sqlSession时触发二级缓存刷新，以便下面的查询能走二级缓存。
        sqlSession.close();
        // 二级缓存
        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        User user2 = sqlSession2.getMapper(UserDao.class).queryUserInfoById(1L);
        System.out.println(user2.getUserEmail());
        sqlSession2.close();
    }

    @Test
    public void testCacheLevel1() {
        UserDao dao = sqlSession.getMapper(UserDao.class);
        User user = dao.queryUserInfoById(1L);
        System.out.println(user.getUserEmail());
        // 一级缓存
        User user1 = dao.queryUserInfoById(1L);
        System.out.println(user1.getUserEmail());
    }

    @Test
    public void testDynamicSql(){
        UserDao dao = sqlSession.getMapper(UserDao.class);
        User userQueryDto = new User();
        userQueryDto.setId(1L)
                .setName("abner");
        List<User> users = dao.dynamicQueryByUserCondition(userQueryDto);
        System.out.println(users.size());
    }

    @Test
    public void testAnnotation(){
        UserDao dao = sqlSession.getMapper(UserDao.class);
        List<User> users = dao.queryByName("abner");
        System.out.println(users.size());
    }

    @Test
    public void testWithReflect() throws IOException {


        UserDao dao = sqlSession.getMapper(UserDao.class);
//        String name = dao.queryName(1L);
//        System.out.println(name);
        User user = dao.queryUserInfoById(1L);
        System.out.println(user.getUserEmail());

//        List<User> users = dao.queryUserList();
//        System.out.println(users.size());
    }

    @Test
    public void testInsert() {
        UserDao dao = sqlSession.getMapper(UserDao.class);
        User user = new User();
        user.setAge(18);
        user.setName("abner");
        int cnt = dao.insertUser(user);
        System.out.println(user.getId());
    }

    @Test
    public void testWithSession() throws IOException{
        User user = sqlSession.selectOne(
                "org.skitii.ibatis.test.dao.UserDao.queryUserInfoById", 1L);
        System.out.println(user.getName());
    }

}

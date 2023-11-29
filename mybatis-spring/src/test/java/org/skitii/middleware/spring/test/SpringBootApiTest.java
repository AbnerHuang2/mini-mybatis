package org.skitii.middleware.spring.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.skitii.middleware.spring.test.dao.UserDao;
import org.skitii.middleware.spring.test.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author skitii
 * @since 2023/11/29
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootApiTest {
    @Resource
    UserDao userDao;

    @Test
    public void testUser(){
        User user = userDao.queryUserInfoById(1L);
        System.out.println(user.getUserEmail());
    }

}

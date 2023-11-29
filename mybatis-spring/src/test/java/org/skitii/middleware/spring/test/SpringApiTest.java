package org.skitii.middleware.spring.test;

import org.skitii.middleware.spring.test.dao.UserDao;
import org.skitii.middleware.spring.test.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * @author skitii
 * @since 2023/11/28
 **/
public class SpringApiTest {

    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("spring-config1.xml");
        UserDao userDao = beanFactory.getBean("userDao", UserDao.class);
        User user = userDao.queryUserInfoById(1L);
        System.out.println(user);

        List<User> users = userDao.queryUserList();
        System.out.println(users.size());

        // UserDao是单例的，所以这里的userDao2和userDao是同一个对象
        UserDao userDao2 = beanFactory.getBean("userDao", UserDao.class);
        User user2 = userDao2.queryUserInfoById(1L);
        System.out.println(user2);
    }

}

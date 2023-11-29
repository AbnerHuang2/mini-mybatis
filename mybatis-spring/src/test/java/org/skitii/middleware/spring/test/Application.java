package org.skitii.middleware.spring.test;

import org.skitii.middleware.spring.test.dao.UserDao;
import org.skitii.middleware.spring.test.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author skitii
 * @since 2023/11/29
 **/
@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"org.skitii.middleware.*"})
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}

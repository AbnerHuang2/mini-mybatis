package org.skitii.middleware.autoconfigure;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.skitii.ibatis.session.SqlSessionFactory;
import org.skitii.ibatis.session.SqlSessionFactoryBuilder;
import org.skitii.middleware.spring.MapperFactoryBean;
import org.skitii.middleware.spring.MapperScannerConfigurer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author skitii
 * @since 2023/11/29
 **/
@Configuration
@ConditionalOnClass({SqlSessionFactory.class})
@EnableConfigurationProperties(MybatisProperties.class)
public class MybatisAutoConfiguration implements InitializingBean {
    @Bean
    //判断当前需要注册的bean的实现类否被spring管理，如果被管理则注入，反之不注入
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(MybatisProperties mybatisProperties) throws Exception {

        Document document = DocumentHelper.createDocument();

        Element configuration = document.addElement("configuration");

        Element environments = configuration.addElement("environments");
        environments.addAttribute("default", "development");

        Element environment = environments.addElement("environment");
        environment.addAttribute("id", "development");
        environment.addElement("transactionManager").addAttribute("type", "JDBC");

        Element dataSource = environment.addElement("dataSource");
        dataSource.addAttribute("type", "POOLED");

        dataSource.addElement("property").addAttribute("name", "driver").addAttribute("value", mybatisProperties.getDriver());
        dataSource.addElement("property").addAttribute("name", "url").addAttribute("value", mybatisProperties.getUrl());
        dataSource.addElement("property").addAttribute("name", "username").addAttribute("value", mybatisProperties.getUsername());
        dataSource.addElement("property").addAttribute("name", "password").addAttribute("value", mybatisProperties.getPassword());

        Element mappers = configuration.addElement("mappers");
        mappers.addElement("mapper").addAttribute("resource", mybatisProperties.getMapperLocations());

        return new SqlSessionFactoryBuilder().build(document);
    }

    public static class AutoConfiguredMapperScannerRegistrar implements EnvironmentAware, ImportBeanDefinitionRegistrar {
        private String basePackage;

        @Override
        public void setEnvironment(Environment environment) {
            this.basePackage = environment.getProperty("mybatis.base-dao-package");
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
            builder.addPropertyValue("basePackage", basePackage);
            registry.registerBeanDefinition(MapperScannerConfigurer.class.getName(), builder.getBeanDefinition());
        }
    }

    @Configuration
    @Import(AutoConfiguredMapperScannerRegistrar.class)
    //@ConditionalOnMissingBean作用：判断当前需要注入Spring容器中的bean的实现类是否已经含有，有的话不注入，没有就注入
    @ConditionalOnMissingBean({MapperFactoryBean.class, MapperScannerConfigurer.class})
    public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {

        @Override
        public void afterPropertiesSet() {
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}

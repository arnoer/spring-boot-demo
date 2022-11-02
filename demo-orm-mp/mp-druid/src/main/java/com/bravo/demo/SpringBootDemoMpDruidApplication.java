package com.bravo.demo;

import com.bravo.demo.props.DruidDataSourceProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 *
 * 官方文档：https://baomidou.com/pages/24112f/
 */
@SpringBootApplication
// 该注解指定@ConfigurationProperties的类或者方法,spring会默认将其注册为bean
@EnableConfigurationProperties({DruidDataSourceProperties.class})
// 扫描Mapper接口
@MapperScan(basePackages = "com.bravo.demo.dao")
@ServletComponentScan(basePackages = {"com.bravo.demo.filter.*", "com.bravo.demo.servlet.*"})
public class SpringBootDemoMpDruidApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoMpDruidApplication.class, args);
    }

}

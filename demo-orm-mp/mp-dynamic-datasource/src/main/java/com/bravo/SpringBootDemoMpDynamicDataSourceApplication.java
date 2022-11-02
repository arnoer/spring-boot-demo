package com.bravo;

import com.bravo.props.DruidDataSourceProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 *
 * 官方文档：https://baomidou.com/pages/24112f/
 */
@SpringBootApplication
// 该注解指定@ConfigurationProperties的类或者方法,spring会默认将其注册为bean
@EnableConfigurationProperties({DruidDataSourceProperties.class})
// 扫描Mapper接口
@MapperScan(basePackages = "com.bravo.dao")
public class SpringBootDemoMpDynamicDataSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoMpDynamicDataSourceApplication.class, args);
    }

}

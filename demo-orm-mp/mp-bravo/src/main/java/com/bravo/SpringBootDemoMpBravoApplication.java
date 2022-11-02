package com.bravo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * 官方文档：https://baomidou.com/pages/24112f/
 */
@SpringBootApplication
@MapperScan(basePackages = "com.bravo.dao") // 扫描Mapper接口
public class SpringBootDemoMpBravoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoMpBravoApplication.class, args);
    }

}

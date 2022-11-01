package com.xkcoding.profile.properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.xkcoding.profile.properties.*")
public class SpringBootDemoProflePropertiesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoProflePropertiesApplication.class, args);
    }

}

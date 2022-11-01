package com.xkcoding.profile.yml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.xkcoding.profile.yml.*")
public class SpringBootDemoProfileYmlApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoProfileYmlApplication.class, args);
    }

}

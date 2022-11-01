package com.xkcoding.profile.properties.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private Environment env;

    @RequestMapping("/getport")
    public String getPort()
    {
        StringBuilder sb = new StringBuilder();

        String[] activeProfiles = env.getActiveProfiles();
        sb.append("启动的 profile 名称为：").append(activeProfiles[0]);

        sb.append("<br/>");

        String portValue = env.getProperty("server.port");
        sb.append("启动的端口为：").append(portValue);
        return sb.toString();
    }
}

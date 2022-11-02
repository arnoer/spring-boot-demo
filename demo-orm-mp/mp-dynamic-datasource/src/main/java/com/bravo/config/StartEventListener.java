package com.bravo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author dell
 */
@Slf4j
@Configuration
public class StartEventListener {

    @Autowired
    private Environment env; // springboot提供的将属性文件中的数据封装到该对象中

	@Async
	@Order
	@EventListener(WebServerInitializedEvent.class)
	public void afterStart(WebServerInitializedEvent event) {
//        log.info("启动完成：{}", env.getActiveProfiles());
		Environment environment = event.getApplicationContext().getEnvironment();
        String appName = Optional.ofNullable(environment.getProperty("spring.application.name")).toString().toUpperCase();
		int localPort = event.getWebServer().getPort();
		String profile = StringUtils.arrayToCommaDelimitedString(environment.getActiveProfiles());
		log.info("---[{}]---启动完成，当前使用的端口:[{}]，环境变量:[{}]---", appName, localPort, profile);
	}
}

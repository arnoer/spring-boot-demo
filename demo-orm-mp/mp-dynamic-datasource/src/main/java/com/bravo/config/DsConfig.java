package com.bravo.config;

import com.baomidou.dynamic.datasource.aop.DynamicDataSourceAnnotationAdvisor;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceCreatorAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceHealthCheckConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidDynamicDataSourceConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;

@Slf4j
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@AutoConfigureBefore(DynamicDataSourceAutoConfiguration.class)
@Import(value = {DruidDynamicDataSourceConfiguration.class, DynamicDataSourceCreatorAutoConfiguration.class, DynamicDataSourceHealthCheckConfiguration.class})
@ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class DsConfig extends DynamicDataSourceAutoConfiguration {

	private final DynamicDataSourceProperties properties;

	public DsConfig(DynamicDataSourceProperties properties) {
		super(properties);
		this.properties = properties;
	}


	@Override
	@Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
	@Bean
	@ConditionalOnMissingBean
	public DynamicDataSourceAnnotationAdvisor dynamicDatasourceAnnotationAdvisor(DsProcessor dsProcessor) {
		BxDynamicDataSourceAnnotationInterceptor interceptor = new BxDynamicDataSourceAnnotationInterceptor(
			properties.isAllowedPublicOnly(), dsProcessor);
		DynamicDataSourceAnnotationAdvisor advisor1 = new DynamicDataSourceAnnotationAdvisor(interceptor);
		advisor1.setOrder(properties.getOrder());
		return advisor1;
	}

}

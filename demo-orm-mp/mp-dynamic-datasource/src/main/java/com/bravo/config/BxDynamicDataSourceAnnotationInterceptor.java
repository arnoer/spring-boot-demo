/*
 * Copyright © 2018 organization baomidou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bravo.config;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.aop.DynamicDataSourceAnnotationInterceptor;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.support.DataSourceClassResolver;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;

/**
 * Core Interceptor of Dynamic Datasource
 *
 * @author TaoYu
 * @since 1.2.0
 */
@Slf4j
public class BxDynamicDataSourceAnnotationInterceptor extends DynamicDataSourceAnnotationInterceptor {

	/**
	 * The identification of SPEL.
	 */
	private static final String DYNAMIC_PREFIX = "#";

	private static Class<DS> dsC;

	private final DataSourceClassResolver dataSourceClassResolver;

	private final DsProcessor dsProcessor;


	public BxDynamicDataSourceAnnotationInterceptor(Boolean allowedPublicOnly, DsProcessor dsProcessor) {
		super(allowedPublicOnly, dsProcessor);
		dataSourceClassResolver = new DataSourceClassResolver(allowedPublicOnly);
		this.dsProcessor = dsProcessor;
	}


	static {
		try {
			dsC = (Class<DS>) Class.forName("com.baomidou.dynamic.datasource.annotation.DS");
		} catch (ClassNotFoundException e) {
			log.error("com.baomidou.dynamic.datasource.annotation.DS not found, check dependency!");
		}
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String dsKey = determineDatasourceKey(invocation);
		DynamicDataSourceContextHolder.push(dsKey);
		try {
			return invocation.proceed();
		} finally {
			DynamicDataSourceContextHolder.poll();
		}
	}

	private String determineDatasourceKey(MethodInvocation invocation) {
		Annotation a = invocation.getMethod().getAnnotation(dsC);
//		log.info("method anno == null: {}" , a == null);
		if (a != null) {
			log.info("method anno， return value: {}", ((com.baomidou.dynamic.datasource.annotation.DS) a).value());
			return ((DS) a).value();
		}
		// 查找接口里有没有DS标签
		Class<?>[] clses = invocation.getThis().getClass().getInterfaces();
		for (Class c : clses) {
//			log.info("c: {}" , c.toString());
			a = c.getAnnotation(dsC);
//			log.info("a == null: {}" , a == null);
			if (a != null) {
				log.info("return value: {}", ((com.baomidou.dynamic.datasource.annotation.DS) a).value());
				return ((DS) a).value();
			}
		}

		String key = dataSourceClassResolver.findDSKey(invocation.getMethod(), invocation.getThis());
		log.info("key: {}", key);
		String key1 = (!key.isEmpty() && key.startsWith(DYNAMIC_PREFIX)) ? dsProcessor.determineDatasource(invocation, key) : key;
		log.info("key1: {}", key1);
		return key1;
	}
}

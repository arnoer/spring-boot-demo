package com.xkcoding.spel.annotation;

import com.baomidou.mybatisplus.extension.service.IService;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解参数的可支持数据类型：
 * 1.所有基本数据类型（int,float,boolean,byte,double,char,long,short)
 * 　　　　2.String类型
 * 　　　　3.Class类型
 * 　　　　4.enum类型
 * 　　　　5.Annotation类型
 * 　　　　6.以上所有类型的数组
 * @author
 */
@Documented
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationDescAnnotation {
	/**
	 * 操作详细描述,支持SPEL表达式
	 *
	 * @return
	 */
	String value() default "";

	/**
	 * 操作对象类型,如INQUIRY,NEWS,DOWNLOAD等等
	 *
	 * @return
	 */
	String operationTarget() default "";

	/**
	 * 操作对象Id,支持SPEL表达式
	 *
	 * @return
	 */
	String operationTargetId() default "";

	/**
	 * 操作类型，如INSERT,UPDATE,DELETE,SELECT,LOGIN,LOGOUT等
	 *
	 * @return
	 */
	String operationType() default "";

	/**
	 * 操作对象名称，放入EvaluationContext解析用，配合value解析,默认叫obj
	 *
	 * @return
	 */
	String objectName() default "obj";

	/**
	 * 当前用户id
	 *
	 * @return
	 */
	String userId() default "";

	/**
	 * 当前用户名称
	 *
	 * @return
	 */
	String userName() default "";

	/**
	 * 当前用户的token，默认表达式为#baseRequest.token
	 *
	 * @return
	 */
	String token() default "#baseRequest.token";

	/**
	 * 根据operationTargetId查询详细信息的服务，查询结果放入EvaluationContext，key的名称是objectName对应的值
	 *
	 * @return
	 */
	Class<? extends IService<?>>[] service() default {};
}

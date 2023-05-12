package com.xkcoding.spel.aop;

import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Lists;
import com.xkcoding.spel.annotation.OperationDescAnnotation;
import com.xkcoding.spel.utils.ExpressionParserHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * spel+aop获取动态参数
 *
 * @author zhangxinyu
 * @date 2023/5/11
 **/
@Aspect
@Component
@Slf4j
public class OperatorLogAop implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private ExpressionParserHelper expressionParserHelper;

    private final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    @PostConstruct
    public void init() {
        log.info("SpelGetParm init ......");
    }

    @AfterReturning(value = "@annotation(com.xkcoding.spel.annotation.OperationDescAnnotation)", returning = "result")
    public void afterReturn(JoinPoint joinPoint, Object result) {
        Class<?> cls = joinPoint.getTarget().getClass();
        String className = cls.getName();
        String methodName = joinPoint.getSignature().getName();
        log.debug(className + "的" + methodName + "方法执行了.");

        // if (result instanceof R) {
        //     R r = (R) result;
        //     // isSuccess的code判断标准为200，此处直接用code比较，项目中code=0为成功
        //     boolean success = r.isSuccess();
        //     if (!success) {
        //         log.debug("result fail, R:{}", r.getMsg());
        //         return;
        //     }
        // }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取被注解方法的形参
        Object[] args = joinPoint.getArgs();

        // 使用Spring的LocalVariableTableParameterNameDiscoverer获取方法形参名数组
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        // 将方法参数变量名和值设置到EvaluationContext中
        EvaluationContext context = expressionParserHelper.buildEvaContext(args, parameterNames, result);
        // 获取方法上传入的注解变量和值
        OperationDescAnnotation[] cacheNotifications = method.getAnnotationsByType(OperationDescAnnotation.class);

        for (OperationDescAnnotation cacheNotification : cacheNotifications) {
            log.debug("======================开始打印参数==========================");
            String valueSpel = cacheNotification.value();

            Object value = expressionParserHelper.getValue(valueSpel, context);
            log.debug("valueSpel:{}, value:{}", valueSpel, value);

            String targetIdSpel = cacheNotification.operationTargetId();
            Object target = expressionParserHelper.getValue(targetIdSpel, context);
            if (target == null) {
                return;
            }

            // 兼容批量操作日志
            List<Object> list;
            if (target instanceof List) {
                list = (List<Object>) target;
            } else if (target instanceof String) {
                list = Lists.newArrayList(target.toString().split(","));
            } else {
                list = Lists.newArrayList(target);
            }

            log.debug("targetIdSpel:{}, targetId:{}", targetIdSpel, list);

            for (Object targetId : list) {
                if (targetId != null && !StringUtils.isEmpty(targetId.toString())) {
                    String targetName = cacheNotification.objectName();
                    Class<? extends IService<?>>[] services = cacheNotification.service();
                    Class<? extends IService<?>> s = services.length > 0 ? services[0] : null;
                    if (s != null && !StringUtils.isEmpty(targetName)) {
                        IService<?> iService = applicationContext.getBean(s);
                        Object object = iService.getById((Serializable)targetId);
                        // 根据服务获取对应的数据设置到context中
                        context.setVariable(targetName, object);
                        log.info("targetName:{},object:{}", targetName, object);
                    }
                } else {
                    targetId = "";
                }
            }

        }

    }

    @Around(value = "@annotation(opDescAnnotation)")
    public Object invoke(ProceedingJoinPoint joinPoint, OperationDescAnnotation opDescAnnotation) {
        Object result = null;
        String methodName = joinPoint.getSignature().getName();
        log.debug("method name:{}", methodName);

        String parm = expressionParserHelper.generateKeyBySpEL(opDescAnnotation.value(), joinPoint);

        log.info("spel获取动态aop参数: {}", parm);

        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

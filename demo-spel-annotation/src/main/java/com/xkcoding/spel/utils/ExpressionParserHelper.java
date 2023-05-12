package com.xkcoding.spel.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @Description: SpEL表达式解析工具类
 */
@Component
@Slf4j
public class ExpressionParserHelper {

    /**
     * 用于SpEL表达式解析.
     */
    private final SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 用于获取方法参数定义名字.
     */
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();


    /**
     * 将方法参数和方法返回值写入EvaluationContext，以供解析
     *
     * @param args           方法参数值
     * @param parameterNames 方法参数名称数组
     * @param result         方法返回值
     * @return Spring的表达式上下文对象
     */
    public EvaluationContext buildEvaContext(Object[] args, String[] parameterNames, Object result) {
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("result", result);
        log.debug("result:" + result);
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                String name = parameterNames[i];
                Object value = args[i];
                log.debug("parameter Name:" + name + ",parameter value:" + value);
                context.setVariable(name, value);
            }
        }
        return context;
    }

    /**
     * 解析Spel表达式获取value
     *
     * @param spelExp spel表达式
     * @param context 解析填充后的context
     * @return 表达式对应的value
     */
    public <T> T getValue(String spelExp, EvaluationContext context) {
        // 不是正确的表达式
        if (StringUtils.isEmpty(spelExp) || !spelExp.contains("#")) {
            return (T) spelExp;
        }
        return (T) parser.parseExpression(spelExp).getValue(context, Object.class);
    }

    public <T> T getValue(String spelExp, Object o, Class cl) {
        return Objects.nonNull(cl) ? (T) parser.parseExpression(spelExp).getValue(o, cl) : (T) parser.parseExpression(spelExp).getValue(o);
    }

    

    /**
     * 解析SpEL表达式
     *
     * @param spELStr
     * @param joinPoint
     * @return
     */
    public  String generateKeyBySpEL(String spELStr, ProceedingJoinPoint joinPoint) {
        // 通过joinPoint获取被注解方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        // 使用Spring的DefaultParameterNameDiscoverer获取方法形参名数组
        String[] paramNames = nameDiscoverer.getParameterNames(method);
        // 解析过后的Spring表达式对象
        Expression expression = parser.parseExpression(spELStr);
        // Spring的表达式上下文对象
        EvaluationContext context = new StandardEvaluationContext();
        // 通过joinPoint获取被注解方法的形参
        Object[] args = joinPoint.getArgs();
        // 给上下文赋值
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        // 表达式从上下文中计算出实际参数值
        /*如:
            @annotation(key="#user.name")
            method(User user)
             那么就可以解析出方法形参的某属性值，return “xiaoming”;
          */
        return expression.getValue(context).toString();
    }
}

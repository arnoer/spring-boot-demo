# mp-demo

> 案例copy至bravo的小册，主要演示springboot如何集成mybatisPlus

## application.yml

```yaml
    server:
    port: 7890

    spring:
      datasource:
        url: jdbc:mysql:///mp_demo??useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true
        username: root
        password: root
        driver-class-name: com.mysql.jdbc.Driver

    logging:
      level:
        com.bravo.demo.dao: debug
```

## 配置动态数据源

> 通过注解@Ds来选择数据源



## 集成Druid

pom.xml

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <!--            <version>1.1.16</version>-->
    <version>1.1.18</version>
</dependency>
```



### 开启监控

Druid Spring Boot Starter 对以下 Druid 内置 Filter，都提供了默认配置：

* StatFilter 通过它可以开启 Druid 的 SQL 监控功能，对 SQL 进行监控.
* WallFilter 使用它可以开启防火墙功能，防御 SQL 注入攻击。
* ConfigFilter
* EncodingConvertFilter
* Slf4jLogFilter
* Log4jFilter
* CommonsLogFilter 可以通过spring.datasource.druid.filters=stat,wall … 的方式来启用相应的内置 Filter，不过这些 Filter 使用的都是默认配置。如果默认配置不能满足我们的需求，我们还可以在配置文件使用 spring.datasource.druid.filter.* 对这些 Filter 进行配置，示例代码如下：

### application.yml

```yaml
#Druid 内置Filter配置
spring:
    datasource:
      druid:
        #对配置已开启的 filters 即 stat(sql 监控)wall（防火墙）
        filter:
          #配置StatFilter (SQL监控配置)
          stat:
            enabled: true #开启 SQL 监控
            slow-sql-millis: 1000 #慢查询
            log-slow-sql: true #记录慢查询 SQL
          #配置WallFilter (防火墙配置)
          wall:
            enabled: true #开启防火墙
            config:
              update-allow: true #允许更新操作
              drop-table-allow: false #禁止删表操作
              insert-allow: true #允许插入操作
              delete-allow: true #删除数据操作

```

DruidConfig.class

```java
@Configuration
public class DruidConfig {
    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        servletRegistrationBean.setServlet(new StatViewServlet());
        servletRegistrationBean.addUrlMappings("/druid/*");
        Map<String, String> initParameters = new HashMap<>();
        //禁用HTML页面上的“Rest All”功能
        initParameters.put("resetEnable", "false");
        //ip白名单（没有配置或者为空，则允许所有访问）
        initParameters.put("allow", "10.8.9.115");
        //++监控页面登录用户名
        initParameters.put("loginUsername", "admin");
        //++监控页面登录用户密码
        initParameters.put("loginPassword", "admin");
        //ip黑名单
        initParameters.put("deny", "");
        //如果某个ip同时存在，deny优先于allow
        servletRegistrationBean.setInitParameters(initParameters);
        return servletRegistrationBean;
    }
}

```

将项目启动后，打开浏览器访问：http://localhost:7890/druid/index.html， 输入密码

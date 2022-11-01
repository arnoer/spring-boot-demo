# demo-profile

> 本demo主要演示了SpringBoot支持的两种不同格式的配置文件的使用

## 启动项目

指定临时属性
cmd java -jar .\demo-properties.jar --spring.config.location=classpath:/application.yml
idea   在program argument中添加 --spring.config.location=classpath:/application.yml,...  # 可以指定多个

还可以指定以下临时属性:
* --server.port=80
* --spring.profiles.active=test # 指定选择哪个配置文件

[临时属性是如何生效的](https://blog.csdn.net/u012098021/article/details/112324818)

其他方式:

* 直接在application.yml指定运行环境
* 在active profile指定运行环境
* vm options中添加: -Dspring.profiles.active=dev  # 还可以指定其他属性: -Dfile.encoding=UTF-8 -Xss100m


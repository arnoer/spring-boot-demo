# Config Zookeeper

## dubbo注册zookper案例

[demo-dubbo](../demo-dubbo)

## 下载

[官网](https://zookeeper.apache.org/)

[清华大学开源软件镜像站](https://mirrors.tuna.tsinghua.edu.cn/apache/zookeeper)

解压：

使用window10||11配置的windows-terminal, linux 环境解压，cd /mnt, tar -zxvf .\apache-zookeeper-3.6.3-bin.tar.gz

## windows

copy conf目录下的文件 zoo_sample.cfg， 修改为zoo.cfg.

zoo.cfg下添加配置：

dataDir=/usr/local/zookeeper/data

dataLogDir=/usr/local/zookeeper/log

运行zkServer.cmd。

若出现`ZooKeeper audit is disabled`，在zoo.cfg文件中添加：`audit.enable=true`，或者在zkServer.cmd文件中添加`-Dzookeeper.audit.
enable=true`

## linux

官网下载linux版本，或者执行： wget https://archive.apache.org/dist/zookeeper/zookeeper-3.4.12/zookeeper-3.4.12.tar.gz

copy一份zoo_sample.cfg命名为zoo.cfg，添加配置：dataDir=/usr/local/zookeeper/data

启动测试：./zkServer.sh start

查看zookeeper状态：./zkServer.sh status  或者运行 netstat -lntup 查看 2181 端口

## 解决问题

在zkServer.cmd文件末尾，添加pause，运行出错打印错误信息



## 常用命令

查看zookeeper注册了哪些服务：
```shell
sh zkCli.sh -server zk所在服务器地址:2181    默认可以不用加上-server

ls /  查看注册的服务

ls /dubbo

# 查看注册的生产者
ls /dubbo/com.xkcoding.dubbo.common.service.HelloService/providers

# 输出
dubbo://172.22.208.1:20880/com.xkcoding.dubbo.common.service.HelloService?anyhost=true&application=spring-boot-demo-dubbo-provider&dubbo=2.6.0&generic=false&interface=com.xkcoding.dubbo.common.service.HelloService&methods=sayHello&pid=409116&side=provider&timestamp=1684136305600

# 172.22.208.1:20880 是生产者机器的位置，20880是默认暴露的端口

```

[zookeeper常用命令行](https://blog.csdn.net/xiao__jia__jia/article/details/84787038)

# Brave-Instrumentation-Mybatis

## 中文说明

这包括一个MyBatis语句拦截器，该拦截器将向Zipkin报告

每个语句需要多长时间，以及相关标签（例如查询）。

要使用它，请将以下代码添加到pom.xml。

```
<dependency>
 <groupId>com.github.JokerLee-9527</groupId>
 <artifactId>brave-instrumentation-mybatis</artifactId>
 <version>1.0.0</version>
</dependency>
```

### 随便说说:

项目中使用要使用zipkin跟踪oracle的项目,因为不能想mysql一样在sql连接串中添加statementInterceptors=brave.mysql.TracingStatementInterceptor&zipkinServiceName=myDatabaseService。

当时项目用的是mybati,想到写一个mybatis的拦截插件。





## English

This includes a MyBatis statement interceptor that will report to Zipkin
how long each statement takes, along with relevant tags like the query.

To use it, Add the following code to pom.xml.

```
<dependency>
  <groupId>com.github.JokerLee-9527</groupId>
  <artifactId>brave-instrumentation-mybatis</artifactId>
  <version>1.0.0</version>
</dependency>
```



### Just checking

The project uses zipkin to track the oracle project, because you cannot add statementInterceptors = brave.mysql.TracingStatementInterceptor & zipkinServiceName = myDatabaseService in the sql connection string like mysql.

At the time the project was using mybati, and I thought of writing a interception plugin for mybatis.

















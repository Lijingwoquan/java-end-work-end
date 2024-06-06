# 使用 OpenJDK 作为基础镜像
FROM openjdk:11-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制 Java 源代码到工作目录
COPY src /app/src

# 复制编译好的库的 .class 文件
COPY libs/json/org/json /app/libs/org/json

# 复制 MySQL 驱动的 JAR 文件
COPY libs/mysql/mysql-connector-j-8.4.0.jar /app/libs/

# 编译 Java 源代码
RUN find /app/src -name "*.java" > sources.txt && \
    javac -cp ".:/app/libs/mysql-connector-j-8.4.0.jar:/app/libs" -d /app/classes @sources.txt

EXPOSE 8082

# 设置类路径包含所有编译后的类和库
ENV CLASSPATH /app/classes:/app/libs/mysql-connector-j-8.4.0.jar:/app/libs

# 设置启动命令
ENTRYPOINT ["java", "-cp", "/app/classes:/app/libs/mysql-connector-j-8.4.0.jar:/app/libs", "SimpleHttpServer"]
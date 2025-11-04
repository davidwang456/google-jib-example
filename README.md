# Jib Spring Boot 示例项目

这是一个使用 Jib 将 Spring Boot 应用打包成 Docker 镜像的示例项目。

## 项目结构

```
jibexample/
├── pom.xml                                    # Maven配置文件
├── README.md                                  # 项目说明文档
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── jibexample/
        │               ├── JibExampleApplication.java    # Spring Boot主类
        │               └── controller/
        │                   └── HelloController.java      # REST控制器
        └── resources/
            └── application.properties                    # 应用配置
```

## 功能特性

- Spring Boot 3.2.0
- RESTful API 示例
- 健康检查端点
- 使用 Jib Maven 插件打包 Docker 镜像

## 前置要求

- JDK 17 或更高版本
- Maven 3.6 或更高版本
- Docker（可选，如果要将镜像推送到本地Docker daemon）

## 使用方法

### 1. 构建项目

```bash
mvn clean package
```

### 2. 使用 Jib 构建 Docker 镜像

#### 方式一：构建并加载到本地 Docker（推荐）

```bash
mvn compile jib:dockerBuild
```

这会在本地构建镜像并加载到 Docker daemon 中，镜像名为 `jib-example:latest`。

#### 方式二：构建 Docker tar 文件

```bash
mvn compile jib:buildTar
```

这会生成一个 tar 文件：`target/jib-image.tar`，可以使用以下命令加载：

```bash
docker load -i target/jib-image.tar
```

#### 方式三：直接推送到远程仓库

修改 `pom.xml` 中的 `<to><image>` 配置为你的镜像仓库地址：

```xml
<to>
    <image>your-registry.com/your-username/jib-example:latest</image>
</to>
```

然后执行：

```bash
# 需要先登录到镜像仓库
docker login your-registry.com

# 构建并推送
mvn compile jib:build
```

### 3. 运行容器

```bash
docker run -d -p 8080:8080 --name jib-example jib-example:latest
```

### 4. 测试应用

```bash
# 健康检查
curl http://localhost:8080/api/health

# Hello接口
curl http://localhost:8080/api/hello
```

## API 端点

- `GET /api/hello` - 返回欢迎消息和时间戳
- `GET /api/health` - 健康检查端点
- `GET /actuator/health` - Spring Boot Actuator 健康检查

## Jib 配置说明

在 `pom.xml` 中配置了 Jib Maven 插件：

- **基础镜像**: `eclipse-temurin:17-jre-alpine` (轻量级 JRE)
- **目标镜像**: `jib-example:latest`
- **JVM参数**: `-Xms512m -Xmx512m`
- **端口**: `8080`
- **格式**: `docker`

## Jib 的优势

1. **无需 Dockerfile** - Jib 自动处理镜像构建
2. **无需 Docker daemon** - 可以直接推送到远程仓库
3. **分层优化** - 依赖和代码分离，加快构建速度
4. **可重现构建** - 相同输入产生相同输出

## 参考资源

- [Jib GitHub](https://github.com/GoogleContainerTools/jib)
- [Jib Maven Plugin 文档](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin)
- [Spring Boot 文档](https://spring.io/projects/spring-boot)

## 许可证

本项目仅用于示例和学习目的。


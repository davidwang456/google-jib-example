# Jib Spring Boot 示例项目

这是一个使用 Jib 将 Spring Boot 应用打包成 Docker 镜像的示例项目，展示了标准的四层架构（Entity、DAO、Service、Controller）和完整的单元测试。

## 项目结构

```
jibexample/
├── pom.xml                                    # Maven配置文件
├── README.md                                  # 项目说明文档
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── example/
    │   │           └── jibexample/
    │   │               ├── JibExampleApplication.java    # Spring Boot主类
    │   │               ├── entity/
    │   │               │   └── User.java                  # 用户实体类
    │   │               ├── dao/
    │   │               │   └── UserRepository.java        # 数据访问层接口
    │   │               ├── service/
    │   │               │   └── UserService.java           # 业务逻辑层
    │   │               └── controller/
    │   │                   ├── HelloController.java       # Hello REST控制器
    │   │                   └── UserController.java        # 用户管理REST控制器
    │   └── resources/
    │       └── application.properties                     # 应用配置
    └── test/
        └── java/
            └── com/
                └── example/
                    └── jibexample/
                        ├── service/
                        │   └── UserServiceTest.java      # Service层单元测试
                        └── controller/
                            └── UserControllerTest.java    # Controller层单元测试
```

## 功能特性

- **Spring Boot 3.2.0** - 基于最新Spring Boot框架
- **四层架构** - Entity、DAO、Service、Controller标准分层
- **JPA数据访问** - 使用Spring Data JPA进行数据持久化
- **H2内存数据库** - 开发环境使用H2数据库
- **RESTful API** - 完整的用户管理API
- **单元测试** - Service层和Controller层完整测试用例
- **健康检查端点** - Actuator健康检查
- **使用 Jib Maven 插件打包 Docker 镜像**

## 技术栈

- **JDK**: 17
- **Spring Boot**: 3.2.0
- **Spring Data JPA**: 数据访问层
- **H2 Database**: 内存数据库
- **JUnit 5**: 单元测试框架
- **Mockito**: Mock测试框架
- **Maven**: 项目构建工具

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

使用默认配置：
```bash
mvn compile jib:dockerBuild
```

使用自定义镜像配置：
```bash
# 指定基础镜像和目标镜像
mvn compile jib:dockerBuild \
  -Djib.from.image=eclipse-temurin:17-jre-alpine \
  -Djib.to.image=my-custom-image:1.0.0

# 或者只覆盖目标镜像
mvn compile jib:dockerBuild -Djib.to.image=my-registry.com/my-app:v1.0.0
```

这会在本地构建镜像并加载到 Docker daemon 中。

#### 方式二：构建 Docker tar 文件

使用默认配置：
```bash
mvn compile jib:buildTar
```

使用自定义镜像配置：
```bash
mvn compile jib:buildTar \
  -Djib.from.image=eclipse-temurin:17-jre-alpine \
  -Djib.to.image=my-app:latest
```

这会生成一个 tar 文件：`target/jib-image.tar`，可以使用以下命令加载：

```bash
docker load -i target/jib-image.tar
```

#### 方式三：直接推送到远程仓库

使用命令行参数指定远程仓库地址：

```bash
# 需要先登录到镜像仓库
docker login your-registry.com

# 构建并推送到远程仓库
mvn compile jib:build \
  -Djib.from.image=eclipse-temurin:17-jre-alpine \
  -Djib.to.image=your-registry.com/your-username/jib-example:latest
```

**注意**：如果不指定命令行参数，将使用 `pom.xml` 中 `<properties>` 部分定义的默认值：
- `jib.from.image` (默认: `eclipse-temurin:17-jre-alpine`)
- `jib.to.image` (默认: `jib-example:latest`)

### 3. 运行容器

```bash
docker run -d -p 8080:8080 --name jib-example jib-example:latest
```

### 4. 运行单元测试

```bash
mvn test
```

### 5. 测试应用

```bash
# 健康检查
curl http://localhost:8080/api/health

# Hello接口
curl http://localhost:8080/api/hello

# 创建用户
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "name": "测试用户"
  }'

# 获取所有用户
curl http://localhost:8080/api/users

# 根据ID获取用户
curl http://localhost:8080/api/users/1

# 根据用户名获取用户
curl http://localhost:8080/api/users/username/testuser

# 更新用户
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "updateduser",
    "email": "updated@example.com",
    "name": "更新用户"
  }'

# 删除用户
curl -X DELETE http://localhost:8080/api/users/1
```

## API 端点

### Hello接口
- `GET /api/hello` - 返回欢迎消息和时间戳
- `GET /api/health` - 健康检查端点

### 用户管理接口
- `POST /api/users` - 创建用户
- `GET /api/users` - 获取所有用户列表
- `GET /api/users/{id}` - 根据ID获取用户
- `GET /api/users/username/{username}` - 根据用户名获取用户
- `PUT /api/users/{id}` - 更新用户信息
- `DELETE /api/users/{id}` - 删除用户

### Actuator端点
- `GET /actuator/health` - Spring Boot Actuator 健康检查
- `GET /h2-console` - H2数据库控制台（开发环境）

## 数据库配置

项目使用H2内存数据库，配置信息在 `application.properties` 中：

- **数据库URL**: `jdbc:h2:mem:testdb`
- **用户名**: `sa`
- **密码**: 空
- **JPA自动更新表结构**: `spring.jpa.hibernate.ddl-auto=update`

可以通过 `http://localhost:8080/h2-console` 访问H2控制台（开发环境）。

## 四层架构说明

### 1. Entity层（实体层）
- **位置**: `src/main/java/com/example/jibexample/entity/`
- **说明**: 定义数据实体类，使用JPA注解进行ORM映射
- **示例**: `User.java` - 用户实体类

### 2. DAO层（数据访问层）
- **位置**: `src/main/java/com/example/jibexample/dao/`
- **说明**: 数据访问接口，继承Spring Data JPA的`JpaRepository`
- **示例**: `UserRepository.java` - 用户数据访问接口

### 3. Service层（业务逻辑层）
- **位置**: `src/main/java/com/example/jibexample/service/`
- **说明**: 业务逻辑处理，包含数据验证、异常处理等
- **示例**: `UserService.java` - 用户业务逻辑服务

### 4. Controller层（控制器层）
- **位置**: `src/main/java/com/example/jibexample/controller/`
- **说明**: REST API控制器，处理HTTP请求和响应
- **示例**: `UserController.java` - 用户管理REST控制器

## 单元测试

项目包含完整的单元测试用例：

### Service层测试
- **文件**: `src/test/java/com/example/jibexample/service/UserServiceTest.java`
- **测试内容**:
  - 用户创建（成功/失败场景）
  - 用户查询（ID/用户名）
  - 用户更新
  - 用户删除
  - 异常处理

### Controller层测试
- **文件**: `src/test/java/com/example/jibexample/controller/UserControllerTest.java`
- **测试内容**:
  - REST API端点测试
  - HTTP状态码验证
  - JSON响应格式验证
  - 异常响应处理

运行测试：
```bash
mvn test
```

## Jib 配置说明

在 `pom.xml` 中配置了 Jib Maven 插件，基础镜像和目标镜像可以通过命令行参数动态指定：

### 默认配置（在 pom.xml 的 properties 中定义）

- **基础镜像**: `eclipse-temurin:17-jre-alpine` (轻量级 JRE)
- **目标镜像**: `jib-example:latest`
- **JVM参数**: `-Xms512m -Xmx512m`
- **端口**: `8080`
- **格式**: `docker`

### 通过命令行参数覆盖镜像配置

可以在执行 Jib 命令时通过 Maven 属性覆盖默认配置：

```bash
# 覆盖基础镜像
mvn compile jib:dockerBuild -Djib.from.image=openjdk:17-jre-slim

# 覆盖目标镜像
mvn compile jib:dockerBuild -Djib.to.image=my-registry.com/my-app:v1.0.0

# 同时覆盖基础镜像和目标镜像
mvn compile jib:dockerBuild \
  -Djib.from.image=eclipse-temurin:17-jre-alpine \
  -Djib.to.image=registry.example.com/jib-example:1.0.0
```

**参数说明**：
- `-Djib.from.image`: 指定基础镜像（FROM镜像）
- `-Djib.to.image`: 指定目标镜像名称和标签

这种方式使得在不同环境（开发、测试、生产）中使用不同的镜像配置变得更加灵活，无需修改 `pom.xml` 文件。

## Jib 的优势

1. **无需 Dockerfile** - Jib 自动处理镜像构建
2. **无需 Docker daemon** - 可以直接推送到远程仓库
3. **分层优化** - 依赖和代码分离，加快构建速度
4. **可重现构建** - 相同输入产生相同输出

## 使用 Buildah 从 Maven 镜像复制 JDK 和 Maven

Buildah 是一个用于构建 OCI（Open Container Initiative）容器镜像的工具，它可以在不需要 Docker daemon 的情况下构建镜像。以下步骤详细说明如何从一个 Maven 镜像中提取 JDK 和 Maven，并将它们复制到使用 buildah 构建的新镜像中。

### 前置要求

- 安装 buildah（Linux 系统或 WSL）
- 了解容器镜像的基本概念

### 步骤一：准备工作

首先，确定源 Maven 镜像和目标基础镜像：

```bash
# 源 Maven 镜像（包含 JDK 和 Maven）
SOURCE_IMAGE="maven:3.9-eclipse-temurin-17"

# 目标基础镜像（可以是任何基础镜像，如 alpine、ubuntu 等）
TARGET_BASE_IMAGE="alpine:latest"

# 新镜像名称
NEW_IMAGE_NAME="custom-maven-jdk:17"
```

### 步骤二：创建临时容器并提取文件

使用 buildah 从源镜像创建容器，并提取 JDK 和 Maven：

```bash
# 1. 从源 Maven 镜像创建容器
SOURCE_CONTAINER=$(buildah from $SOURCE_IMAGE)

# 2. 创建临时目录用于存储提取的文件
TEMP_DIR=$(mktemp -d)
echo "临时目录: $TEMP_DIR"

# 3. 从源容器中复制 JDK（通常在 /usr/local/openjdk-17 或 /opt/java/openjdk）
buildah copy --from $SOURCE_CONTAINER $TEMP_DIR/jdk /usr/local/openjdk-17

# 4. 从源容器中复制 Maven（通常在 /usr/share/maven）
buildah copy --from $SOURCE_CONTAINER $TEMP_DIR/maven /usr/share/maven

# 5. 检查复制的文件
ls -la $TEMP_DIR/jdk
ls -la $TEMP_DIR/maven
```

**注意**：JDK 和 Maven 的路径可能因镜像而异，常见路径包括：
- JDK: `/usr/local/openjdk-17`, `/opt/java/openjdk`, `/usr/lib/jvm/java-17-openjdk`
- Maven: `/usr/share/maven`, `/opt/maven`, `/usr/local/maven`

### 步骤三：查找 JDK 和 Maven 的实际路径

如果不确定路径，可以先进入容器查看：

```bash
# 进入源容器查看文件结构
buildah run $SOURCE_CONTAINER -- sh -c "echo 'JAVA_HOME:' && echo \$JAVA_HOME"
buildah run $SOURCE_CONTAINER -- sh -c "which java"
buildah run $SOURCE_CONTAINER -- sh -c "mvn --version"
buildah run $SOURCE_CONTAINER -- sh -c "ls -la /usr/local/ | grep -E 'openjdk|java'"
buildah run $SOURCE_CONTAINER -- sh -c "ls -la /usr/share/ | grep maven"
```

### 步骤四：创建新镜像并复制文件

使用 buildah 创建新镜像，并复制 JDK 和 Maven：

```bash
# 1. 从目标基础镜像创建新容器
NEW_CONTAINER=$(buildah from $TARGET_BASE_IMAGE)

# 2. 安装必要的依赖（Alpine 需要安装 glibc 兼容层）
buildah run $NEW_CONTAINER -- sh -c "apk add --no-cache bash curl"

# 3. 复制 JDK 到新容器
buildah copy $NEW_CONTAINER $TEMP_DIR/jdk /usr/local/openjdk-17

# 4. 复制 Maven 到新容器
buildah copy $NEW_CONTAINER $TEMP_DIR/maven /usr/share/maven

# 5. 设置环境变量
buildah config --env JAVA_HOME=/usr/local/openjdk-17 $NEW_CONTAINER
buildah config --env PATH="$JAVA_HOME/bin:/usr/share/maven/bin:$PATH" $NEW_CONTAINER
buildah config --env MAVEN_HOME=/usr/share/maven $NEW_CONTAINER

# 6. 创建符号链接（如果需要）
buildah run $NEW_CONTAINER -- sh -c "ln -s /usr/local/openjdk-17/bin/java /usr/local/bin/java || true"
buildah run $NEW_CONTAINER -- sh -c "ln -s /usr/share/maven/bin/mvn /usr/local/bin/mvn || true"

# 7. 验证安装
buildah run $NEW_CONTAINER -- java -version
buildah run $NEW_CONTAINER -- mvn --version
```

### 步骤五：提交镜像

将容器提交为镜像：

```bash
# 提交为新镜像
buildah commit $NEW_CONTAINER $NEW_IMAGE_NAME

# 清理临时容器
buildah rm $SOURCE_CONTAINER $NEW_CONTAINER

# 清理临时目录
rm -rf $TEMP_DIR
```

### 完整脚本示例

以下是一个完整的脚本，自动化上述过程：

```bash
#!/bin/bash
set -e

# 配置变量
SOURCE_IMAGE="maven:3.9-eclipse-temurin-17"
TARGET_BASE_IMAGE="alpine:latest"
NEW_IMAGE_NAME="custom-maven-jdk:17"

echo "=== 步骤 1: 从源镜像创建容器 ==="
SOURCE_CONTAINER=$(buildah from $SOURCE_IMAGE)
echo "源容器: $SOURCE_CONTAINER"

echo "=== 步骤 2: 查找 JDK 和 Maven 路径 ==="
JAVA_HOME=$(buildah run $SOURCE_CONTAINER -- sh -c 'echo $JAVA_HOME')
MAVEN_HOME=$(buildah run $SOURCE_CONTAINER -- sh -c 'echo $MAVEN_HOME || echo /usr/share/maven')
echo "JAVA_HOME: $JAVA_HOME"
echo "MAVEN_HOME: $MAVEN_HOME"

echo "=== 步骤 3: 创建临时目录并提取文件 ==="
TEMP_DIR=$(mktemp -d)
buildah copy --from $SOURCE_CONTAINER $TEMP_DIR/jdk $JAVA_HOME
buildah copy --from $SOURCE_CONTAINER $TEMP_DIR/maven $MAVEN_HOME

echo "=== 步骤 4: 创建新镜像 ==="
NEW_CONTAINER=$(buildah from $TARGET_BASE_IMAGE)

# 安装依赖（Alpine）
buildah run $NEW_CONTAINER -- sh -c "apk add --no-cache bash curl"

# 复制文件
buildah copy $NEW_CONTAINER $TEMP_DIR/jdk $JAVA_HOME
buildah copy $NEW_CONTAINER $TEMP_DIR/maven $MAVEN_HOME

# 设置环境变量
buildah config --env JAVA_HOME=$JAVA_HOME $NEW_CONTAINER
buildah config --env MAVEN_HOME=$MAVEN_HOME $NEW_CONTAINER
buildah config --env PATH="$JAVA_HOME/bin:$MAVEN_HOME/bin:\$PATH" $NEW_CONTAINER

# 创建符号链接
buildah run $NEW_CONTAINER -- sh -c "ln -sf $JAVA_HOME/bin/java /usr/local/bin/java"
buildah run $NEW_CONTAINER -- sh -c "ln -sf $MAVEN_HOME/bin/mvn /usr/local/bin/mvn"

echo "=== 步骤 5: 验证安装 ==="
buildah run $NEW_CONTAINER -- java -version
buildah run $NEW_CONTAINER -- mvn --version

echo "=== 步骤 6: 提交镜像 ==="
buildah commit $NEW_CONTAINER $NEW_IMAGE_NAME

echo "=== 步骤 7: 清理 ==="
buildah rm $SOURCE_CONTAINER $NEW_CONTAINER
rm -rf $TEMP_DIR

echo "=== 完成！镜像 $NEW_IMAGE_NAME 已创建 ==="
```

### 使用新镜像

镜像创建完成后，可以像使用普通 Docker 镜像一样使用它：

```bash
# 查看镜像
buildah images | grep custom-maven-jdk

# 运行容器测试
buildah run $(buildah from custom-maven-jdk:17) -- java -version
buildah run $(buildah from custom-maven-jdk:17) -- mvn --version

# 导出为 Docker 格式（如果需要）
buildah push custom-maven-jdk:17 docker-daemon:custom-maven-jdk:17
```

### 注意事项

1. **路径差异**：不同基础镜像的 JDK 和 Maven 路径可能不同，需要根据实际情况调整
2. **依赖库**：Alpine 镜像使用 musl libc，可能需要安装 glibc 兼容层
3. **文件权限**：确保复制的文件具有正确的执行权限
4. **镜像大小**：复制完整的 JDK 和 Maven 会增加镜像大小，考虑使用多阶段构建优化
5. **环境变量**：确保正确设置 `JAVA_HOME`、`MAVEN_HOME` 和 `PATH`

### 替代方案：使用多阶段构建

如果使用 Dockerfile，可以使用多阶段构建更简洁地实现：

```dockerfile
# 阶段1：从 Maven 镜像提取文件
FROM maven:3.9-eclipse-temurin-17 AS maven-source
COPY --from=maven-source /usr/local/openjdk-17 /tmp/jdk
COPY --from=maven-source /usr/share/maven /tmp/maven

# 阶段2：构建最终镜像
FROM alpine:latest
RUN apk add --no-cache bash curl
COPY --from=maven-source /tmp/jdk /usr/local/openjdk-17
COPY --from=maven-source /tmp/maven /usr/share/maven
ENV JAVA_HOME=/usr/local/openjdk-17
ENV MAVEN_HOME=/usr/share/maven
ENV PATH="$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH"
RUN ln -sf $JAVA_HOME/bin/java /usr/local/bin/java && \
    ln -sf $MAVEN_HOME/bin/mvn /usr/local/bin/mvn
```

## 开发说明

### 运行应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

### 测试覆盖率

项目包含完整的单元测试，覆盖了Service层和Controller层的主要功能。建议在开发新功能时同步添加相应的测试用例。

### 代码规范

- 使用标准的Java命名规范
- 遵循Spring Boot最佳实践
- 保持代码注释清晰
- 确保单元测试覆盖主要业务逻辑

## 参考资源

- [Jib GitHub](https://github.com/GoogleContainerTools/jib)
- [Jib Maven Plugin 文档](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin)
- [Spring Boot 文档](https://spring.io/projects/spring-boot)
- [Spring Data JPA 文档](https://spring.io/projects/spring-data-jpa)
- [H2 Database 文档](https://www.h2database.com/html/main.html)

## 许可证

本项目仅用于示例和学习目的。


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


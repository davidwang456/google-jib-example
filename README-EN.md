# Jib Spring Boot Example Project

This is an example project that uses Jib to package a Spring Boot application into a Docker image, demonstrating a standard four-layer architecture (Entity, DAO, Service, Controller) and comprehensive unit tests.

## Project Structure

```
jibexample/
├── pom.xml                                    # Maven configuration file
├── README.md                                  # Project documentation
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── example/
    │   │           └── jibexample/
    │   │               ├── JibExampleApplication.java    # Spring Boot main class
    │   │               ├── entity/
    │   │               │   └── User.java                  # User entity class
    │   │               ├── dao/
    │   │               │   └── UserRepository.java        # Data access layer interface
    │   │               ├── service/
    │   │               │   └── UserService.java           # Business logic layer
    │   │               └── controller/
    │   │                   ├── HelloController.java       # Hello REST controller
    │   │                   └── UserController.java        # User management REST controller
    │   └── resources/
    │       └── application.properties                     # Application configuration
    └── test/
        └── java/
            └── com/
                └── example/
                    └── jibexample/
                        ├── service/
                        │   └── UserServiceTest.java      # Service layer unit tests
                        └── controller/
                            └── UserControllerTest.java    # Controller layer unit tests
```

## Features

- **Spring Boot 3.2.0** - Based on the latest Spring Boot framework
- **Four-Layer Architecture** - Standard Entity, DAO, Service, Controller layers
- **JPA Data Access** - Data persistence using Spring Data JPA
- **H2 In-Memory Database** - H2 database for development environment
- **RESTful API** - Complete user management API
- **Unit Tests** - Comprehensive test cases for Service and Controller layers
- **Health Check Endpoints** - Actuator health checks
- **Docker Image Packaging with Jib Maven Plugin**

## Technology Stack

- **JDK**: 17
- **Spring Boot**: 3.2.0
- **Spring Data JPA**: Data access layer
- **H2 Database**: In-memory database
- **JUnit 5**: Unit testing framework
- **Mockito**: Mock testing framework
- **Maven**: Project build tool

## Prerequisites

- JDK 17 or higher
- Maven 3.6 or higher
- Docker (optional, if you want to push images to local Docker daemon)

## Usage

### 1. Build the Project

```bash
mvn clean package
```

### 2. Build Docker Image with Jib

#### Method 1: Build and Load to Local Docker (Recommended)

Using default configuration:
```bash
mvn compile jib:dockerBuild
```

Using custom image configuration:
```bash
# Specify base image and target image
mvn compile jib:dockerBuild \
  -Djib.from.image=eclipse-temurin:17-jre-alpine \
  -Djib.to.image=my-custom-image:1.0.0

# Or override only the target image
mvn compile jib:dockerBuild -Djib.to.image=my-registry.com/my-app:v1.0.0
```

This will build the image locally and load it into the Docker daemon.

#### Method 2: Build Docker Tar File

Using default configuration:
```bash
mvn compile jib:buildTar
```

Using custom image configuration:
```bash
mvn compile jib:buildTar \
  -Djib.from.image=eclipse-temurin:17-jre-alpine \
  -Djib.to.image=my-app:latest
```

This generates a tar file: `target/jib-image.tar`, which can be loaded using:

```bash
docker load -i target/jib-image.tar
```

#### Method 3: Push Directly to Remote Registry

Use command-line parameters to specify the remote registry address:

```bash
# First, login to the image registry
docker login your-registry.com

# Build and push to remote registry
mvn compile jib:build \
  -Djib.from.image=eclipse-temurin:17-jre-alpine \
  -Djib.to.image=your-registry.com/your-username/jib-example:latest
```

**Note**: If command-line parameters are not specified, the default values defined in the `<properties>` section of `pom.xml` will be used:
- `jib.from.image` (default: `eclipse-temurin:17-jre-alpine`)
- `jib.to.image` (default: `jib-example:latest`)

### 3. Run Container

```bash
docker run -d -p 8080:8080 --name jib-example jib-example:latest
```

### 4. Run Unit Tests

```bash
mvn test
```

### 5. Test the Application

```bash
# Health check
curl http://localhost:8080/api/health

# Hello endpoint
curl http://localhost:8080/api/hello

# Create user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "name": "Test User"
  }'

# Get all users
curl http://localhost:8080/api/users

# Get user by ID
curl http://localhost:8080/api/users/1

# Get user by username
curl http://localhost:8080/api/users/username/testuser

# Update user
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "updateduser",
    "email": "updated@example.com",
    "name": "Updated User"
  }'

# Delete user
curl -X DELETE http://localhost:8080/api/users/1
```

## API Endpoints

### Hello Endpoints
- `GET /api/hello` - Returns welcome message and timestamp
- `GET /api/health` - Health check endpoint

### User Management Endpoints
- `POST /api/users` - Create user
- `GET /api/users` - Get all users list
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `PUT /api/users/{id}` - Update user information
- `DELETE /api/users/{id}` - Delete user

### Actuator Endpoints
- `GET /actuator/health` - Spring Boot Actuator health check
- `GET /h2-console` - H2 database console (development environment)

## Database Configuration

The project uses H2 in-memory database, configuration is in `application.properties`:

- **Database URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: empty
- **JPA Auto Update Schema**: `spring.jpa.hibernate.ddl-auto=update`

You can access the H2 console at `http://localhost:8080/h2-console` (development environment).

## Four-Layer Architecture

### 1. Entity Layer
- **Location**: `src/main/java/com/example/jibexample/entity/`
- **Description**: Defines data entity classes using JPA annotations for ORM mapping
- **Example**: `User.java` - User entity class

### 2. DAO Layer (Data Access Layer)
- **Location**: `src/main/java/com/example/jibexample/dao/`
- **Description**: Data access interfaces extending Spring Data JPA's `JpaRepository`
- **Example**: `UserRepository.java` - User data access interface

### 3. Service Layer (Business Logic Layer)
- **Location**: `src/main/java/com/example/jibexample/service/`
- **Description**: Business logic processing, including data validation, exception handling, etc.
- **Example**: `UserService.java` - User business logic service

### 4. Controller Layer
- **Location**: `src/main/java/com/example/jibexample/controller/`
- **Description**: REST API controllers handling HTTP requests and responses
- **Example**: `UserController.java` - User management REST controller

## Unit Tests

The project includes comprehensive unit test cases:

### Service Layer Tests
- **File**: `src/test/java/com/example/jibexample/service/UserServiceTest.java`
- **Test Coverage**:
  - User creation (success/failure scenarios)
  - User queries (by ID/username)
  - User updates
  - User deletion
  - Exception handling

### Controller Layer Tests
- **File**: `src/test/java/com/example/jibexample/controller/UserControllerTest.java`
- **Test Coverage**:
  - REST API endpoint tests
  - HTTP status code validation
  - JSON response format validation
  - Exception response handling

Run tests:
```bash
mvn test
```

## Jib Configuration

The Jib Maven plugin is configured in `pom.xml`, where base image and target image can be dynamically specified via command-line parameters:

### Default Configuration (defined in pom.xml properties)

- **Base Image**: `eclipse-temurin:17-jre-alpine` (lightweight JRE)
- **Target Image**: `jib-example:latest`
- **JVM Parameters**: `-Xms512m -Xmx512m`
- **Port**: `8080`
- **Format**: `docker`

### Override Image Configuration via Command-Line Parameters

You can override the default configuration using Maven properties when executing Jib commands:

```bash
# Override base image
mvn compile jib:dockerBuild -Djib.from.image=openjdk:17-jre-slim

# Override target image
mvn compile jib:dockerBuild -Djib.to.image=my-registry.com/my-app:v1.0.0

# Override both base and target images
mvn compile jib:dockerBuild \
  -Djib.from.image=eclipse-temurin:17-jre-alpine \
  -Djib.to.image=registry.example.com/jib-example:1.0.0
```

**Parameter Description**:
- `-Djib.from.image`: Specify base image (FROM image)
- `-Djib.to.image`: Specify target image name and tag

This approach makes it more flexible to use different image configurations across different environments (development, testing, production) without modifying the `pom.xml` file.

## Advantages of Jib

1. **No Dockerfile Required** - Jib automatically handles image building
2. **No Docker Daemon Required** - Can push directly to remote registries
3. **Layer Optimization** - Separates dependencies and code for faster builds
4. **Reproducible Builds** - Same input produces same output

## Copying JDK and Maven from Maven Image Using Buildah

Buildah is a tool for building OCI (Open Container Initiative) container images that can build images without requiring a Docker daemon. The following steps detail how to extract JDK and Maven from a Maven image and copy them to a new image built with buildah.

### Prerequisites

- Install buildah (Linux system or WSL)
- Understand basic concepts of container images

### Step 1: Preparation

First, identify the source Maven image and target base image:

```bash
# Source Maven image (contains JDK and Maven)
SOURCE_IMAGE="maven:3.9-eclipse-temurin-17"

# Target base image (can be any base image like alpine, ubuntu, etc.)
TARGET_BASE_IMAGE="alpine:latest"

# New image name
NEW_IMAGE_NAME="custom-maven-jdk:17"
```

### Step 2: Create Temporary Container and Extract Files

Use buildah to create a container from the source image and extract JDK and Maven:

```bash
# 1. Create container from source Maven image
SOURCE_CONTAINER=$(buildah from $SOURCE_IMAGE)

# 2. Create temporary directory to store extracted files
TEMP_DIR=$(mktemp -d)
echo "Temporary directory: $TEMP_DIR"

# 3. Copy JDK from source container (usually at /usr/local/openjdk-17 or /opt/java/openjdk)
buildah copy --from $SOURCE_CONTAINER $TEMP_DIR/jdk /usr/local/openjdk-17

# 4. Copy Maven from source container (usually at /usr/share/maven)
buildah copy --from $SOURCE_CONTAINER $TEMP_DIR/maven /usr/share/maven

# 5. Check copied files
ls -la $TEMP_DIR/jdk
ls -la $TEMP_DIR/maven
```

**Note**: JDK and Maven paths may vary by image. Common paths include:
- JDK: `/usr/local/openjdk-17`, `/opt/java/openjdk`, `/usr/lib/jvm/java-17-openjdk`
- Maven: `/usr/share/maven`, `/opt/maven`, `/usr/local/maven`

### Step 3: Find Actual Paths of JDK and Maven

If unsure about paths, you can first inspect the container:

```bash
# Enter source container to view file structure
buildah run $SOURCE_CONTAINER -- sh -c "echo 'JAVA_HOME:' && echo \$JAVA_HOME"
buildah run $SOURCE_CONTAINER -- sh -c "which java"
buildah run $SOURCE_CONTAINER -- sh -c "mvn --version"
buildah run $SOURCE_CONTAINER -- sh -c "ls -la /usr/local/ | grep -E 'openjdk|java'"
buildah run $SOURCE_CONTAINER -- sh -c "ls -la /usr/share/ | grep maven"
```

### Step 4: Create New Image and Copy Files

Use buildah to create a new image and copy JDK and Maven:

```bash
# 1. Create new container from target base image
NEW_CONTAINER=$(buildah from $TARGET_BASE_IMAGE)

# 2. Install necessary dependencies (Alpine needs glibc compatibility layer)
buildah run $NEW_CONTAINER -- sh -c "apk add --no-cache bash curl"

# 3. Copy JDK to new container
buildah copy $NEW_CONTAINER $TEMP_DIR/jdk /usr/local/openjdk-17

# 4. Copy Maven to new container
buildah copy $NEW_CONTAINER $TEMP_DIR/maven /usr/share/maven

# 5. Set environment variables
buildah config --env JAVA_HOME=/usr/local/openjdk-17 $NEW_CONTAINER
buildah config --env PATH="$JAVA_HOME/bin:/usr/share/maven/bin:$PATH" $NEW_CONTAINER
buildah config --env MAVEN_HOME=/usr/share/maven $NEW_CONTAINER

# 6. Create symbolic links (if needed)
buildah run $NEW_CONTAINER -- sh -c "ln -s /usr/local/openjdk-17/bin/java /usr/local/bin/java || true"
buildah run $NEW_CONTAINER -- sh -c "ln -s /usr/share/maven/bin/mvn /usr/local/bin/mvn || true"

# 7. Verify installation
buildah run $NEW_CONTAINER -- java -version
buildah run $NEW_CONTAINER -- mvn --version
```

### Step 5: Commit Image

Commit the container as an image:

```bash
# Commit as new image
buildah commit $NEW_CONTAINER $NEW_IMAGE_NAME

# Clean up temporary containers
buildah rm $SOURCE_CONTAINER $NEW_CONTAINER

# Clean up temporary directory
rm -rf $TEMP_DIR
```

### Complete Script Example

The following is a complete script that automates the above process:

```bash
#!/bin/bash
set -e

# Configuration variables
SOURCE_IMAGE="maven:3.9-eclipse-temurin-17"
TARGET_BASE_IMAGE="alpine:latest"
NEW_IMAGE_NAME="custom-maven-jdk:17"

echo "=== Step 1: Create container from source image ==="
SOURCE_CONTAINER=$(buildah from $SOURCE_IMAGE)
echo "Source container: $SOURCE_CONTAINER"

echo "=== Step 2: Find JDK and Maven paths ==="
JAVA_HOME=$(buildah run $SOURCE_CONTAINER -- sh -c 'echo $JAVA_HOME')
MAVEN_HOME=$(buildah run $SOURCE_CONTAINER -- sh -c 'echo $MAVEN_HOME || echo /usr/share/maven')
echo "JAVA_HOME: $JAVA_HOME"
echo "MAVEN_HOME: $MAVEN_HOME"

echo "=== Step 3: Create temporary directory and extract files ==="
TEMP_DIR=$(mktemp -d)
buildah copy --from $SOURCE_CONTAINER $TEMP_DIR/jdk $JAVA_HOME
buildah copy --from $SOURCE_CONTAINER $TEMP_DIR/maven $MAVEN_HOME

echo "=== Step 4: Create new image ==="
NEW_CONTAINER=$(buildah from $TARGET_BASE_IMAGE)

# Install dependencies (Alpine)
buildah run $NEW_CONTAINER -- sh -c "apk add --no-cache bash curl"

# Copy files
buildah copy $NEW_CONTAINER $TEMP_DIR/jdk $JAVA_HOME
buildah copy $NEW_CONTAINER $TEMP_DIR/maven $MAVEN_HOME

# Set environment variables
buildah config --env JAVA_HOME=$JAVA_HOME $NEW_CONTAINER
buildah config --env MAVEN_HOME=$MAVEN_HOME $NEW_CONTAINER
buildah config --env PATH="$JAVA_HOME/bin:$MAVEN_HOME/bin:\$PATH" $NEW_CONTAINER

# Create symbolic links
buildah run $NEW_CONTAINER -- sh -c "ln -sf $JAVA_HOME/bin/java /usr/local/bin/java"
buildah run $NEW_CONTAINER -- sh -c "ln -sf $MAVEN_HOME/bin/mvn /usr/local/bin/mvn"

echo "=== Step 5: Verify installation ==="
buildah run $NEW_CONTAINER -- java -version
buildah run $NEW_CONTAINER -- mvn --version

echo "=== Step 6: Commit image ==="
buildah commit $NEW_CONTAINER $NEW_IMAGE_NAME

echo "=== Step 7: Cleanup ==="
buildah rm $SOURCE_CONTAINER $NEW_CONTAINER
rm -rf $TEMP_DIR

echo "=== Complete! Image $NEW_IMAGE_NAME has been created ==="
```

### Using the New Image

After the image is created, you can use it like a regular Docker image:

```bash
# View images
buildah images | grep custom-maven-jdk

# Run container test
buildah run $(buildah from custom-maven-jdk:17) -- java -version
buildah run $(buildah from custom-maven-jdk:17) -- mvn --version

# Export as Docker format (if needed)
buildah push custom-maven-jdk:17 docker-daemon:custom-maven-jdk:17
```

### Important Notes

1. **Path Differences**: JDK and Maven paths may differ across different base images and need to be adjusted accordingly
2. **Dependencies**: Alpine images use musl libc and may require installing glibc compatibility layer
3. **File Permissions**: Ensure copied files have correct execution permissions
4. **Image Size**: Copying complete JDK and Maven will increase image size; consider using multi-stage builds for optimization
5. **Environment Variables**: Ensure correct setup of `JAVA_HOME`, `MAVEN_HOME`, and `PATH`

### Alternative: Using Multi-Stage Build

If using Dockerfile, you can achieve this more concisely with multi-stage builds:

```dockerfile
# Stage 1: Extract files from Maven image
FROM maven:3.9-eclipse-temurin-17 AS maven-source
COPY --from=maven-source /usr/local/openjdk-17 /tmp/jdk
COPY --from=maven-source /usr/share/maven /tmp/maven

# Stage 2: Build final image
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

## Development Guide

### Running the Application

```bash
# Compile project
mvn clean compile

# Run application
mvn spring-boot:run
```

The application will start at `http://localhost:8080`.

### Test Coverage

The project includes comprehensive unit tests covering the main functionality of the Service and Controller layers. It is recommended to add corresponding test cases when developing new features.

### Code Standards

- Use standard Java naming conventions
- Follow Spring Boot best practices
- Keep code comments clear
- Ensure unit tests cover main business logic

## Reference Resources

- [Jib GitHub](https://github.com/GoogleContainerTools/jib)
- [Jib Maven Plugin Documentation](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [H2 Database Documentation](https://www.h2database.com/html/main.html)

## License

This project is for example and learning purposes only.

# Sử dụng image cơ sở chứa Maven và OpenJDK
FROM maven:3.8.4-openjdk-17-slim as build

# Đặt thư mục làm việc trong container
WORKDIR /app

# Sao chép pom.xml và tải các dependencies của ứng dụng
COPY pom.xml .

# Tải các dependencies
RUN mvn dependency:go-offline

# Sao chép mã nguồn vào container
COPY src /app/src

# Build ứng dụng Spring Boot (tạo file JAR)
RUN mvn clean package -DskipTests

# Sử dụng image chứa OpenJDK để chạy ứng dụng
FROM openjdk:17-jdk-alpine

# Sao chép file JAR từ container build sang container chạy
COPY --from=build /app/target/server-0.0.1-SNAPSHOT.jar /server.jar

# Expose cổng 8080
EXPOSE 8080

# Chạy ứng dụng Spring Boot
CMD ["java", "-jar", "/server.jar"]

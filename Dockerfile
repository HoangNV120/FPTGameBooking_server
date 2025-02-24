# Sử dụng hình ảnh OpenJDK 17
FROM openjdk:17-jdk-alpine

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép file JAR vào container
COPY ./target/server-0.0.1-SNAPSHOT.jar ./server.jar

# Expose cổng cho ứng dụng
EXPOSE 8080

# Chạy ứng dụng với profile 'prod'
CMD ["java", "-jar", "server.jar", "--spring.profiles.active=prod"]

# Sử dụng một hình ảnh cơ sở Java
FROM openjdk:17-jdk-alpine

# Sao chép tệp JAR của ứng dụng Spring Boot vào thư mục /app/service trong hệ thống tệp Docker
COPY ./target/server-0.0.1-SNAPSHOT.jar ./server.jar

# Expose cổng mạng cho ứng dụng Spring Boot (thay đổi số cổng cần thiết)    
# Khai báo rằng container sẽ lắng nghe kết nối đến cổng 8081
EXPOSE 8080

# Khởi chạy ứng dụng Spring Boot khi container được khởi động
CMD ["java", "-jar", "server.jar"]

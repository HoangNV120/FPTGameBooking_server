//package com.server.config.swagger;
//
//import com.server.constants.SwaggerConstant;
//import io.swagger.v3.oas.annotations.OpenAPIDefinition;
//import io.swagger.v3.oas.annotations.info.Contact;
//import io.swagger.v3.oas.annotations.info.Info;
//import io.swagger.v3.oas.annotations.info.License;
//import io.swagger.v3.oas.annotations.servers.Server;
//import org.springdoc.core.GroupedOpenApi;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@OpenAPIDefinition(
//        info = @Info(
//                title = SwaggerConstant.DEFAULT_SWAGGER_TITLE_INFO,
//                description = SwaggerConstant.DEFAULT_SWAGGER_DESCRIPTION_INFO,
//                license = @License(
//                        name = SwaggerConstant.DEFAULT_SWAGGER_LICENSE_INFO_NAME,
//                        url = SwaggerConstant.DEFAULT_SWAGGER_LICENSE_INFO_URL
//                ),
//                version = SwaggerConstant.DEFAULT_SWAGGER_VERSION_INFO,
//                termsOfService = SwaggerConstant.DEFAULT_SWAGGER_TERMS_OF_SERVICE_INFO,
//                contact = @Contact(
//                        name = SwaggerConstant.Contact.DEFAULT_SWAGGER_CONTACT_INFO_NAME,
//                        email = SwaggerConstant.Contact.DEFAULT_SWAGGER_CONTACT_INFO_EMAIL
//                )
//        ),
//        servers = {
//                @Server(
//                        description = SwaggerConstant.Service.DEFAULT_SWAGGER_SERVICE_DEV_DESCRIPTION,
//                        url = SwaggerConstant.Service.DEFAULT_SWAGGER_SERVICE_DEV_URL
//                ),
//                @Server(
//                        description = SwaggerConstant.Service.DEFAULT_SWAGGER_SERVICE_TEST_DESCRIPTION,
//                        url = SwaggerConstant.Service.DEFAULT_SWAGGER_SERVICE_TEST_URL
//                )
//        }
//)
//public class SwaggerConfig {
//
//    // Cấu hình Swagger UI để phục vụ cho các endpoint của API
//    @Bean
//    public GroupedOpenApi publicApi() {
//        return GroupedOpenApi
//                .builder()
//                .group("public") // Đặt tên nhóm API
//                .pathsToMatch("/api/**") // Cấu hình các đường dẫn cần được hiển thị trong Swagger
//                .build();
//    }
//}

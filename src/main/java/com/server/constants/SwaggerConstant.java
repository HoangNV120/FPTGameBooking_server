package com.server.constants;

public interface SwaggerConstant {

    public static final String DEFAULT_SWAGGER_TITLE_INFO = "Swagger API";
    public static final String DEFAULT_SWAGGER_DESCRIPTION_INFO = "Doing CRUD API";
    public static final String DEFAULT_SWAGGER_TERMS_OF_SERVICE_INFO = "Game-Bookings";
    public static final String DEFAULT_SWAGGER_LICENSE_INFO_NAME = "Apache License";
    public static final String DEFAULT_SWAGGER_LICENSE_INFO_URL = "http://localhost:9090";
    public static final String DEFAULT_SWAGGER_VERSION_INFO = "v1.0";

    public interface Service {
        public static final String DEFAULT_SWAGGER_SERVICE_DEV_DESCRIPTION = "DEV";
        public static final String DEFAULT_SWAGGER_SERVICE_DEV_URL = "http://localhost:8082";
        public static final String DEFAULT_SWAGGER_SERVICE_TEST_DESCRIPTION = "TEST";
        public static final String DEFAULT_SWAGGER_SERVICE_TEST_URL = "http://localhost:8082";
    }

    public interface Contact {
        public static final String DEFAULT_SWAGGER_CONTACT_INFO_NAME = "Group One";
        public static final String DEFAULT_SWAGGER_CONTACT_INFO_EMAIL = "admin@gmail.com";
    }

}


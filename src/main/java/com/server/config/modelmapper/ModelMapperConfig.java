package com.server.config.modelmapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Cho phép ánh xạ các thuộc tính từ lớp cha (AuditTable)
//        modelMapper.getConfiguration().setDeepCopyEnabled(true);
//        modelMapper.getConfiguration().setFieldMatchingEnabled(true);
//        modelMapper.getConfiguration().setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        return modelMapper;
    }
}

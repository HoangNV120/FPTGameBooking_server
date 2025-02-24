package com.server.config.modelmapper;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Cho phép ánh xạ các thuộc tính từ lớp cha (AuditTable)
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        Converter<LocalDateTime, LocalDateTime> localDateTimeConverter =
                (MappingContext<LocalDateTime, LocalDateTime> context) -> {
                    LocalDateTime source = context.getSource();
                    return source != null ? source.atZone(ZoneId.of("UTC"))
                            .withZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"))
                            .toLocalDateTime() : null;
                };

        modelMapper.addConverter(localDateTimeConverter);

        return modelMapper;
    }
}

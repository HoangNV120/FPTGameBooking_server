package com.server.util;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeToAsiaHoChiMinhSerializer extends JsonSerializer<LocalDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            ZonedDateTime zonedDateTime = value.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"));
            gen.writeString(zonedDateTime.format(FORMATTER));
        } else {
            gen.writeNull();
        }
    }
}


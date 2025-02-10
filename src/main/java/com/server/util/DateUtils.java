package com.server.util;

import com.server.exceptions.RestApiException;
import lombok.experimental.UtilityClass;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void validateDate(String date, int format) {
        DateFormat dateFormat = new SimpleDateFormat(getDateFormat(format));
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            throw new RestApiException("Sai định dạng ngày, yêu cầu định dạng " + getDateFormat(format));

        }
    }

    private static String getDateFormat(int index) {
        return switch (index) {
            case 1 -> "yyyy/MM/dd";
            default -> "yyyy-MM-dd";
        };
    }

    public static LocalDateTime convertToLocalDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        LocalDate localDate = LocalDate.parse(dateStr, DATE_FORMATTER);
        return localDate.atStartOfDay();
    }


}

package net.pagier.mealster.util;

import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResponseFormatter {

    private static final String ATTACHMENT_CONTENT_DISPOSITION = "attachment; filename=";
    private static final String DATE_FORMAT = "yyyy.MM.dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final String TIME_FORMAT = "HHmmssSSS";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    private static final String FILE_NAME_SUFFIX = "_%s_%s";
    private static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String XLSX_EXTENSION = ".xlsx";

    public static void formatResponseForXlsx(HttpServletResponse response, String filenamePrefix) {
        response.addHeader(HttpHeaders.CONTENT_TYPE, XLSX_CONTENT_TYPE);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, createContentDisposition(filenamePrefix));
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }

    private static String createContentDisposition(String filenamePrefix) {
        String fileName = createFileName(filenamePrefix);
        return ATTACHMENT_CONTENT_DISPOSITION + fileName + ResponseFormatter.XLSX_EXTENSION;
    }

    private static String createFileName(String fileName) {
        LocalDateTime localDateTime = LocalDateTime.now();
        String datePart = localDateTime.format(DATE_FORMATTER);
        String timePart = localDateTime.format(TIME_FORMATTER);
        return String.format(fileName + FILE_NAME_SUFFIX, datePart, timePart);
    }
}

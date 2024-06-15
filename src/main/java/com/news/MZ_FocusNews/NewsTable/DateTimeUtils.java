package com.news.MZ_FocusNews.NewsTable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// 크롤링한 뉴스 날짜 가독성 좋게 변환
public class DateTimeUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    public static LocalDateTime parseDate(String dateStr) {
        return LocalDateTime.parse(dateStr, formatter);
    }
}

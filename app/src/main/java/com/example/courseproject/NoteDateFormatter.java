package com.example.courseproject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class NoteDateFormatter {

    private static final SimpleDateFormat RAW_FORMAT =
            new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

    private static final SimpleDateFormat DISPLAY_FORMAT =
            new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);

    private NoteDateFormatter() {
    }

    public static String forDisplay(String rawDate) {
        if (rawDate == null || rawDate.isEmpty()) {
            return "";
        }
        try {
            Date parsed = RAW_FORMAT.parse(rawDate);
            return parsed != null ? DISPLAY_FORMAT.format(parsed) : rawDate;
        } catch (ParseException e) {
            return rawDate;
        }
    }
}
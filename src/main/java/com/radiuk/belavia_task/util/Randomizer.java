package com.radiuk.belavia_task.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Randomizer {

    private static final Random RANDOM = new Random();
    public static final String LATIN_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String CYRILLIC_LETTERS = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static LocalDate getRandomDate(int dateYearsBack) {
        LocalDate now = LocalDate.now();
        LocalDate yearsAgo = now.minusYears(dateYearsBack);

        long start = yearsAgo.toEpochDay();
        long end = now.toEpochDay();

        return LocalDate.ofEpochDay(RANDOM.nextLong(start, end + 1));
    }

    public static String getRandomString(String charSet, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(charSet.length());
            sb.append(charSet.charAt(index));
        }
        return sb.toString();
    }

    public static int getRandomEvenInt(int min, int max) {
        return RANDOM.nextInt((min + 1) / 2, (max + 1) / 2) * 2;
    }

    public static BigDecimal getRandomDecimal(int min, int max, int scale) {
        BigDecimal result = BigDecimal.valueOf(RANDOM.nextDouble() * (max - min) + min);
        return result.setScale(scale, RoundingMode.HALF_UP);
    }
}

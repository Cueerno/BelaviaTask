package com.radiuk.belavia_task.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
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
        return RANDOM.nextInt(min, (max + 1) / 2) * 2;
    }

    public static String getRandomDecimal(int min, int max, int scale) {
        double d = min + RANDOM.nextDouble() * (max - min);

        String format = "%." + scale + "f";
        return String.format(Locale.ROOT, format , d);
    }
}

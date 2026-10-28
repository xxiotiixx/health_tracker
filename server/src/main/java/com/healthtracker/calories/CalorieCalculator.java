package com.healthtracker.calories;

import java.util.List;

public final class CalorieCalculator {

    public static final double MIN_WEIGHT_KG = 20.0;
    public static final double MAX_WEIGHT_KG = 500.0;
    public static final double MIN_HEIGHT_CM = 50.0;
    public static final double MAX_HEIGHT_CM = 250.0;
    public static final int MIN_AGE = 10;
    public static final int MAX_AGE = 120;
    public static final int MAX_ENTRY_CALORIES = 20000;

    private CalorieCalculator() {
    }
    public static int calculateBmr(Gender gender, double weightKg, double heightCm, int age) {
        return (int) Math.round(rawBmr(gender, weightKg, heightCm, age));
    }
    public static int calculateDailyNorm(Gender gender, double weightKg, double heightCm, int age, ActivityLevel activity) {
        if (activity == null) {
            throw new IllegalArgumentException("Рівень активності не вказано");
        }
        double norm = rawBmr(gender, weightKg, heightCm, age) * activity.getFactor();
        return (int) Math.round(norm);
    }
    public static int totalForDay(List<Integer> entries) {
        if (entries == null) {
            throw new IllegalArgumentException("Список записів не вказано");
        }
        int total = 0;
        for (Integer calories : entries) {
            if (calories == null || calories < 0 || calories > MAX_ENTRY_CALORIES) {
                throw new IllegalArgumentException(
                        "Калорії запису мають бути в межах 0-" + MAX_ENTRY_CALORIES);
            }
            total += calories;
        }
        return total;
    }
    public static int remaining(int dailyNorm, int consumed) {
        if (dailyNorm < 0 || consumed < 0) {
            throw new IllegalArgumentException("Значення не можуть бути від'ємними");
        }
        return dailyNorm - consumed;
    }
    private static double rawBmr(Gender gender, double weightKg, double heightCm, int age) {
        validate(gender, weightKg, heightCm, age);
        double base = 10.0 * weightKg + 6.25 * heightCm - 5.0 * age;
        return gender == Gender.MALE ? base + 5.0 : base - 161.0;
    }
    private static void validate(Gender gender, double weightKg, double heightCm, int age) {
        if (gender == null) {
            throw new IllegalArgumentException("Стать не вказано");
        }
        if (Double.isNaN(weightKg) || weightKg < MIN_WEIGHT_KG || weightKg > MAX_WEIGHT_KG) {
            throw new IllegalArgumentException(
                    "Вага має бути в межах " + MIN_WEIGHT_KG + "-" + MAX_WEIGHT_KG + " кг");
        }
        if (Double.isNaN(heightCm) || heightCm < MIN_HEIGHT_CM || heightCm > MAX_HEIGHT_CM) {
            throw new IllegalArgumentException(
                    "Зріст має бути в межах " + MIN_HEIGHT_CM + "-" + MAX_HEIGHT_CM + " см");
        }
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new IllegalArgumentException(
                    "Вік має бути в межах " + MIN_AGE + "-" + MAX_AGE + " років");
        }
    }
}
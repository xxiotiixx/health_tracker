package com.healthtracker.profile;

import com.healthtracker.bmi.BmiCalculator;
import com.healthtracker.calories.ActivityLevel;
import com.healthtracker.calories.CalorieCalculator;
import com.healthtracker.calories.Gender;

import java.time.LocalDate;
import java.time.Period;

public final class ProfileValidator {
    private ProfileValidator() {
    }
    public static int validate(double heightCm, double weightKg, LocalDate birthDate,
                               Gender gender, ActivityLevel activity, LocalDate today) {
        if (Double.isNaN(heightCm) || heightCm < BmiCalculator.MIN_HEIGHT_CM
                || heightCm > BmiCalculator.MAX_HEIGHT_CM) {
            throw new IllegalArgumentException("Некоректний зріст");
        }
        if (Double.isNaN(weightKg) || weightKg < BmiCalculator.MIN_WEIGHT_KG
                || weightKg > BmiCalculator.MAX_WEIGHT_KG) {
            throw new IllegalArgumentException("Некоректна вага");
        }
        if (gender == null) {
            throw new IllegalArgumentException("Стать не вказано");
        }
        if (activity == null) {
            throw new IllegalArgumentException("Рівень активності не вказано");
        }
        if (birthDate == null || today == null) {
            throw new IllegalArgumentException("Дату народження не вказано");
        }
        if (birthDate.isAfter(today)) {
            throw new IllegalArgumentException("Дата народження в майбутньому");
        }
        int age = Period.between(birthDate, today).getYears();
        if (age < CalorieCalculator.MIN_AGE || age > CalorieCalculator.MAX_AGE) {
            throw new IllegalArgumentException(
                    "Вік має бути в межах " + CalorieCalculator.MIN_AGE + "-"
                            + CalorieCalculator.MAX_AGE + " років");
        }
        return age;
    }
}
package com.healthtracker.profile;

import com.healthtracker.calories.ActivityLevel;
import com.healthtracker.calories.Gender;
import java.time.LocalDate;
public record Profile(long userId, double heightCm, double weightKg,
                      LocalDate birthDate, Gender gender, ActivityLevel activity) {
}
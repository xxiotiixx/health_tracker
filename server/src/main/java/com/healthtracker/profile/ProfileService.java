package com.healthtracker.profile;

import com.healthtracker.calories.ActivityLevel;
import com.healthtracker.calories.Gender;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;
public class ProfileService {
    private final ProfileRepository profiles;
    private final Clock clock;
    public ProfileService(ProfileRepository profiles, Clock clock) {
        this.profiles = profiles;
        this.clock = clock;
    }
    public Profile save(long userId, double heightCm, double weightKg,
                        LocalDate birthDate, Gender gender, ActivityLevel activity) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Некоректний ідентифікатор користувача");
        }
        ProfileValidator.validate(heightCm, weightKg, birthDate, gender, activity,
                LocalDate.now(clock));
        Profile profile = new Profile(userId, heightCm, weightKg, birthDate, gender, activity);
        profiles.save(profile);
        return profile;
    }
    public Optional<Profile> get(long userId) {
        return profiles.findByUserId(userId);
    }
}
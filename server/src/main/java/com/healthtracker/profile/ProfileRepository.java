package com.healthtracker.profile;
import java.util.Optional;
public interface ProfileRepository {
    void save(Profile profile);
    Optional<Profile> findByUserId(long userId);
}
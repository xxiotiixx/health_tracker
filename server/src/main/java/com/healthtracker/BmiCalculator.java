package com.healthtracker;

public class BmiCalculator {

    public static double calculate(double weightKg, double heightCm) {
        if (weightKg <= 0 || heightCm <= 0) {
            throw new IllegalArgumentException("Weight and height must be positive");
        }
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }
}
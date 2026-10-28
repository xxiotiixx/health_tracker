package com.healthtracker.bmi;

public final class BmiCalculator {

    public static final double MIN_WEIGHT_KG = 20.0;
    public static final double MAX_WEIGHT_KG = 500.0;
    public static final double MIN_HEIGHT_CM = 50.0;
    public static final double MAX_HEIGHT_CM = 250.0;

    private BmiCalculator() {
    }

    public static double calculate(double weightKg, double heightCm) {
        validate(weightKg, heightCm);
        double heightM = heightCm / 100.0;
        double bmi = weightKg / (heightM * heightM);
        return Math.round(bmi * 10.0) / 10.0;
    }

    public static BmiCategory classify(double bmi) {
        if (bmi < 18.5) {
            return BmiCategory.UNDERWEIGHT;
        } else if (bmi < 25.0) {
            return BmiCategory.NORMAL;
        } else if (bmi < 30.0) {
            return BmiCategory.OVERWEIGHT;
        }
        return BmiCategory.OBESE;
    }

    private static void validate(double weightKg, double heightCm) {
        if (Double.isNaN(weightKg) || weightKg < MIN_WEIGHT_KG || weightKg > MAX_WEIGHT_KG) {
            throw new IllegalArgumentException(
                    "Вага має бути в межах " + MIN_WEIGHT_KG + "-" + MAX_WEIGHT_KG + " кг");
        }
        if (Double.isNaN(heightCm) || heightCm < MIN_HEIGHT_CM || heightCm > MAX_HEIGHT_CM) {
            throw new IllegalArgumentException(
                    "Зріст має бути в межах " + MIN_HEIGHT_CM + "-" + MAX_HEIGHT_CM + " см");
        }
    }
}
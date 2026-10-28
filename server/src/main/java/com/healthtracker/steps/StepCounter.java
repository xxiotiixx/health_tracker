package com.healthtracker.steps;

import java.util.List;

public final class StepCounter {

    public static final int DEFAULT_GOAL = 10000;
    public static final int MIN_GOAL = 1;
    public static final int MAX_GOAL = 100000;
    public static final int MAX_ENTRY_STEPS = 100000;

    private StepCounter() {
    }
    public static int totalForDay(List<Integer> entries) {
        if (entries == null) {
            throw new IllegalArgumentException("Список записів не вказано");
        }
        int total = 0;
        for (Integer steps : entries) {
            if (steps == null || steps < 0 || steps > MAX_ENTRY_STEPS) {
                throw new IllegalArgumentException(
                        "Кількість кроків запису має бути в межах 0-" + MAX_ENTRY_STEPS);
            }
            total += steps;
        }
        return total;
    }
    public static int progressPercent(int steps, int goal) {
        validate(steps, goal);
        double percent = steps * 100.0 / goal;
        return (int) Math.min(100, Math.round(percent));
    }
    public static boolean isGoalReached(int steps, int goal) {
        validate(steps, goal);
        return steps >= goal;
    }
    public static int stepsToGoal(int steps, int goal) {
        validate(steps, goal);
        return Math.max(0, goal - steps);
    }
    public static int averagePerDay(List<Integer> dailyTotals) {
        if (dailyTotals == null) {
            throw new IllegalArgumentException("Список денних сум не вказано");
        }
        if (dailyTotals.isEmpty()) {
            return 0;
        }
        long sum = 0;
        for (Integer total : dailyTotals) {
            if (total == null || total < 0) {
                throw new IllegalArgumentException("Денна сума має бути невід'ємною");
            }
            sum += total;
        }
        return (int) Math.round((double) sum / dailyTotals.size());
    }

    private static void validate(int steps, int goal) {
        if (steps < 0) {
            throw new IllegalArgumentException("Кількість кроків не може бути від'ємною");
        }
        if (goal < MIN_GOAL || goal > MAX_GOAL) {
            throw new IllegalArgumentException(
                    "Ціль має бути в межах " + MIN_GOAL + "-" + MAX_GOAL + " кроків");
        }
    }
}
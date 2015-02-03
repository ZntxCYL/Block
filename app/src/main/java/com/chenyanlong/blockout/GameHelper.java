package com.chenyanlong.blockout;

public class GameHelper {
    private static int currentLevel = 0;

    public static void setCurrentLevel(int level) {
        currentLevel = level;
    }

    public static int getCurrentLevel() {
        return currentLevel;
    }

    public static int getPassScore() {
        int passScore = 1000;
        return currentLevel * passScore;
    }

    public static String getSaveScoreKey() {
        return getSaveScoreKey(currentLevel);
    }

    public static String getSaveScoreKey(int level) {
        return String.format("high_score_for_level_%d", level);
    }

    public static String getSavePassKey(int level) {
        return String.format("pass_for_level_%d", level);
    }

    public static String getIsFirstPlayKey() {
        return "is_first_play_key";
    }

    public static void nextLevel() {
        ++currentLevel;
    }

    public static int getNextLevel() {
        return currentLevel + 1;
    }
}
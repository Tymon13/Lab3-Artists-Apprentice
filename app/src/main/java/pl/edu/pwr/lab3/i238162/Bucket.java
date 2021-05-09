package pl.edu.pwr.lab3.i238162;

import android.util.Log;

public class Bucket {
    private double gainPerSecond;
    private double currentLevel;
    private double maxLevel;
    private double currentModifier;

    Bucket(double gainPerSecond, double currentLevel, double maxLevel) {
        this.gainPerSecond = gainPerSecond;
        this.currentLevel = currentLevel;
        this.maxLevel = maxLevel;
    }

    public void updateTick(long timeSpentInMs, double modifier) {
        currentModifier = modifier;
        double timeSpentInS = timeSpentInMs / 1000.0;
        double gain = gainPerSecond * timeSpentInS * currentModifier;
        currentLevel = Math.min(maxLevel, currentLevel + gain);
    }

    public double getCurrentLevel() {
        return currentLevel;
    }

    public double getMaxLevel() {
        return maxLevel;
    }

    public double getFillFraction() {
        return currentLevel / maxLevel;
    }

    public double getGainPerSecond() {
        return  gainPerSecond * currentModifier;
    }
}

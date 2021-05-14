package pl.edu.pwr.lab3.i238162;

public class Bucket {
    protected double gainPerSecond;
    protected double currentLevel;
    protected double maxLevel;
    protected double currentModifier = 0.0;

    Bucket(double gainPerSecond, double currentLevel, double maxLevel) {
        this.gainPerSecond = gainPerSecond;
        this.currentLevel = currentLevel;
        this.maxLevel = maxLevel;
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
        return gainPerSecond * currentModifier;
    }
}

package pl.edu.pwr.lab3.i238162;

public class PlayerBucket extends Bucket {
    PlayerBucket(double gainPerSecond, double currentLevel, double maxLevel) {
        super(gainPerSecond, currentLevel, maxLevel);
    }

    public void updateTick(long timeSpentInMs, double modifier) {
        currentModifier = modifier;
        double timeSpentInS = timeSpentInMs / 1000.0;
        double gain = gainPerSecond * timeSpentInS * currentModifier;
        currentLevel = Math.min(maxLevel, currentLevel + gain);
    }
}

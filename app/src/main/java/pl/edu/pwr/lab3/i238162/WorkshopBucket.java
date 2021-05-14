package pl.edu.pwr.lab3.i238162;

public class WorkshopBucket extends Bucket {
    WorkshopBucket(double gainPerSecond, double currentLevel, double maxLevel) {
        super(gainPerSecond, currentLevel, maxLevel);
    }

    public double extractPerTick(long timeSpentInMs, double maxExtracted) {
        double timeSpentInS = timeSpentInMs / 1000.0;
        double maxExtractInTick = gainPerSecond * timeSpentInS;
        return Math.max(maxExtracted, maxExtractInTick);
    }

    public void fillFrom(Bucket otherBucket) {
        double amountToTransfer = Math.min(getRemainingSpace(), otherBucket.getCurrentLevel());
        fillFrom(otherBucket, amountToTransfer);
    }

    private void fillFrom(Bucket otherBucket, double amount) {
        otherBucket.remove(amount);
        this.add(amount);
    }
}
